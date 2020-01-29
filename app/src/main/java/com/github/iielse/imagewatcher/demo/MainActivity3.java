package com.github.iielse.imagewatcher.demo;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.iielse.imagewatcher.ImageWatcherHelper;

import java.util.ArrayList;
import java.util.List;
import com.github.iielse.imageviewer.demo.R;

public class MainActivity3 extends AppCompatActivity {

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
        setContentView(R.layout.activity_main3);

        iwHelper = ImageWatcherHelper.with(this, new GlideSimpleLoader()) // 一般来讲， ImageWatcher 需要占据全屏的位置
                .setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(this) : 0) // 如果不是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
                .setErrorImageRes(R.mipmap.error_picture); // 配置error图标 如果不介意使用lib自带的图标，并不一定要调用这个API

        Utils.fitsSystemWindows(isTranslucentStatus, findViewById(R.id.v_fit));

        ImageView iPicture = (ImageView) findViewById(R.id.i_picture);

        final String picThumbUrl = "http://img.my.csdn.net/uploads/201707/27/1501118720_9504.jpg";
        final String picUrl = "http://img.my.csdn.net/uploads/201707/27/1501118577_9169.jpg";

        Glide.with(iPicture.getContext()).asBitmap().load(picThumbUrl).into(iPicture);
        iPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> longPictureList = new ArrayList<>();
                longPictureList.add(picUrl);
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(0, (ImageView) v);
                iwHelper.show((ImageView) v, mappingViews, convert(longPictureList));
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
                longPictureList.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2211841400,3995215486&fm=27&gp=0.jpg");
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(0, (ImageView) v);
                iwHelper.show((ImageView) v, mappingViews, convert(longPictureList));
            }
        });

        Glide.with(iPicture.getContext()).asBitmap().load("http://image.xcar.com.cn/attachments/a/day_20141126/2014112609_68d9843ed0c3e1d05735CObUWVqIy3rr.jpg")
                .into((ImageView) findViewById(R.id.vLongPictureH));
        findViewById(R.id.vLongPictureH).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> longPictureList = new ArrayList<>();
                longPictureList.add("http://image.xcar.com.cn/attachments/a/day_20141126/2014112609_68d9843ed0c3e1d05735CObUWVqIy3rr.jpg");
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(0, (ImageView) v);
                iwHelper.show((ImageView) v, mappingViews, convert(longPictureList));
            }
        });


        View vGifPicture = findViewById(R.id.vGifPicture);
        Glide.with(vGifPicture.getContext()).asBitmap().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534324920537&di=278f9d990f277cca368a118afaac196b&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201409%2F22%2F20140922013938_NZfBs.thumb.700_0.gif")
                .into((ImageView) vGifPicture);
        vGifPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> longPictureList = new ArrayList<>();
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534326475217&di=f9b996c80a034ae12126bed1e0156445&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201411%2F25%2F20141125204219_QzmMA.thumb.700_0.gif");
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534324920537&di=278f9d990f277cca368a118afaac196b&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201409%2F22%2F20140922013938_NZfBs.thumb.700_0.gif");
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534326475217&di=d828d5dd375440a6344d75ecde93dcb6&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201411%2F29%2F20141129014255_4hnZt.thumb.700_0.gif");
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534326475216&di=09e333916e78b88ad80ad80b26b65bf5&imgtype=0&src=http%3A%2F%2Fattach.cgjoy.com%2Fattachment%2Fforum%2F201505%2F06%2F102801e87qjqt1n3hmt0n1.gif");
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(1, (ImageView) v);
                iwHelper.show((ImageView) v, mappingViews, convert(longPictureList));
            }
        });

        View vCompatImage = findViewById(R.id.vCompatImage);
        Glide.with(vCompatImage.getContext()).asBitmap().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536811355545&di=2436088f354f1a8355ea8db0a82592e0&imgtype=0&src=http%3A%2F%2Fgss0.bdstatic.com%2F7LsWdDW5_xN3otebn9fN2DJv%2Fdoc%2Fpic%2Fitem%2F3801213fb80e7bec392a30d9232eb9389b506b11.jpg")
                .into((ImageView) vCompatImage); //
        vCompatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> longPictureList = new ArrayList<>();
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536811355543&di=87e5c3def99fdad3e356263114f11f0b&imgtype=0&src=http%3A%2F%2Fthumb11.jfcdns.com%2Fthumb%2F2017-11%2Fbce5a02c4551db77_600_400.jpeg");
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536811355543&di=a0dbe4cb9420b031669693f2d170aaae&imgtype=0&src=http%3A%2F%2Fthumb10.jfcdns.com%2Fthumb%2F2017-11%2Fbce5a02c45735cc6_600_400.jpeg");
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536811355543&di=a1b77f149342811754180a7b88382e13&imgtype=0&src=http%3A%2F%2Fgss0.bdstatic.com%2F7LsWdDW5_xN3otebn9fN2DJv%2Fdoc%2Fpic%2Fitem%2Ff31fbe096b63f62442fe1e3b8b44ebf81a4ca362.jpg");
                longPictureList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536811355545&di=2436088f354f1a8355ea8db0a82592e0&imgtype=0&src=http%3A%2F%2Fgss0.bdstatic.com%2F7LsWdDW5_xN3otebn9fN2DJv%2Fdoc%2Fpic%2Fitem%2F3801213fb80e7bec392a30d9232eb9389b506b11.jpg");
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(3, (ImageView) v);
                iwHelper.show((ImageView) v, mappingViews, convert(longPictureList));
            }
        });


        final String urlFitXY = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=393198154,1291067317&fm=15&gp=0.jpg";
        ImageView vFitXyImage = findViewById(R.id.vFitXyImage);
        Glide.with(vCompatImage.getContext()).asBitmap().load(urlFitXY)
                .into(vFitXyImage); //
        vFitXyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> longPictureList = new ArrayList<>();
                longPictureList.add(urlFitXY);
                final int initPosition = 0;
                final SparseArray<ImageView> mappingViews = new SparseArray<>();
                mappingViews.put(initPosition, (ImageView) v);
                iwHelper.show(initPosition, mappingViews, convert(longPictureList));
            }
        });
    }

    private List<Uri> convert(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }


//    @Override
//    public void onBackPressed() {
//        if (!iwHelper.handleBackPressed()) {
//            super.onBackPressed();
//        }
//    }
}
