package com.idz.colman24class2

import androidx.lifecycle.ViewModel
import com.example.firstapplication.model.Auction

class AuctionsListViewModel : ViewModel() {
    private var _auctions: List<Auction>? = null
    var auctions: List<Auction>?
        get() = _auctions
        private set(value) {
            _auctions = value
        }

    fun set(students: List<Auction>?) {
        this.auctions = students
    }
}