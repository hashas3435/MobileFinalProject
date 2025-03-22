package com.example.firstapplication.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapplication.R
import com.example.firstapplication.databinding.AuctionListRowBinding
import com.example.firstapplication.model.Auction
import java.time.LocalDate

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
        binding.auctionRowEndDate.text = "${auction?.endDate}"
    }
}