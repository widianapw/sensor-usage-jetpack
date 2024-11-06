package com.sensor.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sensor.app.MyApplication
import com.sensor.app.data.schema.History
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(

): ViewModel() {

//    val _state = MutableStateFlow(HistoryScreenState())
//
//    val state = _state

    private val realm = MyApplication.realm
    val histories = realm
        .query<History>()
        .asFlow()
        .map { results ->
            results.list.toList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    fun deleteHistory(history: History) {
        viewModelScope.launch {
            realm.write {
                val item = findLatest(history)?: return@write
                delete(item)
            }
        }
    }
}