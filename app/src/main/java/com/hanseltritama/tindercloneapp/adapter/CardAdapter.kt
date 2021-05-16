package com.hanseltritama.tindercloneapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.hanseltritama.tindercloneapp.R
import com.hanseltritama.tindercloneapp.data.Cards

class CardAdapter(context: Context, resId: Int, items: List<Cards>)
    : ArrayAdapter<Cards>(context, resId, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cardItem: Cards? = getItem(position)
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item, parent, false)

        view.findViewById<TextView>(R.id.card_name)?.text = cardItem?.name
        view.findViewById<ImageView>(R.id.card_image)?.setImageResource(R.mipmap.ic_launcher)

        return view
    }
}