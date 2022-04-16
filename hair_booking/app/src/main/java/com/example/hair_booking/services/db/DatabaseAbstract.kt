package com.example.hair_booking.services.db

abstract class DatabaseAbstract {
    abstract  fun find(query:Any):Any
    abstract suspend fun save(data:Any):Any?
    abstract  fun findAll():Any
    abstract  fun findById(id:String):Any
    abstract suspend fun updateOne(id:String, updateDoc:Any):Any
    abstract  fun delete(data:Any):Any
}