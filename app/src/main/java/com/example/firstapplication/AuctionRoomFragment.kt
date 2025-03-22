package com.example.firstapplication

import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.firstapplication.databinding.FragmentAuctionRoomBinding
import com.example.firstapplication.model.Auction
import com.example.firstapplication.model.AuctionModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val AUCTION_ID_PARAM = "auctionId"

class AuctionRoomFragment : Fragment() {
    private var binding: FragmentAuctionRoomBinding? = null
    private var auctionId: String? = null
    private var auction: Auction? = null
    private var sellerPhone: String = "0527212004"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            auctionId = it.getString(AUCTION_ID_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuctionRoomBinding.inflate(inflater, container, false)
        this.binding = binding

        val today = Calendar.getInstance().timeInMillis
        binding.expireDateTextView.text = createExpireDateText(today)
        binding.currentBidTextView.text = createCurrentBidText(0.0)
        fetchAuction()

        binding.setBidTextEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editText: Editable?) {
                validateBidInput()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.contactSellerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$sellerPhone"))
            startActivity(intent)
        }
        binding.setBidButton.setOnClickListener { placeBid() }

        return binding.root
    }

    private fun getBinding(): FragmentAuctionRoomBinding {
        return binding ?: throw IllegalStateException("AuctionRoomFragment binding is null")
    }

    private fun validateBidInput() {
        val binding = getBinding()
        val bidText = binding.setBidTextEdit.text.toString()
        val bidAmount = bidText.toDoubleOrNull()
        val currentBid = auction?.currentBid ?: 0.0

        if (bidAmount == null || bidAmount <= currentBid) {
            binding.setBidTextEdit.error = "Bid must be higher than ${formatBid(currentBid)}"
            binding.setBidButton.isEnabled = false
        } else {
            binding.setBidTextEdit.error = null
            binding.setBidButton.isEnabled = true
        }
    }

    private fun fetchAuction() {
        val binding = getBinding()
        val auctionId = this.auctionId ?: throw IllegalArgumentException("auctionId is null")
        binding.progressBar.visibility = View.VISIBLE

        AuctionModel.shared.getAuctionById(auctionId) { auctionData ->
            if (auctionData !== null) {
                this@AuctionRoomFragment.auction = auctionData
                updateAuctionView(auctionData)
            } else {
                Toast.makeText(requireContext(), "failed to get auction", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(binding.root).popBackStack()
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun formatBid(bid: Double): String {
        return "$${String.format("%.2f", bid)}"
    }

    private fun createCurrentBidText(bid: Double): String {
        return "current bid: ${formatBid(bid)}"
    }

    private fun updateAuctionView(auction: Auction) {
        val binding = getBinding()

        binding.titleTextView.text = auction.title
        binding.descriptionTextView.text = auction.description
        binding.currentBidTextView.text = createCurrentBidText(auction.currentBid)
        binding.setBidTextEdit.text?.clear()
        binding.expireDateTextView.text = createExpireDateText(auction.endDate)
        if (auction.imageUrl.isNotBlank()) {
            Picasso.get()
                .load(auction.imageUrl)
                .placeholder(R.drawable.auction_logo)
                .into(binding.itemImageView)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            .format(Date(timestamp))
    }

    private fun createExpireDateText(timestamp: Long): String {
        return "Auction ends at ${formatTimestamp(timestamp)}"
    }

    private fun placeBid() {
        val binding = getBinding()
        val newBid = binding.setBidTextEdit.text.toString().toDouble()
        auctionId?.let {
            binding.progressBar.visibility = View.VISIBLE
            AuctionModel.shared.placeBid(it, newBid) { success ->
                binding.progressBar.visibility = View.GONE
                if (success) {
                    onSuccessPlaceBid(newBid)
                } else {
                    onFailedPlaceBid()
                }
            }
        }
    }

    private fun onSuccessPlaceBid(newBid: Double) {
        val binding = getBinding()
        binding.currentBidTextView.text = "current bid: $${String.format("%.2f", newBid)}"
        binding.setBidTextEdit.text?.clear()
        binding.setBidButton.isEnabled = false
        auction?.currentBid = newBid
        Toast.makeText(requireContext(), "Bid placed successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun onFailedPlaceBid() {
        Toast.makeText(
            requireContext(),
            "Failed to place bid",
            Toast.LENGTH_SHORT
        ).show()
        fetchAuction()
    }
}