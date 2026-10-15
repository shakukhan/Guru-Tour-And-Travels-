package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.TripType
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.model.VehicleType
import com.example.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab(val title: String) {
    HOME("Home"),
    BOOKING("Book Cab"),
    DRIVER_DASHBOARD("Driver"),
    MY_TRIPS("Trips"),
    PROFILE("Account"),
    HELP("Help & Info")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookingRepository.getInstance(application)

    val currentUser: StateFlow<UserAccount?> = repository.currentUser
    val bookings: StateFlow<List<Booking>> = repository.bookings
    val isFirebaseActive: StateFlow<Boolean> = repository.isFirebaseActive

    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    private val _prefilledPickup = MutableStateFlow("")
    val prefilledPickup: StateFlow<String> = _prefilledPickup.asStateFlow()

    private val _prefilledDestination = MutableStateFlow("")
    val prefilledDestination: StateFlow<String> = _prefilledDestination.asStateFlow()

    private val _selectedTripType = MutableStateFlow(TripType.INTERCITY)
    val selectedTripType: StateFlow<TripType> = _selectedTripType.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage: SharedFlow<String> = _snackBarMessage.asSharedFlow()

    private val _isDriverOnline = MutableStateFlow(true)
    val isDriverOnline: StateFlow<Boolean> = _isDriverOnline.asStateFlow()

    fun navigateTo(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun toggleDriverOnline() {
        _isDriverOnline.value = !_isDriverOnline.value
        val msg = if (_isDriverOnline.value) "You are now ONLINE and visible to Haryana passengers." else "You are now OFFLINE."
        emitMessage(msg)
    }

    fun startBookingFromRoute(pickup: String, destination: String, tripType: TripType) {
        _prefilledPickup.value = pickup
        _prefilledDestination.value = destination
        _selectedTripType.value = tripType
        _currentTab.value = ScreenTab.BOOKING
    }

    fun clearPrefilledRoute() {
        _prefilledPickup.value = ""
        _prefilledDestination.value = ""
    }

    fun setTripType(type: TripType) {
        _selectedTripType.value = type
    }

    fun createBooking(
        pickup: String,
        destination: String,
        tripType: TripType,
        travelDate: String,
        travelTime: String,
        passengerCount: Int,
        vehicleType: VehicleType,
        notes: String,
        passengerName: String = "",
        passengerPhone: String = ""
    ): Booking {
        val booking = repository.createBooking(
            pickup = pickup,
            destination = destination,
            tripType = tripType,
            travelDate = travelDate,
            travelTime = travelTime,
            passengerCount = passengerCount,
            vehicleType = vehicleType,
            notes = notes,
            passengerName = passengerName,
            passengerPhone = passengerPhone
        )
        emitMessage("Booking confirmed! ID: ${booking.id}. Finding nearby Haryana cab...")
        return booking
    }

    fun acceptBooking(bookingId: String) {
        val driver = currentUser.value
        if (driver != null) {
            repository.acceptBooking(bookingId, driver)
            emitMessage("Booking accepted! Passenger notified.")
        }
    }

    fun rejectBooking(bookingId: String) {
        repository.rejectBooking(bookingId)
        emitMessage("Booking request declined.")
    }

    fun completeBooking(bookingId: String) {
        repository.completeBooking(bookingId)
        emitMessage("Trip marked as Completed! Receipt generated.")
    }

    fun cancelBooking(bookingId: String) {
        repository.cancelBooking(bookingId)
        emitMessage("Booking cancelled.")
    }

    fun switchRole(role: UserRole) {
        repository.switchRole(role)
        val roleName = if (role == UserRole.DRIVER) "Driver" else "Passenger"
        emitMessage("Switched to $roleName Mode")
        if (role == UserRole.DRIVER && _currentTab.value == ScreenTab.BOOKING) {
            _currentTab.value = ScreenTab.DRIVER_DASHBOARD
        } else if (role == UserRole.PASSENGER && _currentTab.value == ScreenTab.DRIVER_DASHBOARD) {
            _currentTab.value = ScreenTab.BOOKING
        }
    }

    fun loginDemoPassenger() {
        repository.loginDemoPassenger()
        emitMessage("Logged in as Demo Passenger (Amit Sharma)")
    }

    fun loginDemoDriver() {
        repository.loginDemoDriver()
        emitMessage("Logged in as Demo Driver (Rajesh Kumar - HR 06 AB 7741)")
    }

    fun registerUser(user: UserAccount) {
        repository.registerUser(user)
        emitMessage("Account created successfully for ${user.name}!")
    }

    fun loginUser(email: String, role: UserRole) {
        repository.loginUser(email, role)
        emitMessage("Welcome back! Signed in as ${if (role == UserRole.DRIVER) "Driver" else "Passenger"}.")
    }

    fun logout() {
        repository.logout()
        emitMessage("Signed out. Reset to Demo Passenger.")
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _snackBarMessage.emit(msg)
        }
    }
}
