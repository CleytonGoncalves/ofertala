package com.cleytongoncalves.ofertala.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.util.*

@Suppress("unused")
data class Bid(
    @DocumentId
    val id: String,
    val value: Double,
    val bidderId: DocumentReference?,
    val bidderName: String,
    val timestamp: Date,
    val isWinner: Boolean
) {
    constructor() : this(
        "",
        0.00,
        null,
        "",
        Date(),
        false
    )
}