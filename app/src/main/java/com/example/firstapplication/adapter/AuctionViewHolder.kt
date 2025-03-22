package com.example.firstapplication.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapplication.R
import com.example.firstapplication.model.Auction
import java.time.LocalDate

interface OnItemClickListener {
    fun onItemClick(position: Int)
    fun onItemClick(auction: Auction?)
}

class AuctionViewHolder(
    itemView: View,
    listener: OnItemClickListener?): RecyclerView.ViewHolder(itemView) {

    private var titleField: TextView? = null
    private var descriptionField: TextView? = null
    private var currentBidField: TextView? = null
    private var endDateField: TextView? = null
    private var auction: Auction? = null

    init {
        titleField = itemView.findViewById(R.id.auction_row_title)
        descriptionField = itemView.findViewById(R.id.auction_row_description)
        currentBidField = itemView.findViewById(R.id.auction_row_current_bid)
        endDateField = itemView.findViewById(R.id.auction_row_end_date)

        itemView.setOnClickListener {
            listener?.onItemClick(auction)
        }
    }

    fun bind(auction: Auction?, position: Int) {
        this.auction = auction
        titleField?.text = auction?.title
        descriptionField?.text = auction?.description
        currentBidField?.text = "$${auction?.currentBid}"
        endDateField?.text = "${auction?.endDate}"
    }
}