package com.example.firstapplication

import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
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

        binding.setBidTextEdit.doAfterTextChanged { validateBidInput() }
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

        AuctionModel.shared.getAuctionById(auctionId) { auctionData, exception ->
            if (exception !== null) {
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch auction: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (auctionData !== null) {
                this@AuctionRoomFragment.auction = auctionData
                updateAuctionView(auctionData)
            } else {
                Toast.makeText(requireContext(), "auction not found", Toast.LENGTH_SHORT).show()
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
            AuctionModel.shared.placeBid(it, newBid) { success, exception ->
                binding.progressBar.visibility = View.GONE
                if (success) {
                    onSuccessPlaceBid(newBid)
                } else {
                    onFailedPlaceBid(exception)
                }
            }
        }
    }

    private fun onSuccessPlaceBid(newBid: Double) {
        val binding = getBinding()
        binding.currentBidTextView.text = "current bid: $${String.format("%.2f", newBid)}"
        binding.setBidTextEdit.text?.clear()
        binding.setBidButton.isEnabled = false
        Toast.makeText(requireContext(), "Bid placed successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun onFailedPlaceBid(exception: Exception?) {
        if (exception !== null) {
            Toast.makeText(
                requireContext(),
                "Failed to place bid: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "Bid is too low",
                Toast.LENGTH_SHORT
            ).show()
            fetchAuction()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(auctionId: String) =
            AuctionRoomFragment().apply {
                arguments = Bundle().apply {
                    putString(AUCTION_ID_PARAM, auctionId)
                }
            }
    }
}