package com.example.firstapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstapplication.adapter.AuctionsRecyclerAdapter
import com.example.firstapplication.adapter.OnItemClickListener
import com.example.firstapplication.databinding.FragmentAuctionsListBinding
import com.example.firstapplication.model.Auction
import com.example.firstapplication.model.AuctionModel
import com.example.firstapplication.model.AuctionsListViewModel


class AuctionsListFragment : Fragment() {
    private var binding: FragmentAuctionsListBinding? = null
    private var adapter: AuctionsRecyclerAdapter? = null
    private var viewModel: AuctionsListViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this)[AuctionsListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAuctionsListBinding.inflate(inflater, container, false)

        binding?.recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)
        binding?.recyclerView?.layoutManager = layoutManager

        val DEFAULT_AUCTIONS: List<Auction> = mutableListOf(Auction(id="1",
            title="1",
            description="1",
            endDate = 0L,
            imageUrl="1",
            currentBid=0.0,
            seller="1"))

        adapter = AuctionsRecyclerAdapter(viewModel?.auctions ?: DEFAULT_AUCTIONS)

        adapter?.listener = object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(auction: Auction?) {
                auction?.let {
                    val action = AuctionsListFragmentDirections.
                    actionAuctionsListFragmentToAuctionRoomFragment(it.id)
                    binding?.root?.let {
                        Navigation.findNavController(it).navigate(action)
                    }
                }
            }
        }

        binding?.recyclerView?.adapter = adapter

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        getAllAuctions()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllAuctions() {

        binding?.progressBar?.visibility = View.VISIBLE

        AuctionModel.shared.getAllAuctions {
            viewModel?.set(auctions = it)
            adapter?.update(it)
            adapter?.notifyDataSetChanged()

            binding?.progressBar?.visibility = View.GONE
        }

    }
}