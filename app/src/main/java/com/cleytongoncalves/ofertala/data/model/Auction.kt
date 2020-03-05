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
    val currentAskBidId: String,
    val img: String?
) {
    
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
        null
    )
}