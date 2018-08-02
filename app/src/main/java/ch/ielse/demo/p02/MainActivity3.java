package ch.ielse.demo.p02;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.ielse.imagewatcher.ImageWatcher;
import com.github.ielse.imagewatcher.ImageWatcherHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity3 extends Activity {

    private ImageWatcher vImageWatcher;

    final String picThumbUrl = "http://img.my.csdn.net/uploads/201707/27/1501118720_9504.jpg";
    final String picUrl = "http://img.my.csdn.net/uploads/201707/27/1501118577_9169.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isTranslucentStatus = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            isTranslucentStatus = true;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        vImageWatcher = ImageWatcherHelper.with(this) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(this) : 0) // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
                .setErrorImageRes(R.mipmap.error_picture) // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API
                .setLoader(new GlideImageWatcherLoader())
                .create();

        Utils.fitsSystemWindows(isTranslucentStatus, findViewById(R.id.v_fit));

        ImageView iPicture = (ImageView) findViewById(R.id.i_picture);

        Glide.with(iPicture.getContext()).asBitmap().load(picThumbUrl).into(iPicture);

        iPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(0, (ImageView) v);
                vImageWatcher.show((ImageView) v, mappingViews, Collections.singletonList(Uri.parse(picUrl)));
            }
        });


        Glide.with(iPicture.getContext()).asBitmap().load("http://img.zcool.cn/community/01ee43596f0ce9a8012193a38dcb00.jpg")
                .into((ImageView) findViewById(R.id.vLongPicture));
        findViewById(R.id.vLongPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> longPictureList = new ArrayList<>();
                longPictureList.add("http://img.zcool.cn/community/01ee43596f0ce9a8012193a38dcb00.jpg");
                longPictureList.add("https://img3.duitang.com/uploads/item/201608/03/20160803170546_BwshQ.thumb.224_0.jpeg");
                longPictureList.add("http://img4.duitang.com/uploads/item/201601/21/20160121150507_AkrQx.thumb.224_0.jpeg");
                longPictureList.add("http://img.zcool.cn/community/019971579860ae0000012e7e1b5c10.gif");
                longPictureList.add("http://img.zcool.cn/community/010d6b58ae3d24a801219c772d8b7a.JPG@1280w_1l_2o_100sh.jpg");
                longPictureList.add("http://img.hb.aicdn.com/4fbe16f37f71006636ca171297d409b2cf75b92e263ed3-7VaWyo_fw658");
                longPictureList.add("http://img.hb.aicdn.com/8ff2cf3de3e86c1e32040463b073441180af0b975d3b26-pCY4P9_fw658");
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(0, (ImageView) v);
                vImageWatcher.show((ImageView) v, mappingViews, convert(longPictureList));
            }
        });
    }

    private List<Uri> convert(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }


    @Override
    public void onBackPressed() {
        if (!vImageWatcher.handleBackPressed()) {
            super.onBackPressed();
        }
    }
}
