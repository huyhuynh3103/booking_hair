package com.example.hair_booking.services.db

abstract class DatabaseAbstract<T> {
    abstract suspend fun find(query: T): Any?
    abstract suspend fun save(data: Any?): Any?
    abstract suspend fun findAll(): Any?
    abstract suspend fun findById(id: Any?): Any?
    abstract suspend fun updateOne(id: Any?, updateDoc: Any?): Any?
    abstract suspend fun delete(id: Any?): Any?
    abstract suspend fun add(data: Any?): Any?
}