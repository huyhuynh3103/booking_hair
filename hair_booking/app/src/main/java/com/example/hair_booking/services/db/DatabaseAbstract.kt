package com.example.hair_booking.services.db

abstract class DatabaseAbstract<in T> {
    abstract suspend fun find(query: T):Any
    abstract suspend fun save(data:Any):Any?
    abstract  fun findAll():Any
    abstract  fun findById(id:String):Any
    abstract suspend fun updateOne(id:String, updateDoc:Any):Any
    abstract  fun delete(data:Any):Any
}