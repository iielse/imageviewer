package ch.ielse.view.imagecropper;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;

@SuppressWarnings("unchecked")
public class RxImagePicker {
    static final String TAG = "RxImagePicker";

    final RxImagePickerFragment mRxPickerFragment;

    public RxImagePicker(@NonNull Activity activity) {
        mRxPickerFragment = getRxPickerFragment(activity);
    }

    private RxImagePickerFragment getRxPickerFragment(Activity activity) {
        RxImagePickerFragment rxPermissionsFragment = findRxPickerFragment(activity);
        boolean isNewInstance = rxPermissionsFragment == null;
        if (isNewInstance) {
            rxPermissionsFragment = new RxImagePickerFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return rxPermissionsFragment;
    }

    private RxImagePickerFragment findRxPickerFragment(Activity activity) {
        return (RxImagePickerFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public RxImagePicker setTranslucentStatusHeight(int translucentStatusHeight) {
        mRxPickerFragment.setTranslucentStatusHeight(translucentStatusHeight);
        return this;
    }

    public Observable<String> queryAlbum() {
        return mRxPickerFragment.queryAlbum("", 0, 0, false, false);
    }

    public Observable<Bitmap> queryAlbum(String cropTitle, int outputWidth, int outputHeight, boolean isCircleOverlay) {
        return mRxPickerFragment.queryAlbum(cropTitle, outputWidth, outputHeight, isCircleOverlay, true);
    }

    public Observable<String> takeCamera() {
        return mRxPickerFragment.takeCamera("", 0, 0, false, false);
    }

    public Observable<Bitmap> takeCamera(String cropTitle, int outputWidth, int outputHeight, boolean isCircleOverlay) {
        return mRxPickerFragment.takeCamera(cropTitle, outputWidth, outputHeight, isCircleOverlay, true);
    }

    public Observable<List<String>> queryMulti(int fetchCount) {
        return mRxPickerFragment.queryMulti(fetchCount);
    }
}
