package com.opdeveloper.pedidos.db.models

import com.dbflow5.annotation.Column
import com.dbflow5.annotation.Database
import com.dbflow5.annotation.PrimaryKey
import com.dbflow5.annotation.Table
import com.opdeveloper.pedidos.db.models.pedidosdb.AppDb

@Table(database = AppDb::class, name = "Orders")
class OrderDb (
    @PrimaryKey(autoincrement = true) var id : Long = 0,
    @Column var ProductoId: String? = null,
    @Column var ProductoName: String? = null,
    @Column var Cantidad: Int?= null,
    @Column var Precio: Double?= null,
    @Column var Descuento: Double? = null
)