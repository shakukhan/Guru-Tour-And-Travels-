package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.HaryanaLocations
import com.example.data.model.TripType
import com.example.data.model.VehicleType
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenTab

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val prefilledPickup by viewModel.prefilledPickup.collectAsState()
    val prefilledDestination by viewModel.prefilledDestination.collectAsState()
    val selectedTripTypeFromVm by viewModel.selectedTripType.collectAsState()

    var tripType by remember { mutableStateOf(selectedTripTypeFromVm) }
    var pickupLocation by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf("Today") }
    var travelTime by remember { mutableStateOf("Now (Immediate)") }
    var passengerCount by remember { mutableIntStateOf(2) }
    var selectedVehicle by remember { mutableStateOf(VehicleType.SEDAN) }
    var notes by remember { mutableStateOf("") }

    val currentUser by viewModel.currentUser.collectAsState()
    var passengerName by remember { mutableStateOf(currentUser?.name ?: "Amit Sharma") }
    var passengerPhone by remember { mutableStateOf(currentUser?.phone ?: "+91 98120 45678") }

    var createdBooking by remember { mutableStateOf<Booking?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (passengerName.isBlank()) passengerName = currentUser?.name ?: "Amit Sharma"
        if (passengerPhone.isBlank()) passengerPhone = currentUser?.phone ?: "+91 98120 45678"
    }

    LaunchedEffect(prefilledPickup, prefilledDestination, selectedTripTypeFromVm) {
        if (prefilledPickup.isNotBlank()) pickupLocation = prefilledPickup
        if (prefilledDestination.isNotBlank()) destination = prefilledDestination
        tripType = selectedTripTypeFromVm
    }

    // Default suggestions if empty
    if (pickupLocation.isBlank() && prefilledPickup.isBlank()) {
        pickupLocation = "Gurugram (Cyber Hub)"
    }
    if (destination.isBlank() && prefilledDestination.isBlank()) {
        destination = "Chandigarh / Panchkula"
    }

    val distanceKm = remember(pickupLocation, destination, tripType) {
        HaryanaLocations.estimateDistanceKm(pickupLocation, destination, tripType)
    }

    val estimatedFare = remember(distanceKm, selectedVehicle, tripType) {
        HaryanaLocations.calculateFare(distanceKm, selectedVehicle, tripType)
    }

    val quickHaryanaCities = listOf(
        "Gurugram", "Faridabad", "Panipat", "Ambala",
        "Rohtak", "Hisar", "Karnal", "Sonipat", "Kurukshetra"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("booking_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Book Your Haryana Journey",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Clean, air-conditioned cabs with commercial permits across Haryana & Delhi NCR",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Passenger Contact Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Passenger Contact Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passengerName,
                        onValueChange = { passengerName = it },
                        label = { Text("Passenger Full Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("passenger_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passengerPhone,
                        onValueChange = { passengerPhone = it },
                        label = { Text("Mobile Phone Number (+91)") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldTertiary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("passenger_phone_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Trip Type Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trip Type",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = tripType == TripType.LOCAL,
                            onClick = {
                                tripType = TripType.LOCAL
                                viewModel.setTripType(TripType.LOCAL)
                            },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("trip_type_local")
                        ) {
                            Text("Local City Trip")
                        }
                        SegmentedButton(
                            selected = tripType == TripType.INTERCITY,
                            onClick = {
                                tripType = TripType.INTERCITY
                                viewModel.setTripType(TripType.INTERCITY)
                            },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("trip_type_intercity")
                        ) {
                            Text("Intercity / Outstation")
                        }
                    }
                }
            }
        }

        // Pickup & Destination Inputs
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Route Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Pickup Field
                    OutlinedTextField(
                        value = pickupLocation,
                        onValueChange = { pickupLocation = it },
                        label = { Text("Pickup Location in Haryana") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldTertiary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pickup_location_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Pickup Chips
                    Text(
                        text = "Popular Pickups:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        quickHaryanaCities.take(5).forEach { city ->
                            FilterChip(
                                selected = pickupLocation.contains(city, ignoreCase = true),
                                onClick = { pickupLocation = "$city (City Center)" },
                                label = { Text(city, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Destination Field
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Destination / Drop-off City") },
                        leadingIcon = {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = AmberSecondary)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("destination_location_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Destination Chips
                    Text(
                        text = "Popular Destinations:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        quickHaryanaCities.takeLast(5).forEach { city ->
                            FilterChip(
                                selected = destination.contains(city, ignoreCase = true),
                                onClick = { destination = "$city (Main Stand / Hub)" },
                                label = { Text(city, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Date, Time & Passenger Count
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Schedule & Passengers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Travel Date selection
                    Text(text = "Travel Date", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Today", "Tomorrow", "Weekend").forEach { dateOption ->
                            FilterChip(
                                selected = travelDate == dateOption,
                                onClick = { travelDate = dateOption },
                                label = { Text(dateOption) },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Travel Time selection
                    Text(text = "Travel Time", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 6.dp)
                    ) {
                        listOf("Now (Immediate)", "07:00 AM", "10:30 AM", "02:00 PM", "06:30 PM", "09:30 PM").forEach { timeOption ->
                            FilterChip(
                                selected = travelTime == timeOption,
                                onClick = { travelTime = timeOption },
                                label = { Text(timeOption, fontSize = 11.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Passenger Count Stepper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Number of Passengers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = if (passengerCount <= 4) "Hatchback or Sedan suitable" else if (passengerCount <= 6) "SUV / Ertiga recommended" else "Tempo Traveler recommended",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (passengerCount > 1) passengerCount-- },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("passenger_minus_button")
                            ) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(32.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Text(
                                text = "$passengerCount",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            IconButton(
                                onClick = { if (passengerCount < 14) passengerCount++ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("passenger_plus_button")
                            ) {
                                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface, modifier = Modifier.size(32.dp)) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Vehicle Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Select Vehicle Category",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    VehicleType.values().forEach { vType ->
                        val fare = HaryanaLocations.calculateFare(distanceKm, vType, tripType)
                        val isSelected = selectedVehicle == vType

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedVehicle = vType }
                                .testTag("vehicle_card_${vType.name}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, NavyPrimary) else null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) NavyPrimary else Color.LightGray.copy(alpha = 0.5f),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (vType == VehicleType.TEMPO) Icons.Default.AirportShuttle else Icons.Default.DirectionsCar,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else Color.DarkGray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(text = vType.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "${vType.subtitle} • Up to ${vType.capacity} Seats", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Text(
                                    text = "₹$fare",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = if (isSelected) NavyPrimary else AmberSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Special Notes
        item {
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Special Requests / Luggage Notes (Optional)") },
                placeholder = { Text("e.g. 2 large suitcases, AC throughout, pet friendly") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("booking_notes_input"),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Fare Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trip Fare Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estimated Distance", fontSize = 13.sp)
                        Text("~ $distanceKm km", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Vehicle Category", fontSize = 13.sp)
                        Text(selectedVehicle.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tolls & State Taxes", fontSize = 13.sp)
                        Text(if (tripType == TripType.INTERCITY) "Included (Estimated)" else "Included", fontSize = 13.sp)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Estimated Fare", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Pay driver directly via Cash or UPI", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = "₹$estimatedFare",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = AmberSecondary
                        )
                    }
                }
            }
        }

        // Confirm Button
        item {
            Button(
                onClick = {
                    val booking = viewModel.createBooking(
                        pickup = pickupLocation.ifBlank { "Gurugram" },
                        destination = destination.ifBlank { "Panipat" },
                        tripType = tripType,
                        travelDate = travelDate,
                        travelTime = travelTime,
                        passengerCount = passengerCount,
                        vehicleType = selectedVehicle,
                        notes = notes,
                        passengerName = passengerName,
                        passengerPhone = passengerPhone
                    )
                    createdBooking = booking
                    showConfirmationDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_booking_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm Booking (₹$estimatedFare)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    // Booking Confirmation Dialog
    if (showConfirmationDialog && createdBooking != null) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            icon = {
                Surface(shape = CircleShape, color = EmeraldTertiary, modifier = Modifier.size(52.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            },
            title = {
                Text(
                    text = "Booking Confirmed!",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Your ride request has been dispatched to available Haryana drivers.",
                        fontSize = 13.sp
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Booking ID: ${createdBooking?.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Route: ${createdBooking?.pickupLocation} ➔ ${createdBooking?.destination}", fontSize = 12.sp)
                            Text("Date & Time: ${createdBooking?.travelDate} at ${createdBooking?.travelTime}", fontSize = 12.sp)
                            Text("Total Fare: ₹${createdBooking?.fareAmount}", fontWeight = FontWeight.Bold, color = AmberSecondary, fontSize = 14.sp)
                            Text("Ride Security OTP: ${createdBooking?.otp}", fontWeight = FontWeight.Bold, color = EmeraldTertiary, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        viewModel.clearPrefilledRoute()
                        viewModel.navigateTo(ScreenTab.MY_TRIPS)
                    },
                    modifier = Modifier.testTag("dialog_view_trips_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("View in Trips")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmationDialog = false }) {
                    Text("Book Another")
                }
            }
        )
    }
}
