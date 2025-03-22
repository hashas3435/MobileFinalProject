package com.example.firstapplication.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapplication.R
import com.example.firstapplication.databinding.AuctionListRowBinding
import com.example.firstapplication.model.Auction
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

interface OnItemClickListener {
    fun onItemClick(position: Int)
    fun onItemClick(auction: Auction?)
}

class AuctionViewHolder(
    private val binding: AuctionListRowBinding,
    listener: OnItemClickListener?): RecyclerView.ViewHolder(binding.root) {

    private var auction: Auction? = null

    init {
        itemView.setOnClickListener {
            listener?.onItemClick(auction)
        }
    }

    fun bind(auction: Auction?, position: Int) {
        this.auction = auction
        binding.auctionRowTitle.text = auction?.title
        binding.auctionRowDescription.text = auction?.description
        binding.auctionRowCurrentBid.text = "$${auction?.currentBid}"
        binding.auctionRowEndDate.text = getEndDateText(auction?.endDate)

        if (auction?.imageUrl !== null && auction.imageUrl.isNotBlank()) {
            Picasso.get()
                .load(auction.imageUrl)
                .placeholder(R.drawable.auction_logo)
                .into(binding.auctionRowImage)
        }
    }

    private fun getEndDateText(endDate: Long?): String {
        if (endDate === null ){
            return ""
        }
        return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date(endDate))
    }
}