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
    val timestamp: Date,
    val winner: Boolean,
    val auctionTitle: String
) {
    
    constructor() : this(
        "",
        0.00,
        "",
        "",
        Date(),
        false,
        ""
    )
}