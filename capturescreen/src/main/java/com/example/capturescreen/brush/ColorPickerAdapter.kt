package com.example.capturescreen.brush

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.capturescreen.R
import kotlinx.android.synthetic.main.color_picker_item_list.view.*

class ColorPickerAdapter(val context: Context, val mListener: InteractionListener) : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>() {
    private val colorList: List<Int> = getColorList()

    private fun getColorList(): List<Int> {
        val colorList = ArrayList<Int>()
        colorList.add(ContextCompat.getColor(context, R.color.blue_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.brown_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.green_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.orange_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.red_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.black))
        colorList.add(ContextCompat.getColor(context, R.color.red_orange_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.violet_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.white))
        colorList.add(ContextCompat.getColor(context, R.color.yellow_color_picker))
        colorList.add(ContextCompat.getColor(context, R.color.yellow_green_color_picker))
        return colorList
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.color_picker_item_list, parent, false))

    override fun getItemCount() = colorList.size

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.setColor(colorList[i])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                mListener.onColorSelected(colorList[adapterPosition])
            }
        }

        fun setColor(colorId: Int) = itemView.color_picker_view.setBackgroundColor(colorId)
    }

    interface InteractionListener {
        fun onColorSelected(colorId: Int);
    }
}