package com.batomobile.digiteq_task.ui.task

import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class TaskGridAdapter(private val data: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<GridItemViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): GridItemViewHolder {
        return GridItemViewHolder(ComposeView(parent.context))
    }

    override fun getItemCount() = data.count()

    override fun onBindViewHolder(holder: GridItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun updateData(newData: List<String>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition] == newData[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition] == newData[newItemPosition]
            }

            override fun getOldListSize() = data.size

            override fun getNewListSize() = newData.size
        })

        data.clear()
        data.addAll(newData)
        diff.dispatchUpdatesTo(this)
    }
}

class GridItemViewHolder(
    private val composeView: ComposeView
) : RecyclerView.ViewHolder(composeView) {
    fun bind(input: String) {
        composeView.setContent {
//            TextButton(onClick = {}
//            ) {
            Text(input)
//            }
        }
    }
}