package com.cleytongoncalves.ofertala.data.model

import com.google.firebase.firestore.DocumentId
import java.util.*

@Suppress("unused")
data class Auction(
    @DocumentId
    val id: String,
    val title: String,
    val description: String,
    val sold: Boolean,
    val startVal: Double,
    val minIncrement: Double,
    val startTime: Date,
    val endTime: Date,
    val sellerId2: String,
    val sellerName: String,
    val currentAsk: Double,
    val lastBidId: String,
    val img: String?,
    val searchTerms: List<String>?
) {
    companion object {
        const val COLLECTION_NAME = "auctions"
    }
    
    constructor() : this(
        "",
        "",
        "",
        false,
        0.00,
        0.00,
        Date(),
        Date(),
        "",
        "",
        0.00,
        "",
        null,
        null
    )
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Auction
        
        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (sold != other.sold) return false
        if (startVal != other.startVal) return false
        if (minIncrement != other.minIncrement) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (sellerId2 != other.sellerId2) return false
        if (sellerName != other.sellerName) return false
        if (currentAsk != other.currentAsk) return false
        if (lastBidId != other.lastBidId) return false
        if (img != other.img) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + sold.hashCode()
        result = 31 * result + startVal.hashCode()
        result = 31 * result + minIncrement.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + sellerId2.hashCode()
        result = 31 * result + sellerName.hashCode()
        result = 31 * result + currentAsk.hashCode()
        result = 31 * result + lastBidId.hashCode()
        result = 31 * result + (img?.hashCode() ?: 0)
        return result
    }
}