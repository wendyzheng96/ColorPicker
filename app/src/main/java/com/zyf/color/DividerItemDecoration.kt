package com.zyf.color

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.graphics.drawable.Drawable
import android.support.v7.widget.StaggeredGridLayoutManager

/**
 * 分割线
 */
class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val attrs: IntArray = intArrayOf(android.R.attr.listDivider)
    private var mDivider: Drawable? = null

    init {
        val a: TypedArray = context.obtainStyledAttributes(attrs)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        return when (parent.layoutManager) {
            is GridLayoutManager -> (parent.layoutManager as GridLayoutManager).spanCount
            is StaggeredGridLayoutManager -> (parent.layoutManager as StaggeredGridLayoutManager).spanCount
            else -> -1
        }
    }

    private fun drawHorizontal(c: Canvas, parent:RecyclerView) {
        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams

            val left: Int = child.left - params.leftMargin
            val right: Int = child.right + params.rightMargin + mDivider!!.intrinsicWidth
            val top: Int = child.bottom + params.bottomMargin
            val bottom: Int = top + mDivider!!.intrinsicHeight

            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c)
        }
    }

    private fun drawVertical(c: Canvas, parent:RecyclerView) {
        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams

            val top: Int = child.top - params.topMargin
            val bottom: Int = child.bottom + params.bottomMargin
            val left: Int = child.right + params.rightMargin
            val right: Int = left + mDivider!!.intrinsicWidth

            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c)
        }
    }

    //判断是否是最后一列
    private fun isLastColumn(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {

        val layoutManager = parent.layoutManager

        when (layoutManager) {
            is GridLayoutManager -> {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                    return true
            }
            is StaggeredGridLayoutManager -> {
                if (layoutManager.orientation == StaggeredGridLayoutManager.VERTICAL) {
                    if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                        return true
                } else {
                    val count = childCount - childCount % spanCount
                    if (pos >= count)
                        return true
                }
            }
        }
        return false
    }

    //判断是否是最后一行
    private fun isLastRaw(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int):Boolean {
        val layoutManager = parent.layoutManager

        when (layoutManager) {
            is GridLayoutManager -> {
                val count = childCount - childCount % spanCount
                if (pos >= count)//如果是最后一行，则不需要绘制底部
                    return true
            }
            is StaggeredGridLayoutManager -> {
                if (layoutManager.orientation == StaggeredGridLayoutManager.VERTICAL) {
                    val count = childCount - childCount % spanCount
                    if (pos >= count)//如果是最后一行，则不需要绘制底部
                        return true
                } else {
                    if ((pos + 1) % spanCount == 0)// 如果是最后一行，则不需要绘制底部
                        return true
                }
            }
        }
        return false
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val spanCount = getSpanCount(parent)
        val childCount = parent.adapter!!.itemCount

        when {
            isLastRaw(parent, itemPosition, spanCount, childCount) -> // 如果是最后一行，则不需要绘制底部
                outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
            isLastColumn(parent, itemPosition, spanCount, childCount) -> // 如果是最后一列，则不需要绘制右边
                outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
            else -> outRect.set(0, 0, mDivider!!.intrinsicWidth, mDivider!!.intrinsicHeight)
        }
    }
}
