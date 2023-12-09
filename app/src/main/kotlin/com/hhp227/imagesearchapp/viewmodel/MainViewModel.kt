package com.hhp227.imagesearchapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hhp227.imagesearchapp.data.Document
import com.hhp227.imagesearchapp.data.MainRepository
import com.hhp227.imagesearchapp.data.NetworkStatus
import com.hhp227.imagesearchapp.utilities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val collections: LiveData<List<String>> get() = savedStateHandle.getLiveData(COLLECTIONS_KEY)

    val currentQuery: LiveData<String> get() = savedStateHandle.getLiveData(QUERY_KEY)

    val filterKeyword: LiveData<String> get() = savedStateHandle.getLiveData(FILTER_KEY)

    val pagingData: LiveData<PagingData<Document>> get() = currentQuery.switchMap { query ->
        repository.searchImage(query).cachedIn(viewModelScope)
    }

    val networkStatus: LiveData<NetworkStatus> get() = savedStateHandle.getLiveData(NETWORK_STATUS_KEY)

    private fun setFilterKeyword(keyword: String) {
        savedStateHandle[FILTER_KEY] = keyword
    }

    fun setCollections(list: List<String>) {
        savedStateHandle[COLLECTIONS_KEY] = list
    }

    fun setQuery(query: String) {
        savedStateHandle[QUERY_KEY] = query
    }

    fun setNetworkStatus(status: NetworkStatus) {
        savedStateHandle[NETWORK_STATUS_KEY] = status
    }

    fun onItemSelected(position: Int) {
        val collection = savedStateHandle.get<List<String>>(COLLECTIONS_KEY)

        requireNotNull(collection)
        setFilterKeyword(collection[position])
    }

    init {
        if (!savedStateHandle.contains(COLLECTIONS_KEY)) {
            setCollections(listOf(ALL))
        }
        if (!savedStateHandle.contains(QUERY_KEY)) {
            setQuery(DEFAULT_QUERY)
        }
    }
}