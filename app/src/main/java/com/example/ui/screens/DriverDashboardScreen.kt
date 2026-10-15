package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.model.TripType
import com.example.data.model.UserRole
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusAccepted
import com.example.ui.theme.StatusPending
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DriverDashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val isOnline by viewModel.isDriverOnline.collectAsState()

    val pendingBookings = bookings.filter { it.status == BookingStatus.PENDING }
    val myAcceptedBookings = bookings.filter {
        it.status == BookingStatus.ACCEPTED &&
                (it.driverId == currentUser?.id || currentUser?.role == UserRole.DRIVER)
    }
    val myCompletedBookings = bookings.filter {
        it.status == BookingStatus.COMPLETED &&
                (it.driverId == currentUser?.id || currentUser?.role == UserRole.DRIVER)
    }

    val todayEarnings = myCompletedBookings.sumOf { it.fareAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("driver_dashboard_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Driver Profile & Online Toggle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isOnline) EmeraldTertiary else Color.Gray,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = currentUser?.name ?: "Rajesh Kumar Saini",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "${currentUser?.vehicleModel.takeIf { !it.isNullOrBlank() } ?: "Maruti Dzire"} • ${currentUser?.vehicleNumber.takeIf { !it.isNullOrBlank() } ?: "HR 06 AB 7741"}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = "${currentUser?.rating ?: 4.9f} (Verified Haryana Driver)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Online/Offline switch
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isOnline) "ONLINE" else "OFFLINE",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = if (isOnline) EmeraldTertiary else Color.Gray
                            )
                            Switch(
                                checked = isOnline,
                                onCheckedChange = { viewModel.toggleDriverOnline() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = EmeraldTertiary
                                ),
                                modifier = Modifier.testTag("driver_online_switch")
                            )
                        }
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DriverStatCard(
                    title = "Pending Calls",
                    value = "${pendingBookings.size}",
                    color = AmberSecondary,
                    modifier = Modifier.weight(1f)
                )
                DriverStatCard(
                    title = "Active Trips",
                    value = "${myAcceptedBookings.size}",
                    color = StatusAccepted,
                    modifier = Modifier.weight(1f)
                )
                DriverStatCard(
                    title = "Completed",
                    value = "${myCompletedBookings.size}",
                    color = EmeraldTertiary,
                    modifier = Modifier.weight(1f)
                )
                DriverStatCard(
                    title = "Earnings",
                    value = "₹$todayEarnings",
                    color = NavyPrimary,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }

        // Active Accepted Trips Section
        if (myAcceptedBookings.isNotEmpty()) {
            item {
                Text(
                    text = "Active Trip in Progress",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusAccepted
                )
            }

            items(myAcceptedBookings) { trip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_trip_${trip.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StatusAccepted.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "DRIVER ASSIGNED (ACTIVE)",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = StatusAccepted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "OTP: ${trip.otp}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = EmeraldTertiary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Passenger Details
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
                            Text(
                                text = "${trip.passengerName} (${trip.passengerPhone})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Route Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldTertiary)
                            Text(text = "Pickup: ${trip.pickupLocation}", fontSize = 13.sp)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = AmberSecondary)
                            Text(text = "Drop: ${trip.destination}", fontSize = 13.sp)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Fare to Collect", fontSize = 11.sp, color = Color.Gray)
                                Text("₹${trip.fareAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AmberSecondary)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${trip.passengerPhone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.testTag("call_passenger_${trip.id}")
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call")
                                }

                                Button(
                                    onClick = { viewModel.completeBooking(trip.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldTertiary),
                                    modifier = Modifier.testTag("complete_trip_${trip.id}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Complete")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Available Booking Requests Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Haryana Booking Requests",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = CircleShape,
                    color = AmberSecondary,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${pendingBookings.size}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        if (pendingBookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Pending Booking Requests", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "New booking requests from Haryana passengers will appear here live.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pendingBookings) { booking ->
                BookingRequestCard(
                    booking = booking,
                    onAccept = { viewModel.acceptBooking(booking.id) },
                    onReject = { viewModel.rejectBooking(booking.id) },
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${booking.passengerPhone}")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun BookingRequestCard(
    booking: Booking,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("request_card_${booking.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (booking.tripType == TripType.INTERCITY) AmberSecondary.copy(alpha = 0.15f) else NavyPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = booking.tripType.displayName.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = if (booking.tripType == TripType.INTERCITY) AmberSecondary else NavyPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "ID: ${booking.id}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Route
            Text(
                text = "${booking.pickupLocation} ➔ ${booking.destination}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "📅 ${booking.travelDate} at ${booking.travelTime}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "👥 ${booking.passengerCount} Pax",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Passenger Contact Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${booking.passengerName} (${booking.passengerPhone})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onCall,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = EmeraldTertiary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 11.sp)
                }
            }

            if (booking.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 \"${booking.notes}\"",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Guaranteed Fare", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${booking.fareAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = AmberSecondary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.testTag("reject_booking_${booking.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Decline", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.testTag("accept_booking_${booking.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldTertiary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DriverStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = color)
            Text(text = title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
    }
}
