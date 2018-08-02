# ImageWatcher
图片查看器，为各位追求用户体验的daLao提供更优质的服务
它能够

* 动画无缝切换到查看状态，同样无缝顺畅的动画退出图片查看界面
* 支持多图查看
* 仿微信下拽退出查看图片的操作

![image](https://github.com/iielse/DemoProjects/blob/master/previews/111.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/previews/222.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/previews/333.gif)
![image](https://github.com/iielse/DemoProjects/blob/master/previews/444.gif)

### 下载（强烈推荐下载体验）
在 previews文件夹下 - app-debug.apk
[DemoApp.apk](https://github.com/iielse/DemoProjects/blob/master/previews/app-debug.apk)

至尊体验;daLao专用;上图Gif不够看？下载apk自行体验; /doge


对比之前，
* 修复了错误的宽高计算导致起始图片位置显示错误。
* 优化取消了无意义的旋转，优化了下拽体验，放大情况下，图片已经显示顶端时也可下拽退出。
* 新增支持长图显示(beta)。 使用的网络图片，被屏蔽了请自己换地址，或提醒我。
* 新增自定义loadingUIView
* 新增自定义indexUI


#### 实现步骤

在module的gradle
```
compile 'com.github.ielse:imagewatcher:??'   // 过段时间
```


调用全在demo代码里。 自己看

可以具体看源码demo，这个项目是可以运行的，这个项目是可以运行的，这个项目是可以运行的

#### 写在最后
* 希望能够给在项目上有这个功能需求而又愿意试水的各位daLao节约一些开发时间
* 为了更好的视觉体验


更推荐自己copy代码定制，有不好的地方，和更好的想法欢迎提出来。 楼主也许可能会更新，比如这次/斜眼笑。

如果这些代码对你提供了帮助，你的Star是对本宝最大的支持。  谢 /舔
