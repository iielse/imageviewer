package ch.ielse.demo.p05.api;


import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.ielse.demo.p05.App;
import ch.ielse.demo.p05.Const;
import ch.ielse.demo.p05.ObserverLifecycleHolder;
import ch.ielse.demo.p05.business.BalanceJournalInfo;
import ch.ielse.demo.p05.db.PersonInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class ApiManager {

    private ApiManager() {
        throw new IllegalStateException("No instances!");
    }

    public static void loginTest(ObserverLifecycleHolder holder, final String username, String password, String userType, final SimpleObserver<PersonInfo, PersonInfo> simpleObserver) {
        Observable.timer(2, TimeUnit.SECONDS)
                .flatMap(new Function<Long, Observable<PersonInfo>>() {
                    @Override
                    public Observable<PersonInfo> apply(@NonNull Long aLong) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<PersonInfo>() {
                            @Override
                            public void subscribe(@NonNull ObservableEmitter<PersonInfo> e) throws Exception {
                                PersonInfo test = new PersonInfo(){
                                    int err = ApiEntity.ERR_OK;

                                    @Override
                                    protected Object extraReal() throws Exception {
                                        return this;
                                    }
                                };
                                test.setPlatformId("18812345678");
                                test.setUserId("9527");

                                e.onNext(test);
                                e.onComplete();
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleObserver.attach(holder));
    }

    public static void login(ObserverLifecycleHolder holder, final String username, String password, String userType, final SimpleObserver<PersonInfo, PersonInfo> simpleObserver) {
        App.get().getApiService().login("1", "1", username, password, userType)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Function<PersonInfo, Observable<PersonInfo>>() {
                    @Override
                    public Observable<PersonInfo> apply(@NonNull PersonInfo personInfo) throws Exception {
                        return internalInitializeEasemob(personInfo);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleObserver.attach(holder));
    }

    /**
     * 登录或注册后的用户，进行环信环境初始化
     *
     * @param personInfo 用户信息
     */
    private static Observable<PersonInfo> internalInitializeEasemob(final PersonInfo personInfo) {
        return Observable.create(new ObservableOnSubscribe<PersonInfo>() {
            @Override
            public void subscribe(final @NonNull ObservableEmitter<PersonInfo> e) throws Exception {
//                EMClient.getInstance().login(personInfo.getPlatformId(), "111111", new EMCallBack() {//回调
//                    @Override
//                    public void onSuccess() {
//                        EMClient.getInstance().groupManager().loadAllGroups();
//                        EMClient.getInstance().chatManager().loadAllConversations();
//                        Const.INSTANCE.logd("登录聊天服务器成功！" + personInfo.getPlatformId());
//                        e.onNext(personInfo);
//                        e.onComplete();
//                    }
//
//                    @Override
//                    public void onProgress(int progress, String status) {
//                    }
//
//                    @Override
//                    public void onError(int code, String message) {
//                        Const.INSTANCE.logd("登录聊天服务器失败！" + personInfo.getPlatformId());
//                        e.onError(new RuntimeException("internalInitializeEasemob onError[" + code + "]" + message));
//                    }
//                });
            }
        });
    }


    public static void queryPersonDetailByIds(ObserverLifecycleHolder holder, SimpleObserver<String, String> simpleObserver) {
        String userIds = "[1005002]";

        App.get().getApiService().queryPersonDetailByIds(userIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleObserver.attach(holder));
    }

    public static void queryBalanceJournal(ObserverLifecycleHolder holder, int startIndex, int num, SimpleObserver<List<BalanceJournalInfo>, BalanceJournalInfo> simpleObserver) {
        App.get().getApiService().queryBalanceJournal(startIndex, num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleObserver.attach(holder));
    }
}
