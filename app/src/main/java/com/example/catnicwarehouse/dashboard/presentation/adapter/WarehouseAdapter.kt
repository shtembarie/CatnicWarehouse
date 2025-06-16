package com.example.catnicwarehouse.dashboard.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.catnicwarehouse.R
import com.example.shared.repository.dashboard.WarehousesResponseModelItem

class WarehouseAdapter(private val context: Context, private val locations: List<WarehousesResponseModelItem>) : BaseAdapter() {

    override fun getCount(): Int = locations.size

    override fun getItem(position: Int): Any = locations[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_custom_item, parent, false)
        }

        val location = locations[position]
        val textView = view?.findViewById<TextView>(R.id.spinner_text)
        val arrowView = view?.findViewById<ImageView>(R.id.spinner_arrow)

        // Set the name in the TextView
        textView?.text = location.name

        // Show the arrow (optional, if you want to show the arrow here)
        arrowView?.visibility = View.VISIBLE

        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_custom_item, parent, false)
        }

        val location = locations[position]
        val textView = view?.findViewById<TextView>(R.id.spinner_text)

        // Set the name in the TextView
        textView?.text = location.name

        // Hide the arrow in the dropdown
        val arrowView = view?.findViewById<ImageView>(R.id.spinner_arrow)
        arrowView?.visibility = View.GONE

        return view!!
    }
}