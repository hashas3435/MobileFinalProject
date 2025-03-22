package com.example.firstapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.firstapplication.databinding.FragmentAuctionsListBinding
import com.example.firstapplication.databinding.FragmentCreateAuctionBinding


class AuctionsListFragment : Fragment() {
    private var binding: FragmentAuctionsListBinding? = null
    private var adapter: AuctionsRecyclerAdapter? = null
    private var viewModel: StudentsListViewModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuctionsListBinding.inflate(inflater, container, false)
        this.binding = binding

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}