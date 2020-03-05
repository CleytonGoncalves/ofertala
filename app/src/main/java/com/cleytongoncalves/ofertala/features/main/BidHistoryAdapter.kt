package com.cleytongoncalves.ofertala.features.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cleytongoncalves.ofertala.R
import com.cleytongoncalves.ofertala.data.model.Bid
import com.cleytongoncalves.ofertala.features.main.BidHistoryAdapter.BidViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_bid_history.*

class BidHistoryAdapter internal constructor(options: FirestoreRecyclerOptions<Bid>) :
    FirestoreRecyclerAdapter<Bid, BidViewHolder>(options) {
    
    override fun onBindViewHolder(
        bidVH: BidViewHolder, position: Int, bid: Bid
    ) {
        bidVH.setTitle(bid.auctionTitle)
        bidVH.setValue("\$${bid.value}")
        bidVH.showWinnerIcon(bid.winner)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bid_history, parent, false)
        return BidViewHolder(view)
    }
    
    inner class BidViewHolder internal constructor(override val containerView: View?) :
        RecyclerView.ViewHolder(containerView!!), LayoutContainer {
    
        internal fun showWinnerIcon(show: Boolean) {
            winnerImg.visibility = if (show) View.VISIBLE else View.GONE
        }
        
        internal fun setTitle(title: String) {
            auction_title.text = title
        }
        
        internal fun setValue(value: String) {
            bid_value.text = value
        }
    }
}