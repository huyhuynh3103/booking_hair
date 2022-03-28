package com.example.hair_booking.model

data class Stylist(
    private var _fullName: String,
    private var _avatar: String,
    private var _description: String,
    private var _workPlace: String
) {

    val fullName: String = _fullName
    val avatar: String = _avatar
    val description: String = _description
    val workPlace: String = _workPlace
}