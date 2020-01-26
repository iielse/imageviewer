//package com.github.iielse.imageviewer.datasource
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.github.iielse.imageviewer.ImageViewerViewModel
//
//class ImageViewerViewModelFactory : ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        when {
//            modelClass.isAssignableFrom(ImageViewerViewModel::class.java) ->
//                return ImageViewerViewModel(
//                        Components.requireImageLoader(),
//                        Components.requireDataProvider(),
//                        Components.requireTransformer(),
//                        Components.requireInitKey()) as T
//        }
//        throw IllegalArgumentException("")
//    }
//}