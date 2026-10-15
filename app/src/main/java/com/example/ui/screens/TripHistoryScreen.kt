package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.LiveTripTrackerCard
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusAccepted
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusPending
import com.example.ui.viewmodel.MainViewModel

@Composable
fun TripHistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val bookings by viewModel.bookings.collectAsState()

    var selectedStatusFilter by remember { mutableStateOf<BookingStatus?>(null) }
    var selectedBookingForDetail by remember { mutableStateOf<Booking?>(null) }

    val filteredList = bookings.filter { booking ->
        if (selectedStatusFilter != null && booking.status != selectedStatusFilter) {
            false
        } else {
            true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("trip_history_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = NavyPrimary,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color.White)
                        }
                    }
                    Column {
                        Text(
                            text = if (currentUser?.role == UserRole.DRIVER) "Driver Trip Logs" else "My Travel Bookings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track real-time trip status across Haryana",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Active Trip Live Status Tracker
        val activeTrip = bookings.firstOrNull { it.status == BookingStatus.PENDING || it.status == BookingStatus.ACCEPTED }
        if (activeTrip != null) {
            item {
                LiveTripTrackerCard(
                    booking = activeTrip,
                    onCancel = { viewModel.cancelBooking(activeTrip.id) },
                    onViewAll = { selectedBookingForDetail = activeTrip }
                )
            }
        }

        // Status Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedStatusFilter == null,
                        onClick = { selectedStatusFilter = null },
                        label = { Text("All (${bookings.size})") },
                        modifier = Modifier.testTag("filter_all_trips")
                    )
                }
                BookingStatus.values().forEach { status ->
                    val count = bookings.count { it.status == status }
                    item {
                        FilterChip(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = status },
                            label = { Text("${status.displayName} ($count)") },
                            modifier = Modifier.testTag("filter_${status.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Trips List
        if (filteredList.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Trips Found", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("There are no bookings matching this status filter.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(filteredList) { booking ->
                TripHistoryCard(
                    booking = booking,
                    onClickDetail = { selectedBookingForDetail = booking },
                    onCancel = { viewModel.cancelBooking(booking.id) },
                    onCallDriver = {
                        val phone = booking.driverPhone ?: "+91 98120 00000"
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phone")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }

    // Trip Details & Receipt Dialog
    if (selectedBookingForDetail != null) {
        val b = selectedBookingForDetail!!
        AlertDialog(
            onDismissRequest = { selectedBookingForDetail = null },
            icon = {
                Surface(
                    shape = CircleShape,
                    color = when (b.status) {
                        BookingStatus.PENDING -> StatusPending
                        BookingStatus.ACCEPTED -> StatusAccepted
                        BookingStatus.COMPLETED -> StatusCompleted
                        BookingStatus.CANCELLED, BookingStatus.REJECTED -> StatusCancelled
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White)
                    }
                }
            },
            title = {
                Text(
                    text = "Booking Details & Receipt",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Booking ID:", fontWeight = FontWeight.Bold)
                        Text(b.id, fontWeight = FontWeight.Bold, color = NavyPrimary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status:")
                        StatusBadge(status = b.status)
                    }
                    HorizontalDivider()
                    Text("📍 Pickup: ${b.pickupLocation}", fontSize = 13.sp)
                    Text("🎯 Drop: ${b.destination}", fontSize = 13.sp)
                    Text("📅 Schedule: ${b.travelDate} at ${b.travelTime}", fontSize = 13.sp)
                    Text("🚘 Vehicle: ${b.vehicleType.title}", fontSize = 13.sp)
                    Text("👥 Passengers: ${b.passengerCount}", fontSize = 13.sp)

                    if (b.status == BookingStatus.ACCEPTED || b.status == BookingStatus.COMPLETED) {
                        HorizontalDivider()
                        Text("Driver: ${b.driverName ?: "Rajesh Kumar"}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("Vehicle No: ${b.vehicleNumber ?: "HR 06 AB 7741"}", fontSize = 13.sp)
                        Text("Driver Phone: ${b.driverPhone ?: "+91 94160 88219"}", fontSize = 13.sp)
                        Text("Boarding OTP: ${b.otp}", fontWeight = FontWeight.Bold, color = EmeraldTertiary, fontSize = 14.sp)
                    }

                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Estimated Fare:", fontWeight = FontWeight.Bold)
                        Text("₹${b.fareAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AmberSecondary)
                    }
                    Text(
                        text = "Includes Haryana road permits, tolls, and standard GST.",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedBookingForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun TripHistoryCard(
    booking: Booking,
    onClickDetail: () -> Unit,
    onCancel: () -> Unit,
    onCallDriver: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickDetail() }
            .testTag("trip_card_${booking.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status & Booking ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = booking.status)
                Text(
                    text = booking.id,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route Points
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(18.dp))
                Text(
                    text = booking.pickupLocation,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(18.dp))
                Text(
                    text = booking.destination,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Schedule & Pax
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "🗓️ ${booking.travelDate} • ${booking.travelTime}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "🚗 ${booking.vehicleType.title}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Driver Assigned Info Box
            if (booking.status == BookingStatus.ACCEPTED) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusAccepted.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Assigned Driver: ${booking.driverName ?: "Rajesh Kumar"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${booking.vehicleModel ?: "Dzire"} (${booking.vehicleNumber ?: "HR 06 AB 7741"}) • OTP: ${booking.otp}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = onCallDriver,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call Driver", tint = StatusAccepted)
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            // Footer: Fare & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Fare", fontSize = 11.sp, color = Color.Gray)
                    Text("₹${booking.fareAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AmberSecondary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (booking.status == BookingStatus.PENDING) {
                        TextButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Cancel", fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = onClickDetail,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Receipt", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: BookingStatus) {
    val (bgColor, textColor, label) = when (status) {
        BookingStatus.PENDING -> Triple(StatusPending.copy(alpha = 0.15f), Color(0xFF854D0E), "Pending Driver")
        BookingStatus.ACCEPTED -> Triple(StatusAccepted.copy(alpha = 0.15f), StatusAccepted, "Driver Assigned")
        BookingStatus.COMPLETED -> Triple(StatusCompleted.copy(alpha = 0.15f), StatusCompleted, "Completed")
        BookingStatus.CANCELLED -> Triple(StatusCancelled.copy(alpha = 0.15f), StatusCancelled, "Cancelled")
        BookingStatus.REJECTED -> Triple(StatusCancelled.copy(alpha = 0.15f), StatusCancelled, "Declined")
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
