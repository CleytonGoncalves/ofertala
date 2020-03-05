package com.cleytongoncalves.ofertala.features.bid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.cleytongoncalves.ofertala.LOGGED_USER_ID
import com.cleytongoncalves.ofertala.R
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
    
    private var isAuctionFinished = false
    
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
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_24dp)
        
        loadAuctionData()
        setupRecyclerView()
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
        actionBtn.isEnabled = false
        
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
                    auction.endTime.time,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL
                )}"
                
                isAuctionFinished =
                    auction.sold || System.currentTimeMillis() >= auction.endTime.time
                
                setupActionButton(auction.sellerId2 == LOGGED_USER_ID)
                setupAuctionProgress()
            }
    }
    
    private fun setupAuctionProgress() {
        if (!isAuctionFinished) return
        
        auction_progress.apply {
            isIndeterminate = false
            progress = 0
        }
        
        end_time.text = "Finished!"
    }
    
    private fun setupActionButton(isSeller: Boolean) {
        if (isSeller)
            setupSellerActionButton()
        else
            setupBidderActionButton()
        
        actionBtn.isEnabled = !isAuctionFinished
        actionBtn.visibility = View.VISIBLE
    }
    
    private fun setupSellerActionButton() {
        actionBtn.text = "Finish auction"
        
        actionBtn.setOnClickListener {
            it.isEnabled = false
            
            firestore.runTransaction { transaction ->
                val auctionDoc = firestore
                    .collection("/auctions")
                    .document(auctionId!!)
                
                // All reads must be done before writes
                val auctionSnap = transaction.get(auctionDoc)
                
                val bidderDoc = auctionDoc
                    .collection("/bids")
                    .document(auctionSnap.getString("lastBidId")!!)
                
                transaction.update(auctionDoc, "sold", true)
                transaction.update(bidderDoc, "winner", true)
            }.addOnFailureListener { exception -> Timber.e(exception) }
        }
    }
    
    private fun setupBidderActionButton() {
        actionBtn.text = "Make a bid"
        
        actionBtn.setOnClickListener {
            it.isEnabled = false
            
            val auction = firestore
                .collection("/auctions")
                .document(auctionId!!)
            
            firestore.runTransaction { transaction ->
                    // All reads must be done before writes
                    val auctionSnap = transaction.get(auction)
                    val bidderSnap = transaction.get(firestore.document("/users/${LOGGED_USER_ID}"))
                    
                    val auctionAsk = auctionSnap.getDouble("currentAsk")!!
                    
                    val newBidRef = auction.collection("/bids").document()
                    val newBid = Bid(
                        newBidRef.id,
                        auctionAsk,
                        bidderSnap.id,
                        bidderSnap.getString("name")!!,
                        Date(),
                        false,
                        auctionSnap.getString("title")!!
                    )
                    
                    val newAskValue = auctionAsk + auctionSnap.getDouble("minIncrement")!!
                    
                    transaction.set(newBidRef, newBid)
                    transaction.update(auction, "currentAsk", newAskValue)
                    transaction.update(auction, "lastBidId", newBid.id)
                }
                .addOnFailureListener { exception -> Timber.e(exception) }
                .addOnCompleteListener { actionBtn.isEnabled = !isAuctionFinished }
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