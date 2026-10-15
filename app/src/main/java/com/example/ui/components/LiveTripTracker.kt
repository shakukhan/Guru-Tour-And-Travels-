package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.StatusAccepted
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusPending

@Composable
fun LiveTripTrackerCard(
    booking: Booking,
    onCancel: () -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_trip_tracker_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Live Pulse indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = when (booking.status) {
                            BookingStatus.PENDING -> StatusPending
                            BookingStatus.ACCEPTED -> StatusAccepted
                            BookingStatus.COMPLETED -> StatusCompleted
                            else -> Color.Gray
                        },
                        modifier = Modifier.size(10.dp)
                    ) {}
                    Text(
                        text = "LIVE TRIP TRACKER",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = NavyPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (booking.status) {
                        BookingStatus.PENDING -> StatusPending.copy(alpha = 0.15f)
                        BookingStatus.ACCEPTED -> StatusAccepted.copy(alpha = 0.15f)
                        BookingStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.15f)
                        else -> Color.LightGray
                    }
                ) {
                    Text(
                        text = booking.status.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (booking.status) {
                            BookingStatus.PENDING -> Color(0xFF854D0E)
                            BookingStatus.ACCEPTED -> StatusAccepted
                            BookingStatus.COMPLETED -> StatusCompleted
                            else -> Color.DarkGray
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-step Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step 1: Requested
                TrackerStepNode(
                    label = "Requested",
                    isDone = true,
                    isActive = booking.status == BookingStatus.PENDING,
                    modifier = Modifier.weight(1f)
                )

                // Divider line
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .height(3.dp)
                        .background(
                            if (booking.status == BookingStatus.ACCEPTED || booking.status == BookingStatus.COMPLETED) StatusAccepted
                            else Color.LightGray.copy(alpha = 0.5f)
                        )
                )

                // Step 2: Driver Assigned
                TrackerStepNode(
                    label = "Driver Assigned",
                    isDone = booking.status == BookingStatus.ACCEPTED || booking.status == BookingStatus.COMPLETED,
                    isActive = booking.status == BookingStatus.ACCEPTED,
                    modifier = Modifier.weight(1f)
                )

                // Divider line
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .height(3.dp)
                        .background(
                            if (booking.status == BookingStatus.COMPLETED) StatusCompleted
                            else Color.LightGray.copy(alpha = 0.5f)
                        )
                )

                // Step 3: Completed
                TrackerStepNode(
                    label = "Completed",
                    isDone = booking.status == BookingStatus.COMPLETED,
                    isActive = booking.status == BookingStatus.COMPLETED,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Route points
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(18.dp))
                Text(text = booking.pickupLocation, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(18.dp))
                Text(text = booking.destination, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Driver details or Pending message
            if (booking.status == BookingStatus.ACCEPTED) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Driver: ${booking.driverName ?: "Rajesh Kumar Saini"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${booking.vehicleModel ?: "Maruti Dzire"} (${booking.vehicleNumber ?: "HR 06 AB 7741"})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Boarding OTP: ${booking.otp}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = EmeraldTertiary
                            )
                        }

                        Button(
                            onClick = {
                                val phone = booking.driverPhone ?: "+91 94160 88219"
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$phone")
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("tracker_call_driver_button")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call", fontSize = 12.sp)
                        }
                    }
                }
            } else if (booking.status == BookingStatus.PENDING) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberSecondary.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = AmberSecondary)
                        Column {
                            Text("Looking for nearby Haryana drivers...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Drivers in Gurugram, Panipat, and NCR are reviewing your booking.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Actions Row
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
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                            modifier = Modifier.testTag("tracker_cancel_button")
                        ) {
                            Text("Cancel", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onViewAll,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("tracker_view_all_button")
                    ) {
                        Text("View Details", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TrackerStepNode(
    label: String,
    isDone: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                isDone && !isActive -> EmeraldTertiary
                isActive -> AmberSecondary
                else -> Color.LightGray.copy(alpha = 0.5f)
            },
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isDone && !isActive) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                } else if (isActive) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) NavyPrimary else Color.Gray,
            maxLines = 1
        )
    }
}
