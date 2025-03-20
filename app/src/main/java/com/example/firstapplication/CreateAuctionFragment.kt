package com.example.firstapplication

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.firstapplication.databinding.FragmentCreateAuctionBinding
import com.example.firstapplication.model.Auction
import com.example.firstapplication.model.AuctionModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateAuctionFragment : Fragment() {
    private var binding: FragmentCreateAuctionBinding? = null
    private var pickedTimestamp: Long = 0
    private var cameraLauncher: ActivityResultLauncher<Void?>? = null
    private var didSetProfileImage = false
    private val dateFormater = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCreateAuctionBinding.inflate(inflater, container, false)
        this.binding = binding

        cameraLauncher = createCameraLauncher()
        binding.takePictureButton.setOnClickListener {
            cameraLauncher?.launch(null)
        }
        binding.expireDateDatePickerButton.setOnClickListener {
            showFutureDatePicker()
        }
        updatePickedDate(Calendar.getInstance().timeInMillis)
        binding.createAuctionButton.setOnClickListener(::onCreateAuctionClick)
        binding.cancelButton.setOnClickListener { view ->
            Navigation.findNavController(view).popBackStack()
        }

        return binding.root
    }

    private fun getBinding(): FragmentCreateAuctionBinding {
        return binding ?: throw IllegalStateException("CreateAuctionFragment binding is null")
    }

    private fun createCameraLauncher(): ActivityResultLauncher<Void?> {
        return registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                getBinding().itemImageView.setImageBitmap(it)
                didSetProfileImage = true
            }
        }
    }

    private fun updatePickedDate(timestamp: Long) {
        pickedTimestamp = timestamp
        getBinding().pickedTimeTextView.text =
            getString(R.string.auction_expire_at, dateFormater.format(Date(timestamp)))
    }

    private fun onCreateAuctionClick(view: View) {
        val binding = getBinding()
        val auction = Auction(
            id = "",
            title = binding.titleEditText.text.toString(),
            description = binding.descriptionEditText.text.toString(),
            currentBid = binding.startingBidEditText.text.toString().toInt(),
            endDate = pickedTimestamp,
            imageUrl = "",
            seller = "" // TODO: use logged user
        )

        if (didSetProfileImage) {
            binding.itemImageView.isDrawingCacheEnabled = true
            binding.itemImageView.buildDrawingCache()
            val bitmap = (binding.itemImageView.drawable as BitmapDrawable).bitmap
            AuctionModel.shared.addAuction(auction, bitmap) {
                binding.progressBar.visibility = View.GONE
                Navigation.findNavController(view).popBackStack()
            }
        } else {
            AuctionModel.shared.addAuction(auction, null) {
                binding.progressBar.visibility = View.GONE
                Navigation.findNavController(view).popBackStack()
            }
        }
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
            updatePickedDate(selectedTimestamp)
        }

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}