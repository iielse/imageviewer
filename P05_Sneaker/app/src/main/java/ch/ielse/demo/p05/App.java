package ch.ielse.demo.p05;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;


import ch.ielse.demo.p05.api.interceptor.AddCookiesInterceptor;
import ch.ielse.demo.p05.api.interceptor.AddDeviceInfoInterceptor;
import ch.ielse.demo.p05.api.interceptor.ReceivedCookiesInterceptor;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.QueryBuilder;

import ch.ielse.demo.p05.db.DaoMaster;
import ch.ielse.demo.p05.db.DaoSession;
import ch.ielse.demo.p05.db.PersonInfo;
import ch.ielse.demo.p05.db.PersonInfoDao;
import ch.ielse.demo.p05.db.upgrade.UpgradeableOpenHelper;
import ch.ielse.demo.p05.view.Sneaker;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class App extends Application {

    private static App INSTANCE;
    private ApiService apiService;
    private Gson gson;
    private UpgradeableOpenHelper dbHelper;
    private DaoSession daoSession;
    private int currentDBVersion;
    private PersonInfo currentUser;

    public boolean debug;

    public static synchronized App get() {
        return INSTANCE;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        loadApplicationBasicConfig();
        registerActivityLifecycleCallbacks1();
    }

    public ApiService getApiService() {
        if (apiService == null) {
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(new HttpLoggingInterceptor().setLevel(Const.INSTANCE.getDEBUG() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE));
            client.addInterceptor(new AddDeviceInfoInterceptor());
            client.addInterceptor(new AddCookiesInterceptor());
            client.addInterceptor(new ReceivedCookiesInterceptor());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Const.INSTANCE.getServerUrl())
                    .client(client.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public PersonInfo getCurrentUser() {
        if (currentUser == null) {
            String lastUserId = Const.INSTANCE.spReadString("account", "last_user_id", "");
            if (!TextUtils.isEmpty(lastUserId)) {
                PersonInfoDao personInfoDao = getDaoSession(lastUserId).getPersonInfoDao();
                currentUser = personInfoDao.queryBuilder().where(PersonInfoDao.Properties.UserId.eq(lastUserId)).unique();
            }
        }
        return currentUser;
    }

    public DaoSession getDaoSession() {
        getCurrentUser();
        return getDaoSession(currentUser.getUserId());
    }

    private DaoSession getDaoSession(String key) {
        if (daoSession != null) daoSession.clear();

        if (dbHelper != null && (currentUser == null || !TextUtils.equals(currentUser.getUserId(), key)
                || currentDBVersion < DaoMaster.SCHEMA_VERSION)) {
            dbHelper.close();
            dbHelper = null;
        }
        if (dbHelper == null) {
            // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
            // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
            // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
            // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
            dbHelper = new UpgradeableOpenHelper(this, "xx" + key);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
            daoSession = new DaoMaster(db).newSession();
            currentDBVersion = DaoMaster.SCHEMA_VERSION;
            // 在 QueryBuilder 类中内置两个 Flag 用于方便输出执行的 SQL 语句与传递参数的值
            QueryBuilder.LOG_SQL = debug;
            QueryBuilder.LOG_VALUES = debug;
        }
        return daoSession;
    }

    public Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private void loadApplicationBasicConfig() {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            debug = applicationInfo.metaData.getBoolean("BUGLY_ENABLE_DEBUG");
            Const.INSTANCE.logd("loadApplicationBasicConfig debug " + debug);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Sneaker.setDisplayConfigs(getApplicationContext(),
                R.dimen.edge, R.color.normal, R.dimen.normal, R.dimen.title_height);
    }


    private void registerActivityLifecycleCallbacks1() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Const.INSTANCE.logd("LifecycleCallbacks onActivityCreated [%s] ", activity.getClass().getSimpleName());
                EventBus.getDefault().register(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Const.INSTANCE.logd("LifecycleCallbacks onActivityDestroyed [%s] ", activity.getClass().getSimpleName());
                EventBus.getDefault().unregister(activity);
                if (activity instanceof ObserverLifecycleHolder) {
                    ((ObserverLifecycleHolder) activity).unregisterAll();
                }
            }
        });
    }
}

