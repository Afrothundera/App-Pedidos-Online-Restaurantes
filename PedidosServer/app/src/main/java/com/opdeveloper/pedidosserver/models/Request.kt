package com.opdeveloper.pedidosserver.db.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Request(
    var requestId : String? = null,
    var phone: String? = null,
    var displayName: String? = null,
    var address: String? = null,
    var total: String? = null,
    var status: String? = "0",
    var email: String? = null,
    var userId: String? = null,
    var timestamp: Timestamp? = null,
    var lisrOrder: List<Order>? = null
)