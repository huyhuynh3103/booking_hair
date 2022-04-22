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
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList

class DbAppointmentServices(private var dbInstance: FirebaseFirestore?): DatabaseAbstract<Any?>() {

    fun getAppointmentListForManager(salonId: String, appointmentList: MutableLiveData<ArrayList<Appointment>>,
                                     appointmentSubIds: MutableLiveData<ArrayList<String>>) {
        var tmpAppointmentList: ArrayList<Appointment> = ArrayList()
        var tmpAppointmentSubIds: ArrayList<String> = ArrayList()
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
                    }

                    val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    tmpAppointmentList.sortByDescending { sdf.parse(it.bookingDate) }
                    // Call function to return appointment list after mapping complete
                    appointmentList.postValue(tmpAppointmentList)
                    appointmentSubIds.postValue(tmpAppointmentSubIds)
                }
            }
        }
    }

    suspend fun getAppointmentById(appointmentId: String): Appointment? {
        var appointment: Appointment? = null

        if(dbInstance != null) {
            val document = dbInstance!!.collection(Constant.collection.appointments)
                .document(appointmentId)
                .get()
                .await()
            // Mapping firestore object to kotlin model
            appointment = Appointment(
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
        }
        return appointment
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
                    "title" to discountTitle
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastDays(numOfDays: Int): ArrayList<Pair<String, Long>> {
        val listOfNLastDays: ArrayList<String> = DateServices.getTheNLastDaysFromNow(numOfDays)
        var revenueOfNLastDays:  ArrayList<Pair<String, Long>> = ArrayList()

        // Get list of appointments that have bookingDate is one of the day in listOfNLastDays
        if(dbInstance != null) {
            val result = dbInstance!!.collection(Constant.collection.appointments)
                .whereIn("bookingDate", listOfNLastDays)
                .get()
                .await()

            if(result.documents.size == 0)
                return revenueOfNLastDays // return empty array

            var appointmentList = result.documents

            // Sort appointments with date descending
            val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            appointmentList.sortByDescending { sdf.parse(it.data?.get("bookingDate") as String) }

            appointmentList = appointmentList.reversed()

            // Init with 0th element
            var dateRevenuePair: Pair<String, Long> = Pair(
                appointmentList[0].data?.get("bookingDate") as String,
                appointmentList[0].data?.get("totalPrice") as Long
            )
            revenueOfNLastDays.add(dateRevenuePair)

            // Continue with remain elements in appointmentList

            for(i in 1 until appointmentList.size) {
                val bookingDate = appointmentList[i].data?.get("bookingDate") as String
                val totalPrice = appointmentList[i].data?.get("totalPrice") as Long


                if( revenueOfNLastDays[revenueOfNLastDays.size - 1].first == bookingDate) {
                    // There is a previous duplicate
                    // => total price of previous += current total price

                    // Because elements in pair is val => need to assign a new pair to change the total price
                    dateRevenuePair = Pair(
                        bookingDate,
                        totalPrice + revenueOfNLastDays[revenueOfNLastDays.size - 1].second
                    )
                    revenueOfNLastDays[revenueOfNLastDays.size - 1] = dateRevenuePair // re assign
                }
                else {
                    dateRevenuePair = Pair(
                        bookingDate,
                        totalPrice
                    )
                    revenueOfNLastDays.add(dateRevenuePair)
                }
            }
        }

        return revenueOfNLastDays
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRevenueOfNLastMonths(numOfMonths: Int): ArrayList<Pair<Pair<Int, Int>, Long>> {
        // Pair of month value and year value
        val listOfNLastMonths: ArrayList<Pair<Int, Int>> = DateServices.getTheNLastMonthsFromNow(numOfMonths)
        var revenueOfNLastMonths:  ArrayList<Pair<Pair<Int, Int>, Long>> = ArrayList()

        // Get list of appointments that have bookingDate is one of the day in listOfNLastDays
        if(dbInstance != null) {
            val result = dbInstance!!.collection(Constant.collection.appointments)
                .get()
                .await()

            if(result.documents.size == 0)
                return revenueOfNLastMonths // return empty array

            var appointmentList = result.documents

            // Sort appointments with date descending
            val sdf: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

            appointmentList.sortByDescending { sdf.parse(it.data?.get("bookingDate") as String) }
            appointmentList = appointmentList.reversed()

            appointmentList.filter {
                // only get appointment which month value and year value in bookingDate is in listOfNLastMonths
                listOfNLastMonths.contains(Pair(
                    sdf.parse(it.data?.get("bookingDate") as String).month,
                    sdf.parse(it.data?.get("bookingDate") as String).year
                ))
            }


            // Init with 0th element
            val cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault()))
            cal.time = sdf.parse(appointmentList[0].data?.get("bookingDate") as String)
            var bookingMonth = cal[Calendar.MONTH] + 1
            var bookingYear = cal[Calendar.YEAR]

            var monthRevenuePair: Pair<Pair<Int, Int>, Long> = Pair(
                Pair(
                    bookingMonth,
                    bookingYear,
                ),
                appointmentList[0].data?.get("totalPrice") as Long
            )
            revenueOfNLastMonths.add(monthRevenuePair)

            // Continue with remain elements in appointmentList
            for(i in 1 until appointmentList.size) {
                cal.time = sdf.parse(appointmentList[i].data?.get("bookingDate") as String)
                bookingMonth = cal[Calendar.MONTH] + 1
                bookingYear = cal[Calendar.YEAR]

                val totalPrice = appointmentList[i].data?.get("totalPrice") as Long

                if(revenueOfNLastMonths[revenueOfNLastMonths.size - 1].first.first == bookingMonth
                    && revenueOfNLastMonths[revenueOfNLastMonths.size - 1].first.second == bookingYear) {
                    // There is a previous duplicate
                    // => total price of previous += current total price

                    // Because elements in pair is val => need to assign a new pair to change the total price
                    monthRevenuePair = Pair(
                        Pair(
                            bookingMonth,
                            bookingYear
                        ),
                        totalPrice + revenueOfNLastMonths[revenueOfNLastMonths.size - 1].second
                    )
                    revenueOfNLastMonths[revenueOfNLastMonths.size - 1] = monthRevenuePair // re assign
                }
                else {
                    monthRevenuePair = Pair(
                        Pair(
                            bookingMonth,
                            bookingYear
                        ),
                        totalPrice
                    )
                    revenueOfNLastMonths.add(monthRevenuePair)
                }
            }
        }

        return revenueOfNLastMonths
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