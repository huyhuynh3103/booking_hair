package com.example.hair_booking.model

data class Salon(
    private var _id: String,
    private var _name: String,
    private var _avatar: String,
    private var _description: String
) {

    val id: String = _id
    val name: String = _name
    val avatar: String = _avatar
    val description: String = _description
}