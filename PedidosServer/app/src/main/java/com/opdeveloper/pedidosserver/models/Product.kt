package com.opdeveloper.pedidosserver.db.models

data class Product (
    var CategoryId : String? = null,
    var Descripcion : String? = null,
    var Image : String? = null,
    var Name : String? = null,
    var Precio : Double? = null,
    var ProductoId : String? = null
)