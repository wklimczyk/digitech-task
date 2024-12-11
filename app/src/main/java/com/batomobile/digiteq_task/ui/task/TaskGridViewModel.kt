package com.batomobile.digiteq_task.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskGridViewModel : ViewModel() {

    private val pStateStream = MutableStateFlow(TaskGridState())
    val stateStream: StateFlow<TaskGridState> = pStateStream

    fun loadData(count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            pStateStream.emit(
                stateStream.value.copy(
                    list = generateData(count)
                )
            )
        }
    }

    private fun generateData(count: Int): List<String> {
        if (count < 1) return emptyList()
        return (1..count).map { "item $it" }
    }
}