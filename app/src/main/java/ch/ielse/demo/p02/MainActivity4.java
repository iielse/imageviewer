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
import com.github.ielse.imagewatcher.ImageWatcherHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity4 extends Activity {
    private List<Uri> dataList = new ArrayList<>();

    private ImageWatcherHelper iwHelper;

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
        setContentView(R.layout.activity_main4);

        String u1 = "http://img.hb.aicdn.com/adf79cb0385bfe98456fea7768b12a5ca4eca0b8c81b-Tp0ywW_fw658";
        String u2 = "http://img.hb.aicdn.com/df75487fbc3ad89bb0c42eee053c3fd2fdc6b18a12b99-qYfZrA_fw658";
        String u3 = "http://img.hb.aicdn.com/f2fb872825d780bbe7eb1ca6696454719720f8f0a61a-1VaMd9_fw658";
        String u4 = "http://img.hb.aicdn.com/2982f37c8c149a1a9b9e236e45b7e1eabd4b168710c0c-cyspHw_fw658";
        String u5 = "http://img.hb.aicdn.com/e2c234cf4b8dc718459403b8f2cc8b5bb637bc213f74c-YYwZ0R_fw658";
        String u6 = "http://img.hb.aicdn.com/e1c5522de00aae08819d127b44b5dd943f6359fa121f7-EtF0yY_fw658";
        String u7 = "http://img.hb.aicdn.com/13bbb585cd889fe41d39e8da1eac185143a6a3123ae2-91zlEN_fw658";
        String u8 = "http://img.hb.aicdn.com/c2ea3454413ce9353e002749e5425fcf550f2bf7bb0c-yLD00Z_fw658";
        dataList.add(Uri.parse(u1));
        dataList.add(Uri.parse(u2));
        dataList.add(Uri.parse(u3));
        dataList.add(Uri.parse(u4));
        dataList.add(Uri.parse(u5));
        dataList.add(Uri.parse(u6));
        dataList.add(Uri.parse(u7));
        dataList.add(Uri.parse(u8));

        Glide.with(this).load(u1).into(((ImageView) findViewById(R.id.v1)));
        Glide.with(this).load(u2).into(((ImageView) findViewById(R.id.v2)));
        Glide.with(this).load(u3).into(((ImageView) findViewById(R.id.v3)));
        Glide.with(this).load(u4).into(((ImageView) findViewById(R.id.v4)));
        Glide.with(this).load(u5).into(((ImageView) findViewById(R.id.v5)));
        Glide.with(this).load(u6).into(((ImageView) findViewById(R.id.v6)));
        Glide.with(this).load(u7).into(((ImageView) findViewById(R.id.v7)));
        Glide.with(this).load(u8).into(((ImageView) findViewById(R.id.v8)));

        View.OnClickListener showListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show((ImageView) v);
            }
        };
        findViewById(R.id.v1).setOnClickListener(showListener);
        findViewById(R.id.v2).setOnClickListener(showListener);
        findViewById(R.id.v3).setOnClickListener(showListener);
        findViewById(R.id.v4).setOnClickListener(showListener);
        findViewById(R.id.v5).setOnClickListener(showListener);
        findViewById(R.id.v6).setOnClickListener(showListener);
        findViewById(R.id.v7).setOnClickListener(showListener);
        findViewById(R.id.v8).setOnClickListener(showListener);


        iwHelper = ImageWatcherHelper.with(this ,new SimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setIndexProvider(new CustomDotIndexProvider()); // 自定义index
    }

    private void show(ImageView clickedImage) {
        SparseArray<ImageView> mapping = new SparseArray<>();
        mapping.put(0, (ImageView) findViewById(R.id.v1));
        mapping.put(1, (ImageView) findViewById(R.id.v2));
        mapping.put(2, (ImageView) findViewById(R.id.v3));
        mapping.put(3, (ImageView) findViewById(R.id.v4));
        mapping.put(4, (ImageView) findViewById(R.id.v5));
        mapping.put(5, (ImageView) findViewById(R.id.v6));
        mapping.put(6, (ImageView) findViewById(R.id.v7));
        mapping.put(7, (ImageView) findViewById(R.id.v8));

//        initPosition = -1;
//        for (int x = 0; x < imageGroupList.size(); x++) {
//            if (imageGroupList.get(imageGroupList.keyAt(x)) == i) {
//                initPosition = imageGroupList.keyAt(x);
//                break;
//            }
//        }
//        if (initPosition < 0) {
//            throw new IllegalArgumentException("param ImageView i must be a member of the List <ImageView> imageGroupList!");
//        }

        // 以上是show源码。 所以就是说， clickedImage 是 mapping 中的任意一个。 如果不是就崩了。
        // 注意，下标当然是从0开始的
        iwHelper.show(clickedImage, mapping, dataList);
    }

    @Override
    public void onBackPressed() {
        if (!iwHelper.handleBackPressed()) {
            super.onBackPressed();
        }
    }
}



