package com.example.firstapplication.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapplication.R
import com.example.firstapplication.model.Auction

interface OnItemClickListener {
    fun onItemClick(position: Int)
    fun onItemClick(auction: Auction?)
}

class AuctionViewHolder(
    itemView: View,
    listener: OnItemClickListener?): RecyclerView.ViewHolder(itemView) {

    private var nameField: TextView? = null
    private var idField: TextView? = null
    private var auction: Auction? = null

    init {
        nameField = itemView.findViewById(R.id.auction_row_name_text_view)
        idField = itemView.findViewById(R.id.auction_row_id_text_view)

        itemView.setOnClickListener {
            listener?.onItemClick(auction)
        }
    }

    fun bind(auction: Auction?, position: Int) {
        this.auction = auction
        nameField?.text = auction?.title
        idField?.text = auction?.description
    }
}