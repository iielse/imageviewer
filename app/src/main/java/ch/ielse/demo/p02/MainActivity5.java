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

public class MainActivity5 extends Activity {
    private ImageWatcherHelper iwHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        iwHelper = ImageWatcherHelper.with(this, new SimpleLoader());

        View vGifPicture = findViewById(R.id.vGifPicture);
        Glide.with(vGifPicture.getContext()).asBitmap().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1534324920537&di=278f9d990f277cca368a118afaac196b&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fitem%2F201409%2F22%2F20140922013938_NZfBs.thumb.700_0.gif")
                .into((ImageView) vGifPicture); // asBitmap 必须加
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
    }

    private List<Uri> convert(List<String> data) {
        List<Uri> list = new ArrayList<>();
        for (String d : data) list.add(Uri.parse(d));
        return list;
    }

    @Override
    public void onBackPressed() {
        if (!iwHelper.handleBackPressed()) {
            super.onBackPressed();
        }
    }
}



