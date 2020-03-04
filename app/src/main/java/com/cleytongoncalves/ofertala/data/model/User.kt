package com.cleytongoncalves.ofertala.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String,
    val name: String
) {
    constructor() : this(
        "",
        ""
    )
}