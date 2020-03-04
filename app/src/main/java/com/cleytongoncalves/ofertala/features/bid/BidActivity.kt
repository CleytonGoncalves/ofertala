package com.cleytongoncalves.ofertala.features.bid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleytongoncalves.ofertala.LOGGED_USER_ID
import com.cleytongoncalves.ofertala.R
import com.cleytongoncalves.ofertala.R.drawable
import com.cleytongoncalves.ofertala.data.model.Auction
import com.cleytongoncalves.ofertala.data.model.Bid
import com.cleytongoncalves.ofertala.features.base.BaseActivity
import com.cleytongoncalves.ofertala.util.loadImageFromUrl
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_bid.*
import timber.log.Timber
import java.util.*

class BidActivity : BaseActivity() {
    
    private val firestore: FirebaseFirestore = Firebase.firestore
    private var auctionId: String? = null
    private var bidAdapter: BidAdapter? = null
    
    companion object {
        private const val EXTRA_AUCTION_ID = "EXTRA_AUCTION_ID"
        
        fun getStartIntent(context: Context, auctionId: String): Intent {
            val intent = Intent(context, BidActivity::class.java)
            intent.putExtra(EXTRA_AUCTION_ID, auctionId)
            return intent
        }
    }
    
    override fun layoutId() = R.layout.activity_bid
    
    override fun onStart() {
        super.onStart()
        bidAdapter!!.startListening()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        auctionId = intent.getStringExtra(EXTRA_AUCTION_ID)
        if (auctionId == null) throw IllegalArgumentException("Bid Activity requires an auction id")
        
        setSupportActionBar(auction_toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(drawable.ic_keyboard_arrow_left_black_24dp)
        
        loadAuctionData()
        setupRecyclerView()
        
        setupMakeBidButton()
    }
    
    override fun onStop() {
        super.onStop()
        bidAdapter?.stopListening()
    }
    
    private fun setupRecyclerView() {
        val options =
            FirestoreRecyclerOptions.Builder<Bid>().setQuery(getBidDataQuery(), Bid::class.java)
                .build()
        
        bidAdapter = BidAdapter(options)
        
        recyclerBid?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bidAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun loadAuctionData() {
        makeBidBtn.isEnabled = false
        
        firestore.collection("/auctions").document(auctionId!!)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Timber.e(exception)
                    return@addSnapshotListener
                }
                
                val auction = snapshot!!.toObject<Auction>()!!
                
                title = "Live Auction: ${auction.title}"
                
                if (auction.img != null)
                    auction_img.loadImageFromUrl(auction.img)
                else
                    auction_img.setImageResource(R.drawable.painting_placeholder)
                
                auction_title.text = auction.title
                subtitle.text = auction.sellerName
                bidParams.text =
                    "Starts at \$${auction.startVal} · Increment of \$${auction.minIncrement}"
                currentAsk.text = "Current Ask: \$${auction.currentAsk}"
                end_time.text = "Ends at ${DateUtils.getRelativeTimeSpanString(
                    auction.endTime.time, System.currentTimeMillis(), DateUtils.YEAR_IN_MILLIS
                )}"
                
                makeBidBtn.isEnabled = true
            }
    }
    
    private fun setupMakeBidButton() {
        makeBidBtn.setOnClickListener {
            it.isEnabled = false
            
            val auction = firestore
                .collection("/auctions")
                .document(auctionId!!)
            
            firestore.runTransaction { transaction ->
                // All reads must be done before writes
                val auctionSnap = transaction.get(auction)
                val bidderSnap = transaction.get(firestore.document("/users/${LOGGED_USER_ID}"))
                
                val newAskValue =
                    auctionSnap.getDouble("currentAsk")!! + auctionSnap.getDouble("minIncrement")!!
                
                val newBidRef = auction.collection("/bids").document()
                val newBid = Bid(
                    newBidRef.id,
                    newAskValue,
                    bidderSnap.reference,
                    bidderSnap.getString("name")!!,
                    Date(),
                    false
                )
                
                transaction.update(auction, "currentAsk", newAskValue)
                transaction.set(newBidRef, newBid)
            }.addOnCompleteListener { makeBidBtn.isEnabled = true }
        }
    }
    
    private fun getBidDataQuery(): Query {
        return firestore
            .collection("/auctions")
            .document(auctionId!!)
            .collection("/bids")
            .orderBy("value", Query.Direction.DESCENDING)
            .limit(15)
    }
}