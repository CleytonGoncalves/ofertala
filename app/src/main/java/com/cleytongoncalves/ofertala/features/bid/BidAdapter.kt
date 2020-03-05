package com.cleytongoncalves.ofertala.features.bid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cleytongoncalves.ofertala.R
import com.cleytongoncalves.ofertala.data.model.Bid
import com.cleytongoncalves.ofertala.features.bid.BidAdapter.BidViewHolder
import com.cleytongoncalves.ofertala.util.loadImageFromUrl
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_bid.*

class BidAdapter internal constructor(options: FirestoreRecyclerOptions<Bid>) :
    FirestoreRecyclerAdapter<Bid, BidViewHolder>(options) {
    
    override fun onBindViewHolder(bidVH: BidViewHolder, position: Int, bid: Bid) {
        bidVH.setBidderName(bid.bidderName)
        bidVH.setBidValue("\$${bid.value}")
        bidVH.showWinnerIcon(bid.winner)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bid, parent, false)
        return BidViewHolder(view)
    }
    
    inner class BidViewHolder internal constructor(override val containerView: View?) :
        RecyclerView.ViewHolder(containerView!!), LayoutContainer {
        
        init {
            containerView!!
        }
        
        internal fun setBidderImage(imageUrl: String) {
            bidder_img.loadImageFromUrl(imageUrl)
        }
        
        internal fun showWinnerIcon(show: Boolean) {
            winnerImg.visibility = if (show) View.VISIBLE else View.GONE
        }
        
        internal fun setBidderName(name: String) {
            bidder_name.text = name
        }
        
        internal fun setBidValue(value: String) {
            bid_value.text = value
        }
    }
}