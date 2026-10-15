package com.example.data.model

enum class UserRole {
    PASSENGER,
    DRIVER
}

enum class TripType(val displayName: String, val description: String) {
    LOCAL("Local Trip", "Within city / nearby points in Haryana"),
    INTERCITY("Intercity Trip", "Point-to-point travel between Haryana cities & NCR")
}

enum class BookingStatus(val displayName: String) {
    PENDING("Pending Confirmation"),
    ACCEPTED("Driver Assigned"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    REJECTED("Declined by Driver")
}

enum class VehicleType(
    val title: String,
    val subtitle: String,
    val capacity: Int,
    val baseRatePerKm: Int
) {
    HATCHBACK("Mini / Hatchback", "WagonR, Swift, Tiago", 4, 11),
    SEDAN("Prime Sedan", "Dzire, Etios, Aura", 4, 13),
    SUV("Prime SUV", "Ertiga, Carens, Innova", 6, 17),
    TEMPO("Tempo Traveler", "12-16 Seater Group Travel", 14, 25)
}

data class UserAccount(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val role: UserRole,
    val city: String,
    val vehicleModel: String = "",
    val vehicleNumber: String = "",
    val licenseNumber: String = "",
    val rating: Float = 4.9f
)

data class Booking(
    val id: String,
    val passengerId: String,
    val passengerName: String,
    val passengerPhone: String,
    val driverId: String? = null,
    val driverName: String? = null,
    val driverPhone: String? = null,
    val vehicleType: VehicleType = VehicleType.SEDAN,
    val vehicleModel: String? = null,
    val vehicleNumber: String? = null,
    val pickupLocation: String,
    val destination: String,
    val tripType: TripType,
    val travelDate: String,
    val travelTime: String,
    val passengerCount: Int = 1,
    val fareAmount: Int,
    val status: BookingStatus = BookingStatus.PENDING,
    val otp: String = "4829",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

object HaryanaLocations {
    val majorCities = listOf(
        "Gurugram (Cyber Hub / Sector 29)",
        "Faridabad (Badkal Lake / Sector 15)",
        "Panipat (GT Road / Industrial Area)",
        "Ambala (Cantonment / City)",
        "Rohtak (MDU / Delhi Road)",
        "Hisar (Jind Road / Cantt)",
        "Karnal (Karna Lake / Mall Road)",
        "Sonipat (Murthal / Rai)",
        "Panchkula (Sector 5 / Mansa Devi)",
        "Kurukshetra (Brahma Sarovar / Pipli)",
        "Rewari (Bawal / Brass Market)",
        "Yamunanagar (Jagadhri)",
        "Bhiwani (Clock Tower)",
        "Sirsa (Barnala Road)",
        "Jind (Urban Estate)",
        "Bahadurgarh (Sector 6 / Metro)",
        "Manesar (IMT Industrial Area)",
        "Palwal (Agra Highway)"
    )

    val popularRoutes = listOf(
        PopularRoute("Gurugram", "Chandigarh / Panchkula", "285 km", 3400, "NH-44 Express"),
        PopularRoute("Panipat", "Delhi Airport (IGI)", "95 km", 1650, "GT Road Route"),
        PopularRoute("Ambala", "Kurukshetra", "48 km", 850, "Brahma Sarovar Special"),
        PopularRoute("Rohtak", "Hisar", "98 km", 1500, "Direct Highway"),
        PopularRoute("Faridabad", "Gurugram (Cyber City)", "35 km", 650, "Golf Course Ext.")
    )

    fun estimateDistanceKm(pickup: String, dest: String, tripType: TripType): Int {
        if (tripType == TripType.LOCAL) return 18
        val combined = (pickup.lowercase() + dest.lowercase())
        return when {
            combined.contains("chandigarh") || combined.contains("panchkula") -> 280
            combined.contains("airport") || combined.contains("delhi") -> 95
            combined.contains("ambala") && combined.contains("kurukshetra") -> 50
            combined.contains("rohtak") && combined.contains("hisar") -> 100
            combined.contains("gurugram") && combined.contains("panipat") -> 120
            combined.contains("faridabad") && combined.contains("gurugram") -> 35
            else -> 85
        }
    }

    fun calculateFare(distanceKm: Int, vehicleType: VehicleType, tripType: TripType): Int {
        val base = if (tripType == TripType.LOCAL) 250 else 500
        val kmFare = distanceKm * vehicleType.baseRatePerKm
        val gstAndToll = if (tripType == TripType.INTERCITY) 200 else 50
        return base + kmFare + gstAndToll
    }
}

data class PopularRoute(
    val from: String,
    val to: String,
    val distance: String,
    val startingFare: Int,
    val tag: String
)
