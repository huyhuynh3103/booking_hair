package com.example.hair_booking.model

data class Service(
    private var _id: String,
    private var _title: String,
    private var _price: Int,
    private var _description: String
) {

    val id: String = _id
    val title: String = _title
    val price: Int = _price
    val description: String = _description
}