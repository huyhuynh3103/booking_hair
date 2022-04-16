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

class DbAppointmentServices(private var dbInstance: FirebaseFirestore?): DatabaseAbstract() {

    fun getAppointmentListForManager(): MutableLiveData<ArrayList<Appointment>> {
        var result = MutableLiveData<ArrayList<Appointment>>()
        var appointmentList: ArrayList<Appointment> = ArrayList()

        if(dbInstance != null) {
            dbInstance!!.collection("appointments")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Mapping firestore object to kotlin model
                        var appointment: Appointment = Appointment(
                            document.id,
                            document.data["subId"] as String,
                            document.data["userFullName"] as String,
                            document.data["stylist"] as HashMap<String, *>,
                            document.data["bookingDate"] as String,
                            document.data["bookingTime"] as String,
                            document.data["createdAt"] as String,
                            document.data["status"] as String
                        )
                        // Insert to list
                        appointmentList.add(appointment)
                    }

                    // Call function to return appointment list after mapping complete
                    result.value = appointmentList
                }
                .addOnFailureListener { exception ->
                    Log.d("xk", "get failed with ", exception)
                }
        }
        return result
    }

    suspend fun getAppointmentTimeRanges(bookingDate: String, bookingShiftId: String): ArrayList<Pair<Float, Int>> {

        var appointmentTimeRanges: ArrayList<Pair<Float, Int>> = ArrayList()
        var addValueToAppointmentTimeRanges: Deferred<Unit>? = null

        if (dbInstance != null) {
            val shiftDocRef = dbInstance!!
            .collection("shifts")
                .document(bookingShiftId)
            val result = dbInstance!!.collection("appointments")
                .whereEqualTo("bookingShift", shiftDocRef)
                .whereEqualTo("bookingDate", bookingDate)
                .get()
                .await()

            Log.d("xk", "got time range from database ${result.documents.size}")
            addValueToAppointmentTimeRanges = GlobalScope.async {
                for(document in result.documents) {
                    val serviceId: String = ((document.data?.get("service") as HashMap<Any, Any>)["id"] as DocumentReference).id

                    var serviceDuration: Int = 0
                    async {
                        serviceDuration = dbServices.getServiceServices()!!.getServiceDuration(serviceId)
                        Log.d("xk", "got service duration for calc time range")
                    }.await()
                    Log.d("xk", "start to calc time range")
                    val timeRange: Pair<Float, Int> = Pair((document["bookingTime"] as String).toFloat(), serviceDuration)

                    appointmentTimeRanges.add(timeRange)
                }
            }

        }
        addValueToAppointmentTimeRanges?.await()
        Log.d("xk", "finish calc time range and return")
        return appointmentTimeRanges
    }

    suspend fun getAppliedDiscountIds(userId: String): ArrayList<String> {

        var discountIds: ArrayList<String> = ArrayList()

        if (dbInstance != null) {

            val userDocRef = dbInstance!!
                .collection(Constant.collection.normalUsers)
                .document(userId)

            val result = dbInstance!!.collection(Constant.collection.appointments)
                .whereEqualTo("userId", userDocRef)
                .get()
                .await()

            for(document in result.documents) {
                val discountApplied: HashMap<String, *> = document.data?.get("discountApplied") as HashMap<String, *>
                val discountAppliedId: String = (discountApplied["id"] as DocumentReference).id
                discountIds.add(discountAppliedId)
            }

        }

        return discountIds
    }

    override suspend fun find(query: Any): Any {
        TODO("Not yet implemented")
    }

    override fun save(data: Any): Any {
        TODO("Not yet implemented")
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
                    document.data?.get("userFullName") as String,
                    document.data?.get("hairSalon") as HashMap<String, *>,
                    document.data?.get("service") as HashMap<String, *>,
                    document.data?.get("stylist") as HashMap<String, *>,
                    document.data?.get("bookingDate") as String,
                    document.data?.get("bookingTime") as String,
                    document.data?.get("bookingShift") as DocumentReference,
                    document.data?.get("createdAt") as String,
                    document.data?.get("discountApplied") as HashMap<String, *>,
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
    ) {
        val dbNormalUserServices = dbServices.getNormalUserServices()!!
        val dbSalonServices = dbServices.getSalonServices()!!
        val dbStylistServices = dbServices.getStylistServices()!!

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
                    "name" to (chosenSalon?.name ?: null)
                ),
                "service" to hashMapOf(
                    "id" to serviceDocRef,
                    "title" to serviceTitle
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
                    "name" to (chosenSalon?.name ?: null)
                ),
                "service" to hashMapOf(
                    "id" to serviceDocRef,
                    "title" to serviceTitle
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
            )
        }

        // Save appointment to database
        if(dbInstance != null && docTobeSaved != null) {
            dbInstance!!.collection(Constant.collection.appointments)
                .add(docTobeSaved)
                .addOnSuccessListener { documentReference ->
                    Log.d("DbAppointmentServices", "DocumentSnapshot written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.d("DbAppointmentServices", "Error adding document", e)
                }
        }
    }

    private fun generateAppointmentSubId(): String {
        // Get 4 first characters of random ID
        val uuid = UUID.randomUUID().toString()
        var randomIndex: ArrayList<Int> = ArrayList()

        for(i in 0..3) { // Generate 4 random index
            randomIndex.add((0..uuid.length).random())
        }
        var result: String = ""
        randomIndex.forEach {
            // Pick characters from random indexs
            // and concatenate into final string
            result += uuid[it]
        }
        return result
    }
    override suspend fun findById(data: Any): Any {
        TODO("Not yet implemented")
    }

    override fun updateOne(id: String, updateDoc: Any): Any {
        TODO("Not yet implemented")
    }

    override fun delete(data: Any): Any {
        TODO("Not yet implemented")
    }
}