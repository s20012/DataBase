package com.example.databasetest.customclass

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.databasetest.R


class CustomListAdapter(context: Context, private val data: MutableList<ListItem>, private val resource: Int): BaseAdapter() {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p0: Int): ListItem {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return data[p0].id
    }

    @SuppressLint("SimpleDateFormat")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val item = getItem(p0)
        val view = p1 ?: inflater.inflate(resource, null)

        view.findViewById<TextView>(R.id.text_title).text = item.title
        view.findViewById<TextView>(R.id.day_text).text = item.day
        return view
    }
}


