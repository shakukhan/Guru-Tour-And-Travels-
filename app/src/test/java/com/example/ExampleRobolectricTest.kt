package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BookingStatus
import com.example.data.model.HaryanaLocations
import com.example.data.model.TripType
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.data.model.VehicleType
import com.example.data.repository.BookingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Guru Travels", appName)
  }

  @Test
  fun `test Haryana fare estimation`() {
    val distance = HaryanaLocations.estimateDistanceKm("Panipat", "Delhi Airport", TripType.INTERCITY)
    assertTrue("Distance should be positive", distance > 0)

    val fareSedan = HaryanaLocations.calculateFare(distance, VehicleType.SEDAN, TripType.INTERCITY)
    val fareSuv = HaryanaLocations.calculateFare(distance, VehicleType.SUV, TripType.INTERCITY)

    assertTrue("SUV fare should be higher than Sedan", fareSuv > fareSedan)
  }

  @Test
  fun `test booking submission with phone number and driver acceptance`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = BookingRepository.getInstance(context)

    val booking = repo.createBooking(
      pickup = "Gurugram (Cyber Hub)",
      destination = "Chandigarh",
      tripType = TripType.INTERCITY,
      travelDate = "Today",
      travelTime = "06:00 PM",
      passengerCount = 3,
      vehicleType = VehicleType.SEDAN,
      notes = "Carry 2 bags",
      passengerName = "Suresh Verma",
      passengerPhone = "+91 98123 99999"
    )

    assertEquals("Suresh Verma", booking.passengerName)
    assertEquals("+91 98123 99999", booking.passengerPhone)
    assertEquals(BookingStatus.PENDING, booking.status)

    // Driver accepts
    val driver = UserAccount(
      id = "test_driver_1",
      name = "Baljit Singh",
      phone = "+91 94160 11111",
      email = "baljit@example.com",
      role = UserRole.DRIVER,
      city = "Karnal",
      vehicleModel = "Dzire",
      vehicleNumber = "HR 05 CD 1234"
    )

    repo.acceptBooking(booking.id, driver)
    val updated = repo.bookings.value.first { it.id == booking.id }
    assertEquals(BookingStatus.ACCEPTED, updated.status)
    assertEquals("Baljit Singh", updated.driverName)
  }
}
