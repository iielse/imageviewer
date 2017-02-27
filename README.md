#DemoProjects
为了更好的交互体验

###P01 [ImageCropper](https://github.com/iielse/DemoProjects/tree/master/P01_ImageCropper)
图片裁剪器，为各位追求用户体验的daLao提供更优质的服务
它能够
* 按需求比例裁剪图片，或者按照指定尺寸输出
* 它是一个view不是activity，所以并不需要在AndroidManifest.xml中注册

![image](https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/previews/111.gif)

彩蛋
* 内含对Android6.0动态权限的申请的处理 
[`PermissionUtils`](https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/app/src/main/java/ch/ielse/demo/p01/PermissionUtils.java)
* 封装了获得图片的逻辑，调用炒鸡简单
[`PictureInquirer`]
(https://github.com/iielse/DemoProjects/blob/master/P01_ImageCropper/app/src/main/java/ch/ielse/demo/p01/PictureInquirer.java)

###P02 [ImageWatcher](https://github.com/iielse/DemoProjects/tree/master/P02_ImageWatcher)
图片查看器，为各位追求用户体验的daLao提供更优质的服务 它能够
* 点击图片时以一种无缝顺畅的动画切换到图片查看的界面，同样以一种无缝顺畅的动画退出图片查看界面 
* 支持多图查看，快速翻页，双击放大，单击退出，双手缩放旋转图片 
* 下拽退出查看图片的操作，以及效果是本View的最大卖点(仿微信)

![image](https://github.com/iielse/DemoProjects/blob/master/P02_ImageWatcher/previews/111.gif)


####更多
P04 [ImageWatcher](https://github.com/iielse/DemoProjects/tree/master/P04_TitleAndPager)

撸码中...

彩蛋
* WaterRefreshHeader 高仿iOS QQ 水滴下拉

![image](https://github.com/iielse/DemoProjects/blob/master/P04_TitleAndPager/previews/222.gif)


* expandable + stickyheader RecyclerView 
![image](https://github.com/iielse/DemoProjects/blob/master/P04_TitleAndPager/previews/333.gif)


撸码中...
