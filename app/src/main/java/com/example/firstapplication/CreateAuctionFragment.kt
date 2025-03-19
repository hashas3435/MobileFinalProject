package com.example.firstapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.firstapplication.databinding.FragmentCreateAuctionBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateAuctionFragment : Fragment() {
    private var binding: FragmentCreateAuctionBinding? = null
    private var pickedTimestamp: Long = MaterialDatePicker.todayInUtcMilliseconds()
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    private val dateFormater = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateAuctionBinding.inflate(inflater, container, false)

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                binding.itemImageView.setImageBitmap(bitmap)
                didSetProfileImage = true
            }
        binding.takePictureButton.setOnClickListener {
            cameraLauncher?.launch(null)
        }

        binding.expireDateDatePickerButton.setOnClickListener {
            showFutureDatePicker()
        }

        binding.pickedTimeTextView.text =
            getString(R.string.auction_expire_at, dateFormater.format(Date(pickedTimestamp)))

        binding.createAuctionButton.setOnClickListener {
//            val auction = Auction(
//                title = binding.titleEditText.text.toString(),
//                currentBid = binding.startingBidTextField.text.toString().toInt(),
//                endDate = pickedTimestamp,
//                imageUrl = ""
//            )
//            AuctionModel.shared.addAuction(auction) { }
        }

        this.binding = binding
        return binding.root
    }

    private fun showFutureDatePicker() {
        val today = Calendar.getInstance().timeInMillis

        val datePickerConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(today))
            .build()

        val datePicker = MaterialDatePicker.Builder
            .datePicker()
            .setSelection(today)
            .setCalendarConstraints(datePickerConstraints)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedTimestamp ->
            this@CreateAuctionFragment.pickedTimestamp = selectedTimestamp
            binding?.pickedTimeTextView?.text =
                getString(R.string.auction_expire_at, dateFormater.format(Date(pickedTimestamp)))
        }

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}