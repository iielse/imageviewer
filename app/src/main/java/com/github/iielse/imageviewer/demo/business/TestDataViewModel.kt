package com.github.iielse.imageviewer.demo.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.github.iielse.imageviewer.demo.core.Cell
import com.github.iielse.imageviewer.demo.data.*

class TestDataViewModelFactory(
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = TestRepository.get()
        if (modelClass.isAssignableFrom(TestDataViewModel::class.java)) {
            return TestDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TestDataViewModel(
    private val repository: TestRepository
) : ViewModel() {
    val dataList: LiveData<PagedList<Cell>> = repository.dataList
    fun remove(item: List<MyData>) = repository.reduceDelete(item)
    fun request() = repository.request(true)
}