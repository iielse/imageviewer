#ImageWatcher
图片查看器，为各位追求用户体验的daLao提供更优质的服务
它能够

*点击图片时以一种无缝顺畅的动画切换到图片查看的界面，同样以一种无缝顺畅的动画退出图片查看界面
*支持多图查看，快速翻页，双击放大，单击退出，双手缩放旋转图片
*下拽退出查看图片的操作，以及效果是本View的最大卖点(仿微信)

![image](https://github.com/iielse/DemoProjects/blob/master/P02_ImageWatcher/previews/111.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/P02_ImageWatcher/previews/222.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/P02_ImageWatcher/previews/333.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/P02_ImageWatcher/previews/444.gif)

## 下载（强烈推荐下载体验）

[DemoApp.apk](https://github.com/iielse/DemoProjects/blob/master/P02_ImageWatcher/previews/app-debug.apk)

至尊体验;daLao专用;上图Gif不够看？下载apk自行体验; /doge

## 实现步骤

在module的gradle
```
compile 'ch.ielse:imagewatcher:1.0.0'
```

首先在xml布局中
```
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- some layout here -->

    <ch.ielse.view.imagewatcher.ImageWatcher
        android:id="@+id/v_image_watcher"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <!-- 在跟布局的下面盖上的一个ImageWatcher,ImageWatcher初始化默认是INVISIABLE的 -->
</FrameLayout>
```

蓝后在Activity onCreate里面 一般需要调用这3个API简单的初始化一下

```
// 一般来讲， ImageWatcher 需要占据全屏的位置
ImageWatcher vImageWatcher = (ImageWatcher) findViewById(R.id.v_image_watcher);
// 如果是透明状态栏，你需要给ImageWatcher标记 一个偏移值，以修正点击ImageView查看的启动动画的Y轴起点的不正确
vImageWatcher.setTranslucentStatus(!isTranslucentStatus ? Utils.calcStatusBarHeight(this) : 0);
// 配置error图标，imageWatcher里面有默认图标，你并不一定要调用这个API
vImageWatcher.setErrorImageRes(R.mipmap.error_picture);
// 长按图片的回调，你可以显示一个框继续提供一些复制，发送等功能
vImageWatcher.setOnPictureLongPressListener(this);
```

这个时候你的所有准备工作已经完成
```
/**
 * @param i              被点击的ImageView
 * @param imageGroupList 被点击的ImageView的所在列表，加载图片时会提前展示列表中已经下载完成的thumb图片
 * @param urlList        被加载的图片url列表，数量必须大于等于 imageGroupList.size。 且顺序应当和imageGroupList保持一致
 */
public void show(ImageView i, List<ImageView> imageGroupList, final List<String> urlList) { ... }
```

最后只要调用 `vImageWatcher.show()` 方法就可以了
可以具体看源码demo，这个项目是可以运行的，这个项目是可以运行的，这个项目是可以运行的

## 写在最后
为什么要写这个Demo？

*能够给在项目上有这个功能需求而又愿意试水此库的各位daLao节约一些开发时间
*怕长时间不写代码，会慢慢忘记，于是反复练习
*为了更好的视觉体验

ps:如果此裤对你提供了帮助，你的Star是对本宝最大的支持。  谢 /舔

Q群274306954(可以找到我)