# Imageviewer 集成攻略
```
fun show(view: View) {
        val dataList = TestRepository.get().data
        val clickedData = dataList[dataList.size - 1 - (System.currentTimeMillis() % 10).toInt()]
        val builder = ImageViewerBuilder(
                context = view.context,
                initKey = clickedData.id,
                dataProvider = SimpleDataProvider(dataList),
                imageLoader = MyImageLoader(),
                transformer = object : Transformer {
                    override fun getView(key: Long): ImageView {
                        return fakeStartView(view)
                    }
                }
        )
        builder.show()
    }
```