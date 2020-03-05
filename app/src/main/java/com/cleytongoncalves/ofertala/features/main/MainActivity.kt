package com.cleytongoncalves.ofertala.features.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleytongoncalves.ofertala.LOGGED_USER_ID
import com.cleytongoncalves.ofertala.R
import com.cleytongoncalves.ofertala.data.model.Auction
import com.cleytongoncalves.ofertala.data.model.Bid
import com.cleytongoncalves.ofertala.features.base.BaseActivity
import com.cleytongoncalves.ofertala.features.bid.BidActivity
import com.cleytongoncalves.ofertala.features.main.AuctionAdapter.AuctionClickListener
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    
    private var auctionAdapter: AuctionAdapter? = null
    private var bidHistoryAdapter: BidHistoryAdapter? = null
    
    override fun layoutId() = R.layout.activity_main
    
    override fun onStart() {
        super.onStart()
        auctionAdapter!!.startListening()
        bidHistoryAdapter!!.startListening()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setSupportActionBar(main_toolbar)
        setupAuctionRecyclerView(getAuctionFirestoreAdapterOptions(null))
        setupBidRecyclerView(getBidFirestoreAdapterOptions())
        
        title = "Live Auctions"
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
        if (intent?.action == Intent.ACTION_SEARCH && intent.getStringExtra(SearchManager.QUERY) != null) {
            val filter = intent.getStringExtra(SearchManager.QUERY)
            auctionAdapter?.updateOptions(getAuctionFirestoreAdapterOptions(filter))
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
            
            setOnQueryTextListener(object : OnQueryTextListener {
                var lastText: String? = null
                
                override fun onQueryTextChange(newText: String): Boolean {
                    if (lastText != null && lastText!!.length > 1 && newText.isEmpty()) {
                        lastText = ""
                        auctionAdapter?.updateOptions(getAuctionFirestoreAdapterOptions(null))
                        onActionViewCollapsed()
                        return false
                    }
                    
                    lastText = newText
                    return false
                }
                
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }
            })
        }
        
        return true
    }
    
    override fun onStop() {
        super.onStop()
        auctionAdapter?.stopListening()
        bidHistoryAdapter?.stopListening()
    }
    
    private fun setupAuctionRecyclerView(firestoreOptions: FirestoreRecyclerOptions<Auction>) {
        auctionAdapter = AuctionAdapter(firestoreOptions)
        
        auctionAdapter!!.setClickListener(object : AuctionClickListener {
            override fun onAuctionClick(auctionId: String) {
                startActivity(BidActivity.getStartIntent(this@MainActivity, auctionId))
            }
        })
    
        recyclerAuction?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = auctionAdapter
        }
    }
    
    private fun setupBidRecyclerView(firestoreOptions: FirestoreRecyclerOptions<Bid>) {
        bidHistoryAdapter = BidHistoryAdapter(firestoreOptions)
        
        recyclerBid?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bidHistoryAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun getAuctionFirestoreAdapterOptions(titleFilter: String?): FirestoreRecyclerOptions<Auction> {
        var query: Query = Firebase.firestore
            .collection("/auctions")
        
        if (titleFilter != null)
            query = query.whereArrayContainsAny("searchTerms", titleFilter.toLowerCase().split(" "))
        
        query = query.orderBy("startTime", DESCENDING)
        
        return FirestoreRecyclerOptions.Builder<Auction>()
            .setQuery(query, Auction::class.java)
            .build()
    }
    
    private fun getBidFirestoreAdapterOptions(): FirestoreRecyclerOptions<Bid> {
        val query: Query = Firebase.firestore
            .collectionGroup("bids")
            .whereEqualTo("bidderId", LOGGED_USER_ID)
            .orderBy("timestamp", DESCENDING)
            .limit(3)
        
        return FirestoreRecyclerOptions.Builder<Bid>()
            .setQuery(query, Bid::class.java)
            .build()
    }
}