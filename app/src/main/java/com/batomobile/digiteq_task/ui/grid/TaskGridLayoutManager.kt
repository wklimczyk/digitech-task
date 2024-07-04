package com.batomobile.digiteq_task.ui.grid

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import kotlin.math.max
import kotlin.math.min

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
            if (isLayoutRTL()) {
                getDecoratedLeft(lastItem) - (lastItem.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
            } else {
                getDecoratedRight(lastItem) + (lastItem.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
            }
        } ?: 0

        if (isLayoutRTL()) {
            val resolvedDx = max(lastItemEnd, dx)
            horizontalScrollOffset = min(
                resolvedDx + horizontalScrollOffset,
                0
            )
        } else {
            val maxScroll = horizontalScrollOffset + (lastItemEnd - width)
            horizontalScrollOffset = min(
                max(horizontalScrollOffset + dx, 0),
                maxScroll
            )
        }
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
        val itemSize = width / columnsCount
        pageWidth = itemSize * columnsCount
        val pageSize = columnsCount * rowsCount

        for (index in 0 until itemCount) {
            val view = recycler.getViewForPosition(index)
            addView(view)
            view.updateLayoutParams {
                width = itemSize
                height = itemSize
            }
            val pageIndex = index / pageSize
            val itemIndexOnPage = index % pageSize
            val columnIndex = index % columnsCount
            val rowIndex = itemIndexOnPage / columnsCount

            val left: Int
            val right: Int

            if (isLayoutRTL()) {
                right =
                    width - (pageIndex * pageWidth) - columnIndex * itemSize - horizontalScrollOffset
                left = right - itemSize
            } else {
                left = pageIndex * pageWidth + columnIndex * itemSize - horizontalScrollOffset
                right = left + itemSize
            }

            val top = rowIndex * itemSize
            val bottom = top + itemSize

            measureChildWithMargins(view, itemSize, itemSize)
            layoutDecoratedWithMargins(view, left, top, right, bottom)
        }

        recycler.scrapList.forEach {
            recycler.recycleView(it.itemView)
        }
    }

    private fun isLayoutRTL() = layoutDirection == RecyclerView.LAYOUT_DIRECTION_RTL

    fun scrollToPage(pageNumber: Int) {
        getRecyclerView()?.let { rv ->
            var pageOffset = (pageNumber - 1) * pageWidth
            if (isLayoutRTL()) pageOffset *= -1
            val dx = pageOffset - horizontalScrollOffset
            rv.smoothScrollBy(dx, 0)
        }
    }
}