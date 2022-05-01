package com.example.hair_booking.services.db

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.example.hair_booking.Constant
import com.example.hair_booking.model.*
import com.example.hair_booking.services.booking.DateServices
import com.example.hair_booking.services.booking.TimeServices
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DbAppointmentServices(private var dbInstance: FirebaseFirestore?): DatabaseAbstract<Any?>() {

    fun getAppointmentListForManager(salonId: String, appointmentList: MutableLiveData<ArrayList<Appointment>>,
                                     appointmentSubIds: MutableLiveData<ArrayList<String>>,
                                     appointmentStylistsName: MutableLiveData<ArrayList<String>>) {
        var tmpAppointmentList: ArrayList<Appointment> = ArrayList()
        var tmpAppointmentSubIds: ArrayList<String> = ArrayList()
        var tmpAppointmentStylistsName: ArrayList<String> = ArrayList()
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
                    tmpAppointmentList.clear()
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
                        tmpAppointmentList.add(appointment)
                        tmpAppointmentSubIds.add(document.data?.get("subId") as String)

                        val stylist = document.data?.get("stylist") as HashMap<String, *>
                        tmpAppointmentStylistsName.add(stylist["fullName"] as String)
                    }

                    val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    tmpAppointmentList.sortByDescending { sdf.parse(it.bookingDate) }
                    // Call function to return appointment list after mapping complete
                    appointmentList.postValue(tmpAppointmentList)
                    appointmentSubIds.postValue(tmpAppointmentSubIds)
                    appointmentStylistsName.postValue(tmpAppointmentStylistsName.distinct() as ArrayList<String>?)
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

    suspend fun getAppointmentListForUser(emailUser: String,statusAppointment: String? = null): ArrayList<Appointment>{
        val appointmentList : ArrayList<Appointment> = ArrayList()
        if(dbInstance!=null){
            val accountQuerySnapshot = dbInstance!!
                .collection(Constant.collection.accounts)
                .whereEqualTo("email",emailUser)
                .get()
                .await()
            val accountDocRef = accountQuerySnapshot.documents[0].reference
            val userQuerySnapshot = dbInstance!!
                .collection(Constant.collection.normalUsers)
                .whereEqualTo("accountId",accountDocRef)
                .get()
                .await()
            if(userQuerySnapshot.documents.isNotEmpty()) {
                val appointmentRefList =
                    userQuerySnapshot.documents[0].data!!["appointments"] as ArrayList<DocumentReference>?
                if (appointmentRefList != null) {
                    for (appointmentRef in appointmentRefList) {
                        val document = appointmentRef.get().await()

                        val data = document.data
                        if (data != null) {
                            if (statusAppointment != null) {
                                if (data["status"] != statusAppointment) {
                                    continue
                                }
                            }
                            var rate: Double
                            if (data["rate"] is Long) {
                                rate = Double.fromBits(data["rate"] as Long)
                            } else {
                                rate = data["rate"] as Double
                            }
                            val appointment = Appointment(document.id,
                                data["subId"] as String,
                                data["bookingDate"] as String,
                                data["bookingTime"] as String,
                                data["status"] as String,
                                data["hairSalon"] as HashMap<String, *>,
                                data["totalPrice"] as Long,
                                rate)
                            appointmentList.add(appointment)
                        }


                    }
                }
            }
        }
        return appointmentList
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

    suspend fun findAllWithHairSalon(salonId: String): ArrayList<Appointment> {
        var appointmentList: ArrayList<Appointment> = ArrayList()
        if(dbInstance != null) {
            val salonDocRef = dbInstance!!
                .collection(Constant.collection.hairSalons)
                .document(salonId)
            try {
                val result = dbInstance!!.collection(Constant.collection.appointments)
                    .whereEqualTo("hairSalon.id", salonDocRef)
                    .get()
                    .await()


                for (document in result.documents) {
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
            } catch (exception: Exception) {
                Log.e("DbAppointmentServices: ", exception.toString())
            }
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
        discountPercent: Float,
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

        val chosenService: Service? = dbServiceServices.getServiceById(serviceId)
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
        val status: String = Constant.AppointmentStatus.isPending
        val createdAt: String = DateServices.currentDateInString() + " " + TimeServices.getCurrentTimeInFloatFormat()
        val rate: Long = 0

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
                    "duration" to (chosenService?.duration ?: null),
                    "price" to (chosenService?.price ?: null)
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
                    "title" to discountTitle,
                    "percent" to discountPercent.toString()
                ),
                "notes" to note,
                "totalPrice" to totalPrice,
                "subId" to subId,
                "status" to status,
                "createdAt" to createdAt,
                "rate" to rate
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
                    "duration" to (chosenService?.duration ?: null),
                    "price" to (chosenService?.price ?: null)
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
                "createdAt" to createdAt,
                "rate" to rate
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
    suspend fun cancelBySubId(subId:String){
        try{
            val res = dbInstance!!.collection(Constant.collection.appointments)
                .whereEqualTo("subId",subId)
                .get()
                .await()
            if(!res.isEmpty){
                val docReference = res.documents[0].reference
                docReference.update("status",Constant.AppointmentStatus.isAbort).await()
            }
        }catch (e:Exception){
            Log.e("DbAppointmentServices", "Error cancel appointment", e)
            throw e
        }
    }
    suspend fun cancelById(id:String?){
        try{
            val res = dbInstance!!.collection(Constant.collection.appointments)
                .document(id!!)
                .get()
                .await()
            val docReference = res.reference
            docReference.update("status",Constant.AppointmentStatus.isAbort).await()
        }catch (e:Exception){
            Log.e("DbAppointmentServices", "Error cancel appointment", e)
            throw e
        }
    }
    suspend fun getAppointmentById(id:String):Appointment?{
        var result:Appointment? = null
        val queryResult = dbInstance!!.collection(Constant.collection.appointments).document(id).get().await()
        val data = queryResult.data
        if(data!=null){
            var rate:Double
            if(data.get("rate")  is Long){
                rate = Double.fromBits(data.get("rate")  as Long)
            }else{
                rate = data.get("rate")  as Double
            }

            result = Appointment(
                id,
                data.get("subId") as String,
                data.get("userId") as DocumentReference,
                data.get("userFullName") as String,
                data.get("userPhoneNumber") as String,
                data.get("hairSalon") as HashMap<String, *>,
                data.get("service") as HashMap<String, *>,
                data.get("stylist") as HashMap<String, *>,
                data.get("bookingDate") as String,
                data.get("bookingTime") as String,
                data.get("bookingShift") as DocumentReference,
                data.get("createdAt") as String,
                data.get("discountApplied") as HashMap<String, *>?,
                data.get("notes") as String,
                data.get("status") as String,
                data.get("totalPrice") as Long,
                rate
            )
        }



        return result
    }
    suspend fun rateAppointment(id:String, rating:Float){
        var result:Appointment? = null
        dbInstance!!.collection(Constant.collection.appointments).document(id)
            .update("rate",rating)
            .await()
    }
    suspend fun updateAppointment(
        appointmentId: String,
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
        discountPercent: Float,
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

        val chosenService: Service? = dbServiceServices.getServiceById(serviceId)

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
                    "duration" to (chosenService?.duration ?: null),
                    "price" to (chosenService?.price ?: null)
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
                    "title" to discountTitle,
                    "percent" to discountPercent.toString()
                ),
                "notes" to note,
                "totalPrice" to totalPrice
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
                    "duration" to (chosenService?.duration ?: null),
                    "price" to (chosenService?.price ?: null)
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
                "totalPrice" to totalPrice
            )
        }

        // Find and update appointment to database
        // If appointment does not exist, firestore will create a new one
        if(dbInstance != null && docTobeSaved != null) {
            dbInstance!!.collection(Constant.collection.appointments)
                .document(appointmentId)
                .set(docTobeSaved, SetOptions.merge()) // Set merge option to tell firestore to update changed fields only
                .addOnSuccessListener {
                    Log.d("DbAppointmentServices", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.d("DbAppointmentServices", "Error updating document", e)
                }
        }
        return docTobeSaved
    }

    suspend fun updateAppointmentStatus(appointmentId: String, appointmentStatus: String): Boolean? {
        var ack: Boolean = false
        try {
            // Find and update appointment status
            if(dbInstance != null) {
                dbInstance!!.collection(Constant.collection.appointments)
                    .document(appointmentId)
                    .update("status", appointmentStatus)
                    .await()

                ack = true
            }
        }
        catch (e: Exception) {
            Log.e("DbAppointmentServices", "Error updating document", e)
        }
        return ack
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    suspend fun getRevenueOfNLastDays(numOfDays: Int): ArrayList<Pair<String, Long>> {}

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getServicesBooked(salonId: String): HashMap<String, Int> {
        var services : HashMap<String, Int> = HashMap()
        var appointmentList: ArrayList<Appointment> = ArrayList()
        GlobalScope.async {
            async {
                appointmentList = findAllWithHairSalon(salonId)
            }.await()
            for (i in appointmentList.indices) {
                val appointment = appointmentList[i]
                services.putIfAbsent(appointment.service?.get("title").toString(), 0)
            }
        }.await()
        return services
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAmountOfServicesBooked(salonId: String): Map<String, Int> {
        var amountServices: HashMap<String, Int> = HashMap()
        var appointmentList: ArrayList<Appointment> = ArrayList()
        var services: HashMap<String, Int> = getServicesBooked(salonId)
        GlobalScope.async {
            async {
                appointmentList = findAllWithHairSalon(salonId)
            }.await()
            for (key in services.keys) {
                var count: Int = 0
                if (appointmentList != null) {
                    for (i in appointmentList.indices) {
                        val appointment = appointmentList.get(i)
                        if (key == appointment.service?.get("title").toString())
                            count += 1
                    }
                }
                amountServices.put(key, count)
            }
        }.await()
        val result = amountServices.toList().sortedByDescending { (_, value) -> value }.toMap()
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAmountOfShiftsBooked(salonId: String): Map<String, Int> {
        var amountShifts: HashMap<String, Int> = HashMap()
        var appointmentList: ArrayList<Appointment> = ArrayList()
        var shifts: HashMap<String, String> = hashMapOf("Hl0aRPOhZaI02vqMRUdr" to "Sáng",
            "KaZ0Gj0MzYvkZkpHAs8Q" to "Chiều", "cRAgvOR26BYKSRaHbG0g" to "Tối")
        GlobalScope.async {
            async {
                appointmentList = findAllWithHairSalon(salonId)
            }.await()
            for (key in shifts.keys) {
                var count: Int = 0
                if (appointmentList != null) {
                    for (i in appointmentList.indices) {
                        val appointment = appointmentList.get(i)
                        if (key == appointment.bookingShift?.id.toString())
                            count += 1
                    }
                }
                amountShifts.put(shifts[key]!!, count)
            }
        }.await()
        val result = amountShifts.toList().sortedByDescending { (_, value) -> value }.toMap()
        return result
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastDays(numOfDays: Int, salonId: String): ArrayList<Pair<String, Long>> {
        val listOfNLastDays: ArrayList<String> = DateServices.getTheNLastDaysFromNow(numOfDays)
        var revenueOfNLastDays:  ArrayList<Pair<String, Long>> = ArrayList()


        // Get list of appointments that have bookingDate is one of the day in listOfNLastDays
        if(dbInstance != null) {
            //val result = dbInstance!!.collection(Constant.collection.appointments)
            val salonDocRef = dbInstance!!
                .collection(Constant.collection.hairSalons)
                .document(salonId)

            val result = dbInstance!!.collection(Constant.collection.appointments)
                .whereEqualTo("hairSalon.id",salonDocRef)
                .whereIn("bookingDate", listOfNLastDays)
                .get()
                .await()

            if(result.documents.size == 0)
                return revenueOfNLastDays // return empty array


            var appointmentList = result.documents

            // Sort appointments with date descending
            val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            appointmentList.sortByDescending { sdf.parse(it.data?.get("bookingDate") as String) }


            // Continue with remain elements in appointmentList
            var i: Int = 0
            var appointmentIndex: Int = 0
            while(i < listOfNLastDays.size) {
                if(appointmentIndex < appointmentList.size) { // check in case size of appointmentList < size of listOfNLastDays
                    val bookingDate = appointmentList[appointmentIndex].data?.get("bookingDate") as String
                    val totalPrice = appointmentList[appointmentIndex].data?.get("totalPrice") as Long

                    if(listOfNLastDays[i] == bookingDate) {
                        var prevBookingDate = ""
                        if(appointmentIndex - 1 >= 0) {
                            // If the current day has an appointment => check if it's duplicate with the prev one
                            prevBookingDate = appointmentList[appointmentIndex - 1].data?.get("bookingDate") as String
                        }

                        if(prevBookingDate != "" && prevBookingDate == bookingDate) {
                            // There is a previous duplicate
                            // => total price of previous += current total price
                            // Because elements in pair is val => need to assign a new pair to change the total price

                            val prevTotalPrice = appointmentList[appointmentIndex - 1].data?.get("totalPrice") as Long
                            val dateRevenuePair = Pair(
                                bookingDate,
                                totalPrice + prevTotalPrice
                            )
                            revenueOfNLastDays[revenueOfNLastDays.size - 1] = dateRevenuePair // re assign
//                            continue // Keep current day in listOfNLastDays to continue to check for duplicates
                        }
                        else {
                            // If there is no previous duplicate => assign current day with current appointment total price
                            revenueOfNLastDays.add(Pair(listOfNLastDays[i], totalPrice))

                            if(appointmentIndex - 1 > 0 )
                                i++ // continue with the next day in listOfNLastDays
                        }
                        appointmentIndex++ // continue with a another appointment
                    }
                    else {
                        // If there is no more duplicate to check => skip 1 loop to get next value of listOfNLastDays
                        if(revenueOfNLastDays.size - 1 >= 0 && revenueOfNLastDays[revenueOfNLastDays.size - 1].first != listOfNLastDays[i]) {
                            // Assign zero revenue for days that did not have any appointment
                            revenueOfNLastDays.add(Pair(listOfNLastDays[i], 0))
                        }
                        i++
                    }

                }
                else {
                    // If there is no more duplicate to check => skip 1 loop to get next value of listOfNLastDays
                    if(i != 0 && revenueOfNLastDays.size - 1 >= 0 && revenueOfNLastDays[revenueOfNLastDays.size - 1].first != listOfNLastDays[i]) {
                        // Assign zero revenue for days that did not have any appointment
                        revenueOfNLastDays.add(Pair(listOfNLastDays[i], 0))
                    }
                    else if (i == 0 && revenueOfNLastDays.size - 1 < 0)
                    {
                        // Assign zero revenue for days that did not have any appointment
                        revenueOfNLastDays.add(Pair(listOfNLastDays[i], 0))
                    }
                    i++
                }
            }
        }

        return revenueOfNLastDays.reversed() as ArrayList<Pair<String, Long>>
    }


    @RequiresApi(Build.VERSION_CODES.O)
    //suspend fun getRevenueOfNLastMonths(numOfMonths: Int): ArrayList<Pair<Pair<Int, Int>, Long>> {
    suspend fun getRevenueOfNLastMonths(numOfMonths: Int, salonId: String): ArrayList<Pair<Pair<Int, Int>, Long>> {
        // Pair of month value and year value
        val listOfNLastMonths: ArrayList<Pair<Int, Int>> = DateServices.getTheNLastMonthsFromNow(numOfMonths)
        var revenueOfNLastMonths:  ArrayList<Pair<Pair<Int, Int>, Long>> = ArrayList()

        // Get list of appointments that have bookingDate is one of the day in listOfNLastDays
        if(dbInstance != null) {
            //val result = dbInstance!!.collection(Constant.collection.appointments)
            val salonDocRef = dbInstance!!
                .collection(Constant.collection.hairSalons)
                .document(salonId)

            val result = dbInstance!!.collection(Constant.collection.appointments)
                .whereEqualTo("hairSalon.id",salonDocRef)
                .get()
                .await()

            if(result.documents.size == 0)
                return revenueOfNLastMonths // return empty array

            var appointmentList = result.documents

            // Sort appointments with date descending
            val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

            appointmentList.sortByDescending { sdf.parse(it.data?.get("bookingDate") as String) }

            appointmentList.filter {
                // only get appointment which month value and year value in bookingDate is in listOfNLastMonths
                listOfNLastMonths.contains(Pair(
                    sdf.parse(it.data?.get("bookingDate") as String).month,
                    sdf.parse(it.data?.get("bookingDate") as String).year
                ))
            }


            // Init with 0th element
            val cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()))

            // Continue with remain elements in appointmentList
            var i: Int = 0
            var appointmentIndex: Int = 0
            while(i < listOfNLastMonths.size) {
                if(appointmentIndex < appointmentList.size) { // check in case size of appointmentList < size of listOfNLastMonths
                    cal.time = sdf.parse(appointmentList[appointmentIndex].data?.get("bookingDate") as String)
                    var bookingMonth = cal[Calendar.MONTH] + 1
                    var bookingYear = cal[Calendar.YEAR]
                    val totalPrice = appointmentList[appointmentIndex].data?.get("totalPrice") as Long

                    if(listOfNLastMonths[i].first == bookingMonth && listOfNLastMonths[i].second == bookingYear) {
                        var prevBookingMonth: Pair<Int, Int>? = null
                        if(appointmentIndex - 1 >= 0) {
                            // If the current month has an appointment => check if it's duplicate with the prev one
                            cal.time = sdf.parse(appointmentList[appointmentIndex - 1].data?.get("bookingDate") as String)
                            prevBookingMonth = Pair(cal[Calendar.MONTH] + 1, cal[Calendar.YEAR])
                        }

                        if(prevBookingMonth != null
                            && prevBookingMonth.first == bookingMonth
                            && prevBookingMonth.second == bookingYear) {
                            // There is a previous duplicate
                            // => total price of previous += current total price
                            // Because elements in pair is val => need to assign a new pair to change the total price

                            val prevTotalPrice = appointmentList[appointmentIndex - 1].data?.get("totalPrice") as Long
                            val monthRevenuePair: Pair<Pair<Int, Int>, Long> = Pair(
                                Pair(
                                    bookingMonth,
                                    bookingYear,
                                ),
                                prevTotalPrice
                            )
                            revenueOfNLastMonths[revenueOfNLastMonths.size - 1] = monthRevenuePair // re assign
                        }
                        else {
                            // If there is no previous duplicate => assign current day with current appointment total price
                            revenueOfNLastMonths.add(Pair(listOfNLastMonths[i], totalPrice))

                            if(appointmentIndex - 1 > 0 )
                                i++ // continue with the next day in listOfNLastMonths
                        }
                        appointmentIndex++ // continue with a another appointment
                    }
                    else {
                        // If there is no more duplicate to check => skip 1 loop to get next value of listOfNLastMonths
                        if(revenueOfNLastMonths.size - 1 >= 0 && revenueOfNLastMonths[revenueOfNLastMonths.size - 1].first != listOfNLastMonths[i]) {
                            // Assign zero revenue for days that did not have any appointment
                            revenueOfNLastMonths.add(Pair(listOfNLastMonths[i], 0))
                        }
                        i++
                    }

                }
                else {
                    // If there is no more duplicate to check => skip 1 loop to get next value of listOfNLastMonths
                    if(i != 0 && revenueOfNLastMonths.size - 1 >= 0 && revenueOfNLastMonths[revenueOfNLastMonths.size - 1].first != listOfNLastMonths[i]) {
                        // Assign zero revenue for days that did not have any appointment
                        revenueOfNLastMonths.add(Pair(listOfNLastMonths[i], 0))
                    }
                    else if (i == 0 && revenueOfNLastMonths.size - 1 < 0)
                    {
                        // Assign zero revenue for days that did not have any appointment
                        revenueOfNLastMonths.add(Pair(listOfNLastMonths[i], 0))
                    }
                    i++
                }
            }
        }

        return  revenueOfNLastMonths.reversed() as ArrayList<Pair<Pair<Int, Int>, Long>>
    }

    suspend fun countAppointment(stylist: DocumentReference?): Int {
        var count = 0;

        try {
            val docSnap = dbInstance!!.collection(Constant.collection.appointments)
                .whereEqualTo("stylist.id", stylist)
                .whereEqualTo("status", "Chưa thanh toán")
                .get()
                .await()

            count = docSnap.documents.size
            Log.i("isConflict", count.toString())
        }
        catch (exception: Exception) {
            Log.e("DbAppointmentServices: ", exception.toString())
        }
        return count
    }

    suspend fun countAppointment(stylist: DocumentReference?, shift: DocumentReference?): Int {
        var count = 0;

        try {
            val docSnap = dbInstance!!.collection(Constant.collection.appointments)
                .whereEqualTo("stylist.id", stylist)
                .whereEqualTo("status", "Chưa thanh toán")
                .whereEqualTo("bookingShift", shift)
                .get()
                .await()

            count = docSnap.documents.size
            Log.i("isConflict", count.toString())
        }
        catch (exception: Exception) {
            Log.e("DbAppointmentServices: ", exception.toString())
        }
        return count
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