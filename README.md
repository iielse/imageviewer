### 图片浏览器

- 支持左右/上下滑动（基于ViewPager2）
- 支持常有图片操作手势（基于PhotoView）
- 支持查看超大图（基于SubsamplingScaleImageView）
- 支持简单的下拽退出
- 支持进入退出动画
- 支持数据内容异步追加（向前翻页.聊天/帖子历史记录中的图片查看等）

#### 使用
```
implementation 'com.github.iielse.imageviewer:imageviewer:2.0.1'
```
```
ImageViewerBuilder(
                context = this,
                initKey = photo.id,
                dataProvider = MyDataProvider(),
                imageLoader = MyImageLoader(),
                transformer = MyTransformer()
        ).show()
```

#### TODO
- 适配RTL问题

欢迎提出需求建议&bug反馈
