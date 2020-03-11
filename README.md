### 图片浏览器

![](https://github.com/iielse/res/blob/master/imageviewer/1.gif)

- 支持左右翻页滑动&上下翻页滑动
- 支持常用的图片操作手势.双击放大/缩小等（基于[PhotoView](https://github.com/chrisbanes/PhotoView)）
- 支持查看超大图（基于[SubsamplingScaleImageView](https://github.com/davemorrissey/subsampling-scale-image-view)）
- 支持下拽退出手势
- 支持进入&退出动画
- 支持数据分页加载

#### 使用
```
implementation 'com.github.iielse:imageviewer:2.0.5'
```
```
val builder = ImageViewerBuilder(
    context = this,
    initKey = photo.id,
    dataProvider = MyDataProvider(),
    imageLoader = MyImageLoader(),
    transformer = MyTransformer()
)
builder.setVHCustomizer(MyCustomViewHolderUI())
builder.setOverlayCustomizer(MyCustomIndicatorUI())
builder.setViewerCallback(MyViewerStateChangedListener())
builder.show()
```

主要接口 | 作用介绍
--- | ---
DataProvider | 提供浏览数据源.支持分页异步加载更多数据
ImageLoader | 提供数据对应视图的加载方案.以及超大图的加载方案
Transformer | 确定被浏览的控件和页面上原有控件的对应关系
VHCustomizer |自定义每页浏览UI
OverlayCustomizer |自定义覆盖在Viewer之上的UI
ViewerCallback | 提供页面滑动及浏览内容被下拽返回的状态回调



欢迎提出需求建议&bug反馈