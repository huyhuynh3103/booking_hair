package com.example.hair_booking.services.db

abstract class DatabaseAbstract {
    abstract suspend fun find(data:Any):Any
    abstract suspend fun save(data:Any):Any
    abstract  fun findAll():Any
    abstract suspend fun findById(data:Any):Any
    abstract suspend fun updateOne(id:String, updateDoc:Any):Any
    abstract suspend fun delete(data:Any):Any
}