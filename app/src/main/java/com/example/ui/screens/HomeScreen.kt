package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BookingStatus
import com.example.data.model.HaryanaLocations
import com.example.data.model.TripType
import com.example.data.model.UserRole
import com.example.ui.components.LiveTripTrackerCard
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenTab

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val bookings by viewModel.bookings.collectAsState()
    val pendingCount = bookings.count { it.status == BookingStatus.PENDING }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Image Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.travel_haryana_banner),
                    contentDescription = "Guru Tour & Travels Haryana Highway Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0A2540)),
                                startY = 150f
                            )
                        )
                )

                // Floating Brand Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "HARYANA SPECIALIST",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Guru Tour & Travels",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Reliable Cabs & Tour Packages Across Haryana & NCR",
                        fontSize = 13.sp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        // Active Role Switcher Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("role_switch_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (currentUser?.role == UserRole.DRIVER) EmeraldTertiary else NavyPrimary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (currentUser?.role == UserRole.DRIVER) Icons.Default.DirectionsCar else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = if (currentUser?.role == UserRole.DRIVER) "Driver Mode Active" else "Passenger Mode Active",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = currentUser?.name ?: "Demo User",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val nextRole = if (currentUser?.role == UserRole.DRIVER) UserRole.PASSENGER else UserRole.DRIVER
                            viewModel.switchRole(nextRole)
                        },
                        modifier = Modifier.testTag("quick_switch_role_button")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (currentUser?.role == UserRole.DRIVER) "Be Passenger" else "Be Driver", fontSize = 12.sp)
                    }
                }
            }
        }

        // Live Active Booking Status Tracker
        val activeBooking = bookings.firstOrNull { it.status == BookingStatus.PENDING || it.status == BookingStatus.ACCEPTED }
        if (activeBooking != null) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    LiveTripTrackerCard(
                        booking = activeBooking,
                        onCancel = { viewModel.cancelBooking(activeBooking.id) },
                        onViewAll = { viewModel.navigateTo(ScreenTab.MY_TRIPS) }
                    )
                }
            }
        }

        // Action Buttons Row (Book Cab vs Driver Dashboard)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(ScreenTab.BOOKING) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("home_book_cab_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(Icons.Default.LocalTaxi, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Book a Cab", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Local & Intercity", fontSize = 11.sp, color = Color(0xFFDCE8F5))
                    }
                }

                Button(
                    onClick = { viewModel.navigateTo(ScreenTab.DRIVER_DASHBOARD) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .testTag("home_driver_dashboard_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary)
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Driver Feed", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("$pendingCount Pending", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }

        // Quick Travel Type Selection
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Choose Your Trip Style",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Local Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.startBookingFromRoute("Gurugram", "Manesar", TripType.LOCAL)
                            }
                            .testTag("quick_local_trip_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = NavyPrimary.copy(alpha = 0.12f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = NavyPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Local City Cab", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Hourly & Point-to-Point in Haryana cities", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Intercity Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                viewModel.startBookingFromRoute("Panipat", "Delhi Airport", TripType.INTERCITY)
                            }
                            .testTag("quick_intercity_trip_card"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = AmberSecondary.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, tint = AmberSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Intercity Express", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("One-way & Round trip across Haryana & NCR", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Popular Haryana Routes Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Popular Haryana Routes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Fixed transparent fares with no hidden surcharges",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(HaryanaLocations.popularRoutes) { route ->
                        Card(
                            modifier = Modifier
                                .width(230.dp)
                                .clickable {
                                    viewModel.startBookingFromRoute(route.from, route.to, TripType.INTERCITY)
                                }
                                .testTag("popular_route_${route.from}_${route.to}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldTertiary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = route.tag,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = EmeraldTertiary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${route.from} ➔ ${route.to}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Approx. ${route.distance}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "₹${route.startingFare}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberSecondary
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = NavyPrimary
                                    ) {
                                        Text(
                                            text = "Book",
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trust & Quality Badges
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Why Guru Tour & Travels?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FeatureBadge(icon = Icons.Default.CheckCircle, label = "Verified Drivers", sub = "Haryana registered")
                        FeatureBadge(icon = Icons.Default.Star, label = "4.9★ Rated", sub = "Top tourist fleet")
                        FeatureBadge(icon = Icons.Default.Call, label = "24x7 Support", sub = "Direct phone call")
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    sub: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(96.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = NavyPrimary.copy(alpha = 0.1f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text(text = sub, fontSize = 9.sp, color = Color.Gray)
    }
}
