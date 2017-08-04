package ch.ielse.view.richtexteditor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;

import ch.ielse.view.imagecropper.RxImagePicker;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class RichTextEditor extends FrameLayout implements View.OnClickListener {
    View vAddImage;
    EditText vInput;

    public RichTextEditor(Context context) {
        this(context, null);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.rich_text_editor, this);
        vInput = (EditText) findViewById(R.id.vInput);
        (vAddImage = findViewById(R.id.vAddImage)).setOnClickListener(this);


    }


    @Override
    public void onClick(final View view) {
        if (view == vAddImage) {
            new RxPermissions((Activity) view.getContext())
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(@NonNull Boolean aBoolean) throws Exception {

                            new RxImagePicker((Activity) view.getContext())
                                    .queryAlbum()
                                    .flatMap(new Function<String, ObservableSource<String>>() {
                                        @Override
                                        public ObservableSource<String> apply(@NonNull String s) throws Exception {
                                            return rxCompress(s);
                                        }
                                    })
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(@NonNull String s) throws Exception {
                                            addImageSpan(s);
                                        }
                                    });
                        }
                    });
        }
    }

    void addImageSpan(final String localImage) {
        Glide.with(getContext()).load(localImage).asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Editable editable = vInput.getText();

                        ImageSpan imageSpan = new ImageSpan(getContext(), resource);
                        CharSequence text = "[i]" + localImage + "[/i]";
                        SpannableStringBuilder builder = new SpannableStringBuilder(text);
                        builder.setSpan(imageSpan, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        editable.insert(vInput.getSelectionEnd(), "\n");
                        editable.insert(vInput.getSelectionEnd(), builder);
                        editable.insert(vInput.getSelectionEnd(), "\n");

                        Toast.makeText(getContext().getApplicationContext(), vInput.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    void getOutput() {
        // TODO 
    }

    ObservableSource<String> rxCompress(@NonNull final String originUrl) {
        return new ObservableSource<String>() {
            @Override
            public void subscribe(@NonNull final Observer<? super String> observer) {
                Tiny.getInstance().source(new File(originUrl)).asFile().compress(new FileCallback() {
                    @Override
                    public void callback(boolean isSuccess, String outfile) {
                        if (isSuccess) {
                            observer.onNext(outfile);
                        } else {
                            Toast.makeText(getContext().getApplicationContext(),
                                    "源文件受损或内存不足导致压缩失败，请重试或换一张\n" + originUrl, Toast.LENGTH_SHORT).show();
                        }
                        observer.onComplete();
                    }
                });
            }
        };
    }
}
