//package ch.ielse.demo.p02;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.net.Uri;
//import android.support.annotation.Nullable;
//
//import com.facebook.common.executors.CallerThreadExecutor;
//import com.facebook.common.references.CloseableReference;
//import com.facebook.datasource.DataSource;
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.imagepipeline.core.ImagePipeline;
//import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
//import com.facebook.imagepipeline.image.CloseableImage;
//import com.facebook.imagepipeline.request.ImageRequest;
//import com.facebook.imagepipeline.request.ImageRequestBuilder;
//import com.github.iielse.imagewatcher.ImageWatcher;
//
//class FrescoSimpleLoader implements ImageWatcher.Loader {
//    @Override
//    public void load(final Context context, Uri uri, final ImageWatcher.LoadCallback lc) {
//        lc.onLoadStarted(null);
//
//        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).setProgressiveRenderingEnabled(true).build();
//        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
//        dataSource.subscribe(new BaseBitmapDataSubscriber() {
//            @Override
//            public void onNewResultImpl(@Nullable final Bitmap bitmap) {
//                //bitmap即为下载所得图片
//
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        lc.onResourceReady(new BitmapDrawable(bitmap));
//                    }
//                });
//            }
//
//            @Override
//            public void onFailureImpl(DataSource dataSource) {
//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        lc.onLoadFailed(null);
//                    }
//                });
//            }
//        }, CallerThreadExecutor.getInstance());
//    }
//}
