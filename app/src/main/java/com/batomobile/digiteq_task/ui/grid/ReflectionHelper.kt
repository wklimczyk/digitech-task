package com.batomobile.digiteq_task.ui.grid

import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun TaskGridLayoutManager.getRecyclerView() =
    RecyclerView.LayoutManager::class.memberProperties.find { it.name == "mRecyclerView" }
        ?.let {
            it.isAccessible = true
            it.get(this) as RecyclerView
        }

fun RecyclerView.getRecycler() =
    RecyclerView::class.memberProperties.find { it.name == "mRecycler" }?.let {
        it.isAccessible = true
        it.get(this) as RecyclerView.Recycler
    }