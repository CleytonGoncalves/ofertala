package com.cleytongoncalves.ofertala.features.main

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleytongoncalves.ofertala.R
import com.cleytongoncalves.ofertala.data.model.Auction
import com.cleytongoncalves.ofertala.features.base.BaseActivity
import com.cleytongoncalves.ofertala.features.bid.BidActivity
import com.cleytongoncalves.ofertala.features.main.AuctionAdapter.AuctionClickListener
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), AuctionClickListener {

    private var auctionAdapter: AuctionAdapter? = null

    override fun layoutId() = R.layout.activity_main

    override fun onStart() {
        super.onStart()
        auctionAdapter!!.startListening()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(main_toolbar)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val options = FirestoreRecyclerOptions.Builder<Auction>()
            .setQuery(getAuctionDataQuery(), Auction::class.java)
            .build()

        auctionAdapter = AuctionAdapter(options)
        auctionAdapter!!.setClickListener(this)

        recyclerAuction?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = auctionAdapter
        }
    }

    override fun onStop() {
        super.onStop()
        auctionAdapter?.stopListening()
    }

    override fun onAuctionClick(auctionId: String) {
        startActivity(BidActivity.getStartIntent(this, auctionId))
    }

    private fun getAuctionDataQuery(): Query {
        return Firebase.firestore
            .collection("/auctions")
            .orderBy("startTime", Query.Direction.DESCENDING)
    }
}