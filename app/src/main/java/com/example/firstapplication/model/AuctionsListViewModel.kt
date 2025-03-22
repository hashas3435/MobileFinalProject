package com.example.firstapplication.model

import androidx.lifecycle.ViewModel

class AuctionsListViewModel : ViewModel() {
    private var _auctions: List<Auction>? = null
    var auctions: List<Auction>?
        get() = _auctions
        private set(value) {
            _auctions = value
        }

    fun set(auctions: List<Auction>?) {
        this.auctions = auctions
    }
}