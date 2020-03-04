package com.cleytongoncalves.ofertala.features.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
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
        setupRecyclerView(getFirestoreAdapterOptions(null))
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
        if (intent?.action == Intent.ACTION_SEARCH && intent.getStringExtra(SearchManager.QUERY) != null) {
            val filter = intent.getStringExtra(SearchManager.QUERY)
            auctionAdapter?.updateOptions(getFirestoreAdapterOptions(filter))
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
                        auctionAdapter?.updateOptions(getFirestoreAdapterOptions(null))
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
    }
    
    override fun onAuctionClick(auctionId: String) {
        startActivity(BidActivity.getStartIntent(this, auctionId))
    }
    
    private fun setupRecyclerView(firestoreOptions: FirestoreRecyclerOptions<Auction>) {
        progress.visibility = View.VISIBLE
        
        fun hideProgressBar() {
            progress.visibility = View.GONE
        }
        
        auctionAdapter = AuctionAdapter(firestoreOptions, ::hideProgressBar)
        auctionAdapter!!.setClickListener(this)
        
        recyclerAuction?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = auctionAdapter
        }
    }
    
    private fun getFirestoreAdapterOptions(titleFilter: String?): FirestoreRecyclerOptions<Auction> {
        var query: Query = Firebase.firestore
            .collection("/auctions")
        
        if (titleFilter != null)
            query = query.whereArrayContainsAny("searchTerms", titleFilter.toLowerCase().split(" "))
        
        query = query.orderBy("startTime", Query.Direction.DESCENDING)
        
        return FirestoreRecyclerOptions.Builder<Auction>()
            .setQuery(query, Auction::class.java)
            .build()
    }
}