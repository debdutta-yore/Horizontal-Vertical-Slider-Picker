package com.example.nbtk.slider

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log


/**
 * Created by nbtk on 5/4/18.
 */
class SliderLayoutManager(context: Context?) : LinearLayoutManager(context) {

    init {
         orientation = VERTICAL;
    }

    var callback: OnItemSelectedListener? = null
    private lateinit var recyclerView: RecyclerView

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        recyclerView = view!!

        // Smart snapping
        LinearSnapHelper().attachToRecyclerView(recyclerView)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        scaleDownView()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            scaleDownView()
            return scrolled
        } else {
            return 0
        }
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        if (orientation == LinearLayoutManager.VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
            scaleDownView()
            return scrolled
        } else {
            return 0
        }
    }

    private fun scaleDownView() {
        if(orientation== HORIZONTAL){
            val mid = width / 2.0f
            for (i in 0 until childCount) {

                // Calculating the distance of the child from the center
                val child = getChildAt(i)
                val childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f
                val distanceFromCenter = Math.abs(mid - childMid)

                // The scaling formula
                var scale = 1-distanceFromCenter/width

                // Set scale to view
                scale = interpolateScale(scale)
                child.scaleX = scale
                child.scaleY = scale
            }
        }
        else{
            val mid = height / 2.0f
            for (i in 0 until childCount) {

                // Calculating the distance of the child from the center
                val child = getChildAt(i)
                val childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f
                val distanceFromCenter = Math.abs(mid - childMid)

                // The scaling formula
                var scale = 1-distanceFromCenter/height

                // Set scale to view
                scale = interpolateScale(scale)
                child.scaleX = scale
                child.scaleY = scale
            }
        }
    }

    private fun interpolateScale(scale: Float): Float {
        Log.d("scale_bug","$scale")
        return Math.pow(scale.toDouble(),3.0).toFloat()
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        // When scroll stops we notify on the selected item
        if (state.equals(RecyclerView.SCROLL_STATE_IDLE)) {

            // Find the closest child to the recyclerView center --> this is the selected item.
            val recyclerViewCenterX = getRecyclerViewCenterX()
            var minDistance = recyclerView.width
            var position = -1
            for (i in 0 until recyclerView.childCount) {
                val child = recyclerView.getChildAt(i)
                val childCenterX = getDecoratedLeft(child) + (getDecoratedRight(child) - getDecoratedLeft(child)) / 2
                var newDistance = Math.abs(childCenterX - recyclerViewCenterX)
                if (newDistance < minDistance) {
                    minDistance = newDistance
                    position = recyclerView.getChildLayoutPosition(child)
                }
            }

            // Notify on item selection
            callback?.onItemSelected(position)
        }
    }

    private fun getRecyclerViewCenterX() : Int {
        return (recyclerView.right - recyclerView.left)/2 + recyclerView.left
    }

    interface OnItemSelectedListener {
        fun onItemSelected(layoutPosition: Int)
    }
}