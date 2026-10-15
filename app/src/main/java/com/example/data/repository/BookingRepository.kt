package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.TripType
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.model.VehicleType
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class BookingRepository private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("guru_travels_store", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _isFirebaseActive = MutableStateFlow(false)
    val isFirebaseActive: StateFlow<Boolean> = _isFirebaseActive.asStateFlow()

    private var firestore: FirebaseFirestore? = null
    private var firestoreListener: ListenerRegistration? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        checkFirebaseAvailability()
        loadLocalData()
        if (_currentUser.value == null) {
            // Default to demo passenger so the app is instantly usable
            loginDemoPassenger()
        }
    }

    private fun checkFirebaseAvailability() {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                val dbId = context.getString(com.example.R.string.firestore_database_id)
                firestore = FirebaseFirestore.getInstance(FirebaseApp.getInstance(), dbId)
                _isFirebaseActive.value = true
                listenToFirestoreBookings()
                Log.d("BookingRepository", "Firebase Firestore is connected and initialized with db: $dbId")
            } else {
                _isFirebaseActive.value = false
                Log.d("BookingRepository", "Firebase not yet provisioned. Operating in Demo/Local mode.")
            }
        } catch (e: Exception) {
            _isFirebaseActive.value = false
            Log.w("BookingRepository", "Firestore connection inactive: ${e.message}")
        }
    }

    private fun listenToFirestoreBookings() {
        try {
            firestoreListener?.remove()
            firestoreListener = firestore?.collection("bookings")
                ?.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("BookingRepository", "Firestore listen error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val remoteList = snapshot.documents.mapNotNull { doc ->
                            try {
                                Booking(
                                    id = doc.getString("id") ?: doc.id,
                                    passengerId = doc.getString("passengerId") ?: "",
                                    passengerName = doc.getString("passengerName") ?: "Passenger",
                                    passengerPhone = doc.getString("passengerPhone") ?: "",
                                    driverId = doc.getString("driverId"),
                                    driverName = doc.getString("driverName"),
                                    driverPhone = doc.getString("driverPhone"),
                                    vehicleType = try {
                                        VehicleType.valueOf(doc.getString("vehicleType") ?: "SEDAN")
                                    } catch (_: Exception) { VehicleType.SEDAN },
                                    vehicleModel = doc.getString("vehicleModel"),
                                    vehicleNumber = doc.getString("vehicleNumber"),
                                    pickupLocation = doc.getString("pickupLocation") ?: "",
                                    destination = doc.getString("destination") ?: "",
                                    tripType = try {
                                        TripType.valueOf(doc.getString("tripType") ?: "LOCAL")
                                    } catch (_: Exception) { TripType.LOCAL },
                                    travelDate = doc.getString("travelDate") ?: "",
                                    travelTime = doc.getString("travelTime") ?: "",
                                    passengerCount = (doc.getLong("passengerCount") ?: 1L).toInt(),
                                    fareAmount = (doc.getLong("fareAmount") ?: 500L).toInt(),
                                    status = try {
                                        BookingStatus.valueOf(doc.getString("status") ?: "PENDING")
                                    } catch (_: Exception) { BookingStatus.PENDING },
                                    otp = doc.getString("otp") ?: "1234",
                                    notes = doc.getString("notes") ?: "",
                                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _bookings.value = remoteList.sortedByDescending { it.createdAt }
                        saveBookingsLocally(_bookings.value)
                    }
                }
        } catch (e: Exception) {
            Log.w("BookingRepository", "Error setting up firestore listener: ${e.message}")
        }
    }

    fun loginDemoPassenger() {
        val passenger = UserAccount(
            id = "demo_pass_101",
            name = "Amit Sharma",
            phone = "+91 98120 45678",
            email = "amit.sharma@example.com",
            role = UserRole.PASSENGER,
            city = "Gurugram (Sector 29)",
            rating = 4.8f
        )
        _currentUser.value = passenger
        saveCurrentUserLocally(passenger)
    }

    fun loginDemoDriver() {
        val driver = UserAccount(
            id = "demo_driver_202",
            name = "Rajesh Kumar Saini",
            phone = "+91 94160 88219",
            email = "rajesh.driver@example.com",
            role = UserRole.DRIVER,
            city = "Panipat (GT Road)",
            vehicleModel = "Maruti Suzuki Dzire (Prime Sedan)",
            vehicleNumber = "HR 06 AB 7741",
            licenseNumber = "HR-06-2018-00923",
            rating = 4.95f
        )
        _currentUser.value = driver
        saveCurrentUserLocally(driver)
    }

    fun registerUser(user: UserAccount) {
        _currentUser.value = user
        saveCurrentUserLocally(user)
        syncUserToFirestore(user)
    }

    fun loginUser(email: String, role: UserRole) {
        val existing = _currentUser.value
        val user = if (existing != null && existing.email.equals(email, ignoreCase = true)) {
            existing.copy(role = role)
        } else {
            UserAccount(
                id = "usr_" + UUID.randomUUID().toString().take(8),
                name = if (role == UserRole.PASSENGER) "Vikas Yadav" else "Kuldeep Singh",
                phone = "+91 98960 12345",
                email = email,
                role = role,
                city = if (role == UserRole.PASSENGER) "Faridabad" else "Karnal",
                vehicleModel = if (role == UserRole.DRIVER) "Toyota Innova Crysta" else "",
                vehicleNumber = if (role == UserRole.DRIVER) "HR 05 CD 8822" else "",
                licenseNumber = if (role == UserRole.DRIVER) "HR-05-2019-77112" else ""
            )
        }
        _currentUser.value = user
        saveCurrentUserLocally(user)
        syncUserToFirestore(user)
    }

    fun switchRole(newRole: UserRole) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            role = newRole,
            vehicleModel = if (newRole == UserRole.DRIVER && current.vehicleModel.isBlank()) "Maruti Dzire" else current.vehicleModel,
            vehicleNumber = if (newRole == UserRole.DRIVER && current.vehicleNumber.isBlank()) "HR 26 AB 3301" else current.vehicleNumber
        )
        _currentUser.value = updated
        saveCurrentUserLocally(updated)
    }

    fun logout() {
        loginDemoPassenger()
    }

    fun createBooking(
        pickup: String,
        destination: String,
        tripType: TripType,
        travelDate: String,
        travelTime: String,
        passengerCount: Int,
        vehicleType: VehicleType,
        notes: String = "",
        passengerName: String = "",
        passengerPhone: String = ""
    ): Booking {
        val user = _currentUser.value
        val passName = passengerName.ifBlank { user?.name?.ifBlank { "Passenger" } ?: "Amit Sharma" }
        val passPhone = passengerPhone.ifBlank { user?.phone?.ifBlank { "+91 98120 45678" } ?: "+91 98120 45678" }
        val passId = user?.id ?: "demo_pass_101"

        val distanceKm = com.example.data.model.HaryanaLocations.estimateDistanceKm(pickup, destination, tripType)
        val calculatedFare = com.example.data.model.HaryanaLocations.calculateFare(distanceKm, vehicleType, tripType)

        val newBooking = Booking(
            id = "BK-" + (1000 + _bookings.value.size + 1),
            passengerId = passId,
            passengerName = passName,
            passengerPhone = passPhone,
            pickupLocation = pickup,
            destination = destination,
            tripType = tripType,
            travelDate = travelDate,
            travelTime = travelTime,
            passengerCount = passengerCount,
            vehicleType = vehicleType,
            fareAmount = calculatedFare,
            status = BookingStatus.PENDING,
            otp = ((1000..9999).random()).toString(),
            notes = notes,
            createdAt = System.currentTimeMillis()
        )

        val currentList = _bookings.value.toMutableList()
        currentList.add(0, newBooking)
        _bookings.value = currentList
        saveBookingsLocally(currentList)
        syncBookingToFirestore(newBooking)
        return newBooking
    }

    fun rejectBooking(bookingId: String) {
        val currentList = _bookings.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val updated = currentList[index].copy(status = BookingStatus.REJECTED)
            currentList[index] = updated
            _bookings.value = currentList
            saveBookingsLocally(currentList)
            syncBookingToFirestore(updated)
        }
    }

    fun acceptBooking(bookingId: String, driver: UserAccount) {
        val currentList = _bookings.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val updated = currentList[index].copy(
                driverId = driver.id,
                driverName = driver.name,
                driverPhone = driver.phone,
                vehicleModel = driver.vehicleModel.ifBlank { "Maruti Dzire" },
                vehicleNumber = driver.vehicleNumber.ifBlank { "HR 06 AB 7741" },
                status = BookingStatus.ACCEPTED
            )
            currentList[index] = updated
            _bookings.value = currentList
            saveBookingsLocally(currentList)
            syncBookingToFirestore(updated)
        }
    }

    fun completeBooking(bookingId: String) {
        val currentList = _bookings.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val updated = currentList[index].copy(status = BookingStatus.COMPLETED)
            currentList[index] = updated
            _bookings.value = currentList
            saveBookingsLocally(currentList)
            syncBookingToFirestore(updated)
        }
    }

    fun cancelBooking(bookingId: String) {
        val currentList = _bookings.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val updated = currentList[index].copy(status = BookingStatus.CANCELLED)
            currentList[index] = updated
            _bookings.value = currentList
            saveBookingsLocally(currentList)
            syncBookingToFirestore(updated)
        }
    }

    private fun syncBookingToFirestore(booking: Booking) {
        if (_isFirebaseActive.value && firestore != null) {
            scope.launch {
                try {
                    val map = hashMapOf(
                        "id" to booking.id,
                        "passengerId" to booking.passengerId,
                        "passengerName" to booking.passengerName,
                        "passengerPhone" to booking.passengerPhone,
                        "driverId" to booking.driverId,
                        "driverName" to booking.driverName,
                        "driverPhone" to booking.driverPhone,
                        "vehicleType" to booking.vehicleType.name,
                        "vehicleModel" to booking.vehicleModel,
                        "vehicleNumber" to booking.vehicleNumber,
                        "pickupLocation" to booking.pickupLocation,
                        "destination" to booking.destination,
                        "tripType" to booking.tripType.name,
                        "travelDate" to booking.travelDate,
                        "travelTime" to booking.travelTime,
                        "passengerCount" to booking.passengerCount,
                        "fareAmount" to booking.fareAmount,
                        "status" to booking.status.name,
                        "otp" to booking.otp,
                        "notes" to booking.notes,
                        "createdAt" to booking.createdAt
                    )
                    firestore?.collection("bookings")?.document(booking.id)?.set(map)
                } catch (e: Exception) {
                    Log.w("BookingRepository", "Failed to sync to Firestore: ${e.message}")
                }
            }
        }
    }

    private fun syncUserToFirestore(user: UserAccount) {
        if (_isFirebaseActive.value && firestore != null) {
            scope.launch {
                try {
                    val map = hashMapOf(
                        "uid" to user.id,
                        "name" to user.name,
                        "phone" to user.phone,
                        "email" to user.email,
                        "role" to user.role.name,
                        "city" to user.city,
                        "vehicleModel" to user.vehicleModel,
                        "vehicleNumber" to user.vehicleNumber,
                        "rating" to user.rating
                    )
                    firestore?.collection("users")?.document(user.id)?.set(map)
                } catch (e: Exception) {
                    Log.w("BookingRepository", "Failed to sync user: ${e.message}")
                }
            }
        }
    }

    private fun saveCurrentUserLocally(user: UserAccount) {
        prefs.edit().apply {
            putString("user_id", user.id)
            putString("user_name", user.name)
            putString("user_phone", user.phone)
            putString("user_email", user.email)
            putString("user_role", user.role.name)
            putString("user_city", user.city)
            putString("user_vmodel", user.vehicleModel)
            putString("user_vnumber", user.vehicleNumber)
            putString("user_license", user.licenseNumber)
            putFloat("user_rating", user.rating)
            apply()
        }
    }

    private fun saveBookingsLocally(list: List<Booking>) {
        val array = JSONArray()
        list.forEach { b ->
            val obj = JSONObject().apply {
                put("id", b.id)
                put("passengerId", b.passengerId)
                put("passengerName", b.passengerName)
                put("passengerPhone", b.passengerPhone)
                put("driverId", b.driverId ?: "")
                put("driverName", b.driverName ?: "")
                put("driverPhone", b.driverPhone ?: "")
                put("vehicleType", b.vehicleType.name)
                put("vehicleModel", b.vehicleModel ?: "")
                put("vehicleNumber", b.vehicleNumber ?: "")
                put("pickupLocation", b.pickupLocation)
                put("destination", b.destination)
                put("tripType", b.tripType.name)
                put("travelDate", b.travelDate)
                put("travelTime", b.travelTime)
                put("passengerCount", b.passengerCount)
                put("fareAmount", b.fareAmount)
                put("status", b.status.name)
                put("otp", b.otp)
                put("notes", b.notes)
                put("createdAt", b.createdAt)
            }
            array.put(obj)
        }
        prefs.edit().putString("saved_bookings", array.toString()).apply()
    }

    private fun loadLocalData() {
        // Load User
        val userId = prefs.getString("user_id", null)
        if (userId != null) {
            val role = try {
                UserRole.valueOf(prefs.getString("user_role", "PASSENGER") ?: "PASSENGER")
            } catch (_: Exception) { UserRole.PASSENGER }

            _currentUser.value = UserAccount(
                id = userId,
                name = prefs.getString("user_name", "Amit Sharma") ?: "Amit Sharma",
                phone = prefs.getString("user_phone", "+91 98120 45678") ?: "+91 98120 45678",
                email = prefs.getString("user_email", "amit@example.com") ?: "amit@example.com",
                role = role,
                city = prefs.getString("user_city", "Gurugram") ?: "Gurugram",
                vehicleModel = prefs.getString("user_vmodel", "") ?: "",
                vehicleNumber = prefs.getString("user_vnumber", "") ?: "",
                licenseNumber = prefs.getString("user_license", "") ?: "",
                rating = prefs.getFloat("user_rating", 4.9f)
            )
        }

        // Load Bookings or initialize realistic Haryana demo bookings
        val savedJson = prefs.getString("saved_bookings", null)
        if (!savedJson.isNullOrBlank()) {
            try {
                val array = JSONArray(savedJson)
                val list = mutableListOf<Booking>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        Booking(
                            id = obj.getString("id"),
                            passengerId = obj.getString("passengerId"),
                            passengerName = obj.getString("passengerName"),
                            passengerPhone = obj.getString("passengerPhone"),
                            driverId = obj.optString("driverId").takeIf { it.isNotBlank() },
                            driverName = obj.optString("driverName").takeIf { it.isNotBlank() },
                            driverPhone = obj.optString("driverPhone").takeIf { it.isNotBlank() },
                            vehicleType = try {
                                VehicleType.valueOf(obj.getString("vehicleType"))
                            } catch (_: Exception) { VehicleType.SEDAN },
                            vehicleModel = obj.optString("vehicleModel").takeIf { it.isNotBlank() },
                            vehicleNumber = obj.optString("vehicleNumber").takeIf { it.isNotBlank() },
                            pickupLocation = obj.getString("pickupLocation"),
                            destination = obj.getString("destination"),
                            tripType = try {
                                TripType.valueOf(obj.getString("tripType"))
                            } catch (_: Exception) { TripType.LOCAL },
                            travelDate = obj.getString("travelDate"),
                            travelTime = obj.getString("travelTime"),
                            passengerCount = obj.getInt("passengerCount"),
                            fareAmount = obj.getInt("fareAmount"),
                            status = try {
                                BookingStatus.valueOf(obj.getString("status"))
                            } catch (_: Exception) { BookingStatus.PENDING },
                            otp = obj.optString("otp", "4829"),
                            notes = obj.optString("notes", ""),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }
                _bookings.value = list
                return
            } catch (e: Exception) {
                Log.w("BookingRepository", "Failed to parse saved bookings: ${e.message}")
            }
        }

        // Initialize realistic Haryana sample bookings
        val initialSample = listOf(
            Booking(
                id = "BK-1001",
                passengerId = "demo_pass_101",
                passengerName = "Amit Sharma",
                passengerPhone = "+91 98120 45678",
                driverId = null,
                driverName = null,
                driverPhone = null,
                vehicleType = VehicleType.SEDAN,
                pickupLocation = "Panipat (GT Road Bus Stand)",
                destination = "Delhi Airport (IGI Terminal 3)",
                tripType = TripType.INTERCITY,
                travelDate = "Today",
                travelTime = "04:30 PM",
                passengerCount = 2,
                fareAmount = 1650,
                status = BookingStatus.PENDING,
                otp = "5921",
                notes = "2 Large luggage bags. Need AC cab.",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 20
            ),
            Booking(
                id = "BK-1002",
                passengerId = "usr_sunita",
                passengerName = "Sunita Rani",
                passengerPhone = "+91 94670 33120",
                driverId = "demo_driver_202",
                driverName = "Rajesh Kumar Saini",
                driverPhone = "+91 94160 88219",
                vehicleType = VehicleType.SUV,
                vehicleModel = "Maruti Suzuki Ertiga",
                vehicleNumber = "HR 06 AB 7741",
                pickupLocation = "Gurugram (Cyber Hub, Phase 2)",
                destination = "Kurukshetra (Brahma Sarovar)",
                tripType = TripType.INTERCITY,
                travelDate = "Tomorrow",
                travelTime = "07:00 AM",
                passengerCount = 5,
                fareAmount = 3200,
                status = BookingStatus.ACCEPTED,
                otp = "3319",
                notes = "Family pilgrimage tour with senior citizens.",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 2
            ),
            Booking(
                id = "BK-1003",
                passengerId = "demo_pass_101",
                passengerName = "Amit Sharma",
                passengerPhone = "+91 98120 45678",
                driverId = "drv_vikram",
                driverName = "Vikram Phogat",
                driverPhone = "+91 98130 99412",
                vehicleType = VehicleType.HATCHBACK,
                vehicleModel = "Maruti Suzuki Swift",
                vehicleNumber = "HR 26 DL 4492",
                pickupLocation = "Rohtak (MDU Gate 1)",
                destination = "Hisar (Railway Station)",
                tripType = TripType.INTERCITY,
                travelDate = "Yesterday",
                travelTime = "11:00 AM",
                passengerCount = 1,
                fareAmount = 1450,
                status = BookingStatus.COMPLETED,
                otp = "8812",
                notes = "Trip completed smoothly.",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24
            )
        )
        _bookings.value = initialSample
        saveBookingsLocally(initialSample)
    }

    companion object {
        @Volatile
        private var instance: BookingRepository? = null

        fun getInstance(context: Context): BookingRepository {
            return instance ?: synchronized(this) {
                instance ?: BookingRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
