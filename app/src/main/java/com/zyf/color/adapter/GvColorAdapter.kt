package com.zyf.color.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.zyf.color.bean.ColorBean
import com.zyf.color.R

/**
 * Created by zyf on 2019/4/11.
 */
class GvColorAdapter(private val context: Context, private val data: List<ColorBean>) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(i: Int): ColorBean {
        return data[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val holder: ViewHolder?
        var view = p1

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_color, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }
        val bean: ColorBean = data[p0]
        holder.tvColor.text = "#" + Integer.toHexString(bean.color)
        holder.tvColor.setBackgroundColor(bean.color)
        return view!!
    }

    inner class ViewHolder(itemView: View?){
        val tvColor: TextView = itemView!!.findViewById(R.id.tvColor)
    }

}
