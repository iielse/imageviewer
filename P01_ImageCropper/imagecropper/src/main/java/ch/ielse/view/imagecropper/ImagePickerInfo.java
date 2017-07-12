package ch.ielse.view.imagecropper;


import io.reactivex.subjects.PublishSubject;

public class ImagePickerInfo {

    public final String cropTitle;
    public final int outputWidth;
    public final int outputHeight;
    public final boolean isCircleOverlay;
    public final boolean crop;
    public final PublishSubject subject;

    public ImagePickerInfo(String cropTitle, int outputWidth, int outputHeight, boolean isCircleOverlay, boolean crop, PublishSubject subject) {
        this.cropTitle = cropTitle;
        this.outputWidth = outputWidth;
        this.outputHeight = outputHeight;
        this.isCircleOverlay = isCircleOverlay;
        this.crop = crop;
        this.subject = subject;
    }

    public ImagePickerInfo(PublishSubject subject) {
        this("", 0, 0, false, false, subject);
    }
}
