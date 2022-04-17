package com.example.hair_booking.services.db

abstract class DatabaseAbstract {
    abstract  fun find(data: Any?): Any?
    abstract  fun save(data: Any?): Any?
    abstract  fun findAll(): Any?
    abstract  fun findById(id: Any?): Any?
    abstract suspend fun updateOne(id: String?, updateDoc: Any?): Any?
    abstract  fun delete(data: Any?): Any?
}