package com.example.hair_booking.services.db

abstract class DatabaseAbstract {
    abstract suspend fun find(query:Any):Any
    abstract  fun save(data:Any):Any
    abstract suspend fun findAll():Any
    abstract suspend fun findById(data:Any):Any
    abstract  fun updateOne(id:String, updateDoc:Any):Any
    abstract  fun delete(data:Any):Any
}