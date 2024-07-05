package com.batomobile.digiteq_task.ui.grid

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class TaskGridSnapHelper : LinearSnapHelper() {

    override fun calculateDistanceToFinalSnap(
        lm: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val result = super.calculateDistanceToFinalSnap(lm, targetView)?.apply {
            (lm as? TaskGridLayoutManager)?.let {
                val pageTarget = it.getSnapTargetAnchor(targetView) ?: targetView
                val pagePosition = getItemHorizontalPosition(pageTarget, lm)
                this[0] = pagePosition
            }
        }
        return result
    }

    private fun getItemHorizontalPosition(view: View, lm: TaskGridLayoutManager) =
        if (lm.isLayoutRTL()) {
            //TODO Fix issue with last page
            lm.getDecoratedRight(view) + (view.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
        } else {
            lm.getDecoratedLeft(view) - (view.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
        }
}