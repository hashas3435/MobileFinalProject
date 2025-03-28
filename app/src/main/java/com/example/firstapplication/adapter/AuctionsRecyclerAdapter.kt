package com.example.firstapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firstapplication.R
import com.example.firstapplication.databinding.AuctionListRowBinding
import com.example.firstapplication.model.Auction

class AuctionsRecyclerAdapter(private var auctions: List<Auction>?) :
    RecyclerView.Adapter<AuctionViewHolder>()  {

    var listener: OnItemClickListener? = null

    fun update(auctions: List<Auction>?) {
        this.auctions = auctions
    }

    override fun getItemCount(): Int = auctions?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AuctionListRowBinding.inflate(inflater, parent, false)
        return AuctionViewHolder(binding, this.listener)
    }

    override fun onBindViewHolder(holder: AuctionViewHolder, position: Int) {
        holder.bind(
            auction = auctions?.get(position),
            position = position
        )
    }

}