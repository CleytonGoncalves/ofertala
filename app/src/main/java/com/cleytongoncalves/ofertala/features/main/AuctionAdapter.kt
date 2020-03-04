package com.cleytongoncalves.ofertala.features.main

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cleytongoncalves.ofertala.R
import com.cleytongoncalves.ofertala.data.model.Auction
import com.cleytongoncalves.ofertala.features.main.AuctionAdapter.AuctionViewHolder
import com.cleytongoncalves.ofertala.util.loadImageFromUrl
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_bid.auction_title
import kotlinx.android.synthetic.main.item_auction.*
import java.util.*

class AuctionAdapter internal constructor(
    options: FirestoreRecyclerOptions<Auction>,
    val loadedCallback: () -> Unit
) :
    FirestoreRecyclerAdapter<Auction, AuctionViewHolder>(options) {
    
    private var clickListener: AuctionClickListener? = null
    
    override fun onViewAttachedToWindow(holder: AuctionViewHolder) {
        super.onViewAttachedToWindow(holder)
        loadedCallback()
    }
    
    override fun onBindViewHolder(
        auctionVH: AuctionViewHolder, position: Int, auction: Auction
    ) {
        auctionVH.setAuctionId(auction.id)
        auctionVH.setImage(auction.img)
        auctionVH.setTitle(auction.title)
        auctionVH.setSeller(auction.sellerName)
        auctionVH.setEndTime(auction.endTime)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuctionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_auction, parent, false)
        return AuctionViewHolder(view)
    }
    
    fun setClickListener(clickListener: AuctionClickListener) {
        this.clickListener = clickListener
    }
    
    inner class AuctionViewHolder internal constructor(override val containerView: View?) :
        RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        
        private var auctionId: String? = null
        
        init {
            containerView!!.setOnClickListener { clickListener?.onAuctionClick(auctionId!!) }
        }
        
        internal fun setAuctionId(id: String) {
            auctionId = id
        }
        
        internal fun setImage(imgSrc: String?) {
            if (imgSrc != null)
                auction_img.loadImageFromUrl(imgSrc)
            else
                auction_img.setImageResource(R.drawable.painting_placeholder)
        }
        
        internal fun setTitle(title: String) {
            auction_title.text = title
        }
        
        internal fun setSeller(seller: String) {
            auction_seller.text = seller
        }
        
        internal fun setEndTime(endTime: Date) {
            auction_endTime.text = DateUtils.getRelativeTimeSpanString(
                endTime.time,
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL
            )
        }
    }
    
    interface AuctionClickListener {
        fun onAuctionClick(auctionId: String)
    }
}