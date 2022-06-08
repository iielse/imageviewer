package com.github.iielse.imageviewer.demo.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.github.iielse.imageviewer.demo.core.Cell
import com.github.iielse.imageviewer.demo.data.*

class TestDataViewModel(
    private val repository: TestRepository
) : ViewModel() {
    val dataList: LiveData<PagedList<Cell>> = repository.dataList
    fun remove(item: List<MyData>) = repository.localDelete(item)
    fun request() = repository.request(true)

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TestDataViewModel( TestRepository.get()) as T
        }
    }
}