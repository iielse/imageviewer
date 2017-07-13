# ImageCropper
图片裁剪器，为各位追求用户体验的daLao提供更优质的服务
它能够

* 按需求比例裁剪图片，或者按照指定尺寸输出
* ~~它是一个view不是activity，所以并不需要在AndroidManifest.xml中注册~~
* RxImagePicker 搞到图片只要1行代码

![image](https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/previews/111.gif)

马蛋，模拟器不能多点触摸(![image](https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/previews/222.gif))

## 下载（强烈推荐下载体验）

[DemoApp.apk](https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/previews/app-debug.apk)

至尊体验;daLao专用;上图Gif不够看？下载apk自行体验; /doge


![image](https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/previews/333.jpg)

![image](https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/previews/444.jpg)

## 使用方法


* 从相册中取出一张原图

```
new RxImagePicker(activity)
    .queryAlbum()
    .subscribe(new Consumer<String>() {
        @Override
        public void accept(@NonNull String output) throws Exception {
            Glide.with(iTarget.getContext()).load(output).into(iTarget);
        }
    });
```

* 从相册中选择单张图片并完成指定尺寸(比例)裁剪

```
final ImageView iTarget;
boolean circleOverlay = true;
new RxImagePicker(activity)
    .setTranslucentStatusHeight(statusBarHeight)
    .queryAlbum("设置头像",iTarget.getWidth(), iTarget.getHeight(), circleOverlay)
    .subscribe(new Consumer<Bitmap>() {
        @Override
        public void accept(@NonNull Bitmap output) throws Exception {
            iTarget.setImageBitmap(Utils.transformCropCircle(output));
        }
    });
```

* 使用手机相机拍摄单张图片并完成指定尺寸(比例)裁剪

```
final ImageView iTarget;
boolean circleOverlay = false;
new RxImagePicker(activity)
    .setTranslucentStatusHeight(statusBarHeight)
    .takeCamera("设置背景",iTarget.getWidth(), iTarget.getHeight(), circleOverlay)
    .subscribe(new Consumer<Bitmap>() {
        @Override
        public void accept(@NonNull Bitmap output) throws Exception {
            iTarget.setImageBitmap(output);
        }
    });
```

* 多图选择 得到选择的原图url集合

```
final int fetchCount = 9;
new RxImagePicker(activity)
    .queryMulti(fetchCount)
    .subscribe(new Consumer<List<String>>() {
        @Override
        public void accept(@NonNull List<String> pictureUrls) throws Exception {
        }
    });
```


可以具体看源码demo，这个项目是可以运行的，这个项目是可以运行的，这个项目是可以运行的

* 能够给在项目上有这个功能需求而又愿意试水此库的各位daLao节约一些开发时间
* 怕长时间不写代码，会慢慢忘记，于是反复练习
* 为了更好的视觉体验
* 如果对你提供了帮助，你的Star是对本宝最大的支持。谢 /舔