package com.opdeveloper.pedidos.db.models

data class Order (
    var ProductoId: String? = null,
    var ProductoName: String? = null,
    var Cantidad: Int?= null,
    var Precio: Double?= null,
    var Descuento: Double? = null
)