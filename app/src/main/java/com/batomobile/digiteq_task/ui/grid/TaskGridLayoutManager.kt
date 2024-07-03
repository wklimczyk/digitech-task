package com.batomobile.digiteq_task.ui.grid

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class TaskGridLayoutManager(
    private val rowsCount: Int,
    private val columnsCount: Int,
    private val context: Context
//) : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
) : RecyclerView.LayoutManager() {

    private var horizontalScrollOffset = 0
    var pageWidth: Int = 0

    private var recycler: Recycler? = null

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(
        RecyclerView.LayoutParams.WRAP_CONTENT,
        RecyclerView.LayoutParams.WRAP_CONTENT
    )

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        fill(recycler = recycler)
    }

    override fun canScrollHorizontally() = true

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: Recycler,
        state: RecyclerView.State?
    ): Int {
        if (childCount < rowsCount * columnsCount) return 0
        val lastScrollOffset = horizontalScrollOffset
        val pageSize = columnsCount * rowsCount
        val indexOfItemAtTheEnd =
            (itemCount / pageSize) * pageSize + min(columnsCount, itemCount % pageSize) - 1
        val lastItem = getChildAt(indexOfItemAtTheEnd)

        val lastItemEnd = lastItem?.run {
            if (getIsLTR()) {
                //TODO add overscroll to fill empty columns in page
                getDecoratedRight(lastItem) + (lastItem.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
            } else {
                getDecoratedLeft(lastItem) - (lastItem.layoutParams as ViewGroup.MarginLayoutParams).marginStart
            }
        } ?: 0

        //TODO fix RTL scroll bounds
        horizontalScrollOffset = min(
            max(horizontalScrollOffset + dx, 0),
            horizontalScrollOffset + (lastItemEnd - width)
        )
        fill(recycler = recycler)
        return horizontalScrollOffset - lastScrollOffset
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val linearSmoothScroller = LinearSmoothScroller(recyclerView.context)
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    private fun fill(recycler: Recycler) {
        detachAndScrapAttachedViews(recycler)
        val itemWidth = width / columnsCount
        val itemHeight = height / rowsCount
        pageWidth = itemWidth * columnsCount
        val pageSize = columnsCount * rowsCount

        for (index in 0 until itemCount) {
            val view = recycler.getViewForPosition(index)
            addView(view)
            view.updateLayoutParams {
                width = itemWidth
                height = itemHeight
            }
            val pageIndex = index / pageSize
            val itemIndexOnPage = index % pageSize
            val columnIndex = index % columnsCount
            val rowIndex = itemIndexOnPage / columnsCount

            val left: Int
            val right: Int

            if (getIsLTR()) {
                left = pageIndex * pageWidth + columnIndex * itemWidth - horizontalScrollOffset
                right = left + itemWidth
            } else {
                right =
                    width - (pageIndex * pageWidth) - columnIndex * itemWidth + horizontalScrollOffset
                left = right - itemWidth
            }

            val top = rowIndex * itemHeight
            val bottom = top + itemHeight

            measureChildWithMargins(view, itemWidth, itemHeight)
            layoutDecoratedWithMargins(view, left, top, right, bottom)
        }

        recycler.scrapList.forEach {
            recycler.recycleView(it.itemView)
        }
    }

    private fun getIsLTR() = layoutDirection == RecyclerView.LAYOUT_DIRECTION_LTR

    fun scrollToPage(pageNumber: Int) {
        getRecyclerView()?.let { rv ->
            val pageOffset = (pageNumber - 1) * pageWidth
            rv.smoothScrollBy(pageOffset - horizontalScrollOffset, 0)
        }
    }

    private fun getRecyclerView() =
        RecyclerView.LayoutManager::class.memberProperties.find { it.name == "mRecyclerView" }
            ?.let {
                it.isAccessible = true
                it.get(this) as RecyclerView
            }

    private fun getRecycler(rv: RecyclerView) =
        RecyclerView::class.memberProperties.find { it.name == "mRecycler" }?.let {
            it.isAccessible = true
            it.get(rv) as Recycler
        }
}