package com.cleytongoncalves.ofertala.data.model

import com.google.firebase.firestore.DocumentId
import java.util.*

@Suppress("unused")
data class Bid(
    @DocumentId
    val id: String,
    val value: Double,
    val bidderId: String,
    val bidderName: String,
    val bidderImg: String?,
    val timestamp: Date,
    val winner: Boolean,
    val auctionTitle: String
) {
    companion object {
        const val COLLECTION_NAME = "bids"
    }
    
    constructor() : this(
        "",
        0.00,
        "",
        "",
        null,
        Date(),
        false,
        ""
    )
}