package com.example.hair_booking.services.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.*
import com.example.hair_booking.services.booking.DateServices
import com.example.hair_booking.services.booking.TimeServices
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DbAppointmentServices(private var dbInstance: FirebaseFirestore?): DatabaseAbstract<Any?>() {

    fun getAppointmentListForManager(salonId: String, result: MutableLiveData<ArrayList<Appointment>>) {
        var appointmentList: ArrayList<Appointment> = ArrayList()

        if(dbInstance != null) {
            val salonDocRef = dbInstance!!
                .collection(Constant.collection.hairSalons)
                .document(salonId)

            dbInstance!!.collection("appointments")
                .whereEqualTo("hairSalon.id", salonDocRef)
                .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val documents = snapshot.documents
                    appointmentList.clear()
                    for (document in documents) {
                        // Mapping firestore object to kotlin model
                        val appointment: Appointment = Appointment(
                            document.id,
                            document.data?.get("subId") as String,
                            document.data?.get("userId") as DocumentReference,
                            document.data?.get("userFullName") as String,
                            document.data?.get("userPhoneNumber") as String,
                            document.data?.get("hairSalon") as HashMap<String, *>,
                            document.data?.get("service") as HashMap<String, *>,
                            document.data?.get("stylist") as HashMap<String, *>,
                            document.data?.get("bookingDate") as String,
                            document.data?.get("bookingTime") as String,
                            document.data?.get("bookingShift") as DocumentReference,
                            document.data?.get("createdAt") as String,
                            document.data?.get("discountApplied") as HashMap<String, *>?,
                            document.data?.get("notes") as String,
                            document.data?.get("status") as String,
                            document.data?.get("totalPrice") as Long,
                        )
                        // Insert to list
                        appointmentList.add(appointment)
                    }

                    // Call function to return appointment list after mapping complete
                    result.postValue(appointmentList)
                }
            }
        }
    }

    fun getAppointmentTimeRanges(appointmentList: ArrayList<Appointment>): ArrayList<Pair<Float, Int>> {
        var appointmentTimeRanges: ArrayList<Pair<Float, Int>> = ArrayList()

            for(appointment in appointmentList) {
                var serviceDuration: Int = (appointment.service?.get("duration") as Long).toInt()

                val timeRange: Pair<Float, Int> = Pair((appointment.bookingTime)!!.toFloat(), serviceDuration)

                appointmentTimeRanges.add(timeRange)
            }

        return appointmentTimeRanges
    }

    suspend fun getAppliedDiscountIds(userId: String, serviceId: String): ArrayList<String?> {

        var discountIds: ArrayList<String?> = ArrayList()

        if (dbInstance != null) {

            val userDocRef = dbInstance!!
                .collection(Constant.collection.normalUsers)
                .document(userId)

            val serviceDocRef: DocumentReference = dbInstance!!
                .collection(Constant.collection.services)
                .document(serviceId)

            val result = dbInstance!!.collection(Constant.collection.appointments)
                .whereEqualTo("userId", userDocRef)
                .whereEqualTo("service.id", serviceDocRef)
                .get()
                .await()

            for(document in result.documents) {
                val discountApplied: HashMap<String, *>? = document.data?.get("discountApplied") as HashMap<String, *>?
                val discountAppliedId: String? = (discountApplied?.get("id") as DocumentReference?)?.id
                discountIds.add(discountAppliedId)
            }

        }

        return discountIds
    }


    override suspend fun findAll(): ArrayList<Appointment> {
        var appointmentList: ArrayList<Appointment> = ArrayList()
        try {
            val result = dbInstance!!.collection(Constant.collection.appointments)
                .get()
                .await()


            for(document in result.documents) {
                // Mapping firestore object to kotlin model
                val appointment: Appointment = Appointment(
                    document.id,
                    document.data?.get("subId") as String,
                    document.data?.get("userId") as DocumentReference,
                    document.data?.get("userFullName") as String,
                    document.data?.get("userPhoneNumber") as String,
                    document.data?.get("hairSalon") as HashMap<String, *>,
                    document.data?.get("service") as HashMap<String, *>,
                    document.data?.get("stylist") as HashMap<String, *>,
                    document.data?.get("bookingDate") as String,
                    document.data?.get("bookingTime") as String,
                    document.data?.get("bookingShift") as DocumentReference,
                    document.data?.get("createdAt") as String,
                    document.data?.get("discountApplied") as HashMap<String, *>?,
                    document.data?.get("notes") as String,
                    document.data?.get("status") as String,
                    document.data?.get("totalPrice") as Long,
                )
                // Insert to list
                appointmentList.add(appointment)
            }
        }
        catch (exception: Exception) {
            Log.e("DbAppointmentServices: ", exception.toString())
        }
        return appointmentList
    }

    suspend fun saveBookingSchedule(
        userId: String,
        salonId: String,
        serviceId: String,
        serviceTitle: String,
        stylistId: String,
        bookingDate: String,
        bookingTime: String,
        shiftId: String,
        discountId: String,
        discountTitle: String,
        note: String,
        totalPrice: Long
    ): HashMap<String, *> {
        val dbNormalUserServices = dbServices.getNormalUserServices()!!
        val dbSalonServices = dbServices.getSalonServices()!!
        val dbStylistServices = dbServices.getStylistServices()!!
        val dbServiceServices = dbServices.getServiceServices()!!

        // Get user info
        val user: NormalUser? = dbNormalUserServices.getUserById(userId)

        // Get salon info
        val chosenSalon: Salon? = dbSalonServices.getSalonById(salonId)

        // Get stylist info
        val chosenStylist: Stylist? = dbStylistServices.getStylistById(stylistId)

        // Create user doc reference
        val userDocRef: DocumentReference = dbInstance!!.collection(Constant.collection.normalUsers)
            .document(userId)

        // Create salon doc reference
        val salonDocRef: DocumentReference = dbInstance!!.collection(Constant.collection.hairSalons)
            .document(chosenSalon!!.id)

        // Create shift doc reference
        val stylistDocRef: DocumentReference = dbInstance!!.collection(Constant.collection.stylists)
            .document(stylistId)

        // Create shift doc reference
        val shiftDocRef: DocumentReference = dbInstance!!.collection(Constant.collection.shifts)
            .document(shiftId)

        // Create service doc reference
        val serviceDocRef: DocumentReference = dbInstance!!.collection(Constant.collection.services)
            .document(serviceId)

        val subId: String = generateAppointmentSubId()
        val status: String = "Chấp nhận"
        val createdAt: String = DateServices.currentDateInString() + " " + TimeServices.getCurrentTimeInFloatFormat()
        val serviceDuration: Int = dbServiceServices.getServiceDuration(serviceId)

        var discountDocRef: DocumentReference? = null
        var docTobeSaved: HashMap<String, Any?>? = null
        if(!discountId.isNullOrEmpty() && !discountTitle.isNullOrEmpty()) {
            // Create discount doc reference
            discountDocRef = dbInstance!!.collection(Constant.collection.discounts)
                .document(discountId)

            docTobeSaved = hashMapOf(
                "userId" to userDocRef,
                "userFullName" to (user?.fullName ?: null),
                "userPhoneNumber" to (user?.phoneNumber ?: null),
                "hairSalon" to hashMapOf(
                    "address" to (chosenSalon?.address ?: null),
                    "id" to salonDocRef,
                    "name" to (chosenSalon?.name ?: null),
                    "phoneNumber" to (chosenSalon?.phoneNumber ?: null)
                ),
                "service" to hashMapOf(
                    "id" to serviceDocRef,
                    "title" to serviceTitle,
                    "duration" to serviceDuration
                ),
                "stylist" to hashMapOf(
                    "id" to stylistDocRef,
                    "fullName" to (chosenStylist?.fullName ?: null),
                    "description" to (chosenStylist?.description ?: null),
                    "avatar" to (chosenStylist?.avatar ?: null)
                ),
                "bookingDate" to bookingDate,
                "bookingTime" to bookingTime,
                "bookingShift" to shiftDocRef,
                "discountApplied" to hashMapOf(
                    "id" to discountDocRef,
                    "title" to discountTitle
                ),
                "notes" to note,
                "totalPrice" to totalPrice,
                "subId" to subId,
                "status" to status,
                "createdAt" to createdAt
            )
        }
        else {
            docTobeSaved = hashMapOf(
                "userId" to userDocRef,
                "userFullName" to (user?.fullName ?: null),
                "userPhoneNumber" to (user?.phoneNumber ?: null),
                "hairSalon" to hashMapOf(
                    "address" to (chosenSalon?.address ?: null),
                    "id" to salonDocRef,
                    "name" to (chosenSalon?.name ?: null),
                    "phoneNumber" to (chosenSalon?.phoneNumber ?: null)
                ),
                "service" to hashMapOf(
                    "id" to serviceDocRef,
                    "title" to serviceTitle,
                    "duration" to serviceDuration
                ),
                "stylist" to hashMapOf(
                    "id" to stylistDocRef,
                    "fullName" to (chosenStylist?.fullName ?: null),
                    "description" to (chosenStylist?.description ?: null),
                    "avatar" to (chosenStylist?.avatar ?: null)
                ),
                "bookingDate" to bookingDate,
                "bookingTime" to bookingTime,
                "bookingShift" to shiftDocRef,
                "discountApplied" to null,
                "notes" to note,
                "totalPrice" to totalPrice,
                "subId" to subId,
                "status" to status,
                "createdAt" to createdAt
            )
        }

        // Save appointment to database
        if(dbInstance != null && docTobeSaved != null) {
            dbInstance!!.collection(Constant.collection.appointments)
                .add(docTobeSaved)
                .addOnSuccessListener { documentReference ->
                    Log.d("DbAppointmentServices", "DocumentSnapshot written with ID: ${documentReference.id}")
                    dbSalonServices.updateHairSalonWhenBooking(salonDocRef.id, documentReference)
                    dbNormalUserServices.updateNormalUserWhenBooking(userId, documentReference)
                }
                .addOnFailureListener { e ->
                    Log.d("DbAppointmentServices", "Error adding document", e)
                }
        }
        return docTobeSaved
    }

    private fun generateAppointmentSubId(): String {
        // Get 4 first characters of random ID
        val uuid = UUID.randomUUID().toString()
        var result: String = ""

        var i: Int = 0
        var previousIsHyphen: Boolean = false
        while(i < 4) { // Generate 4 random index
            val tmp: Int = (uuid.indices).random()
            if(uuid[tmp] == '-') {
                if(previousIsHyphen)
                    continue
                else {
                    previousIsHyphen = true
                    i--
                    continue
                }
            }
            else
                result += uuid[tmp]
            i++
        }
        return "#$result"
    }

    override suspend fun find(query: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun save(data: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun updateOne(id: Any?, updateDoc: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Any?): Any? {
        TODO("Not yet implemented")
    }

    override suspend fun add(data: Any?): Any? {
        TODO("Not yet implemented")
    }
}