package com.batomobile.digiteq_task

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.batomobile.digiteq_task.databinding.ActivityMainBinding
import com.batomobile.digiteq_task.ui.grid.TaskGridLayoutManager
import com.batomobile.digiteq_task.ui.task.TaskGridAdapter
import com.batomobile.digiteq_task.ui.task.TaskGridViewModel
import com.batomobile.digiteq_task.ui.theme.DigitechTaskTheme
import kotlinx.coroutines.launch
import kotlin.math.ceil

const val rowsCount = 2
const val columnsCount = 5
const val itemCount = 56

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TaskGridViewModel
    private lateinit var adapter: TaskGridAdapter
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this@MainActivity)[TaskGridViewModel::class.java]
        adapter = TaskGridAdapter()
        buildRegular()
//        buildComposable()

        collectState()
        viewModel.loadData(itemCount)
    }

    private fun buildRegular() {
        with(ActivityMainBinding.inflate(layoutInflater)) {
            binding = this
            setContentView(root)
            gridRecyclerView.layoutManager = getLayoutManager()
            gridRecyclerView.adapter = adapter
        }
    }

    private fun showMeSomeDocs(context: Context) {
        GridLayoutManager(context, columnsCount)
        LinearLayoutManager(context).apply {
            LinearSnapHelper()
        }
    }

    private fun getLayoutManager() =
        TaskGridLayoutManager(rowsCount, columnsCount, this@MainActivity)

    private fun buildComposable() {
        setContent {
            DigitechTaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ComposableGrid(
                        recyclerView = { context ->
                            RecyclerView(context).apply {
                                layoutManager = this@MainActivity.getLayoutManager()
                                adapter = this@MainActivity.adapter
                            }
                        }, modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun collectState() {
        lifecycleScope.launch {
            viewModel.stateStream.collect { state ->
                adapter.updateData(state.list)
                val pageCount = ceil(state.list.size / (columnsCount * rowsCount.toDouble()))
                with(binding.pageNumber) {
                    if (pageCount > 1) {
                        visibility = View.VISIBLE
                        valueFrom = 1f
                        value = 1f
                        valueTo = pageCount.toFloat()
                        addOnChangeListener { _, fl, _ ->
                            (binding.gridRecyclerView.layoutManager as TaskGridLayoutManager).scrollToPage(
                                fl.toInt()
                            )
                        }
                    }
                }

            }
        }
    }

    @Composable
    internal fun ComposableGrid(
        recyclerView: (Context) -> View, modifier: Modifier = Modifier
    ) {
        AndroidView(modifier = modifier, factory = { context -> recyclerView(context) })
    }
}


