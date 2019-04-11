package com.zyf.color.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zyf.color.bean.ColorBean
import com.zyf.color.R

/**
 * 颜色适配器
 */
class ColorAdapter(private val context: Context, val data:MutableList<ColorBean>):
    RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_color, p0, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = data.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tvColor.text = "#" + Integer.toHexString(data[p1].color)
        p0.tvColor.setBackgroundColor(data[p1].color)
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){
        val tvColor: TextView = itemView!!.findViewById(R.id.tvColor)
    }
}