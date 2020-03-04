package com.cleytongoncalves.ofertala.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.util.*

@Suppress("unused")
data class Auction(
    @DocumentId
    val id: String,
    val title: String,
    val description: String,
    val isSold: Boolean,
    val startVal: Double,
    val minIncrement: Double,
    val startTime: Date,
    val endTime: Date,
    val sellerId: DocumentReference?,
    val sellerName: String,
    val currentAsk: Double
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
    null,
    "",
        0.00
    )
}