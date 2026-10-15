package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingStatus
import com.example.data.model.UserRole
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.ContactHelpScreen
import com.example.ui.screens.DriverDashboardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.TripHistoryScreen
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val currentUser by viewModel.currentUser.collectAsState()
                val bookings by viewModel.bookings.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }

                val pendingCount = bookings.count { it.status == BookingStatus.PENDING }

                // Collect snackbar messages
                LaunchedEffect(Unit) {
                    viewModel.snackBarMessage.collect { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                }

                // Handle system back navigation
                if (currentTab != ScreenTab.HOME) {
                    BackHandler {
                        viewModel.navigateTo(ScreenTab.HOME)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.guru_travels_logo),
                                        contentDescription = "Guru Tour & Travels Logo",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Guru Tour & Travels",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                }
                            },
                            actions = {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (currentUser?.role == UserRole.DRIVER) AmberSecondary else EmeraldTertiary,
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (currentUser?.role == UserRole.DRIVER) "DRIVER" else "PASSENGER",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = NavyPrimary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("main_bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == ScreenTab.HOME,
                                onClick = { viewModel.navigateTo(ScreenTab.HOME) },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyPrimary,
                                    selectedTextColor = NavyPrimary
                                ),
                                modifier = Modifier.testTag("nav_tab_home")
                            )

                            NavigationBarItem(
                                selected = currentTab == ScreenTab.BOOKING,
                                onClick = { viewModel.navigateTo(ScreenTab.BOOKING) },
                                icon = { Icon(Icons.Default.LocalTaxi, contentDescription = "Book") },
                                label = { Text("Book", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyPrimary,
                                    selectedTextColor = NavyPrimary
                                ),
                                modifier = Modifier.testTag("nav_tab_booking")
                            )

                            NavigationBarItem(
                                selected = currentTab == ScreenTab.DRIVER_DASHBOARD,
                                onClick = { viewModel.navigateTo(ScreenTab.DRIVER_DASHBOARD) },
                                icon = {
                                    BadgedBox(
                                        badge = {
                                            if (pendingCount > 0) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = AmberSecondary,
                                                    modifier = Modifier.size(16.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Text("$pendingCount", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.DirectionsCar, contentDescription = "Driver")
                                    }
                                },
                                label = { Text("Driver", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AmberSecondary,
                                    selectedTextColor = AmberSecondary
                                ),
                                modifier = Modifier.testTag("nav_tab_driver")
                            )

                            NavigationBarItem(
                                selected = currentTab == ScreenTab.MY_TRIPS,
                                onClick = { viewModel.navigateTo(ScreenTab.MY_TRIPS) },
                                icon = { Icon(Icons.Default.History, contentDescription = "Trips") },
                                label = { Text("Trips", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyPrimary,
                                    selectedTextColor = NavyPrimary
                                ),
                                modifier = Modifier.testTag("nav_tab_trips")
                            )

                            NavigationBarItem(
                                selected = currentTab == ScreenTab.PROFILE,
                                onClick = { viewModel.navigateTo(ScreenTab.PROFILE) },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                                label = { Text("Account", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyPrimary,
                                    selectedTextColor = NavyPrimary
                                ),
                                modifier = Modifier.testTag("nav_tab_profile")
                            )

                            NavigationBarItem(
                                selected = currentTab == ScreenTab.HELP,
                                onClick = { viewModel.navigateTo(ScreenTab.HELP) },
                                icon = { Icon(Icons.Default.Help, contentDescription = "Help") },
                                label = { Text("Help", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyPrimary,
                                    selectedTextColor = NavyPrimary
                                ),
                                modifier = Modifier.testTag("nav_tab_help")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
                            ScreenTab.BOOKING -> BookingScreen(viewModel = viewModel)
                            ScreenTab.DRIVER_DASHBOARD -> DriverDashboardScreen(viewModel = viewModel)
                            ScreenTab.MY_TRIPS -> TripHistoryScreen(viewModel = viewModel)
                            ScreenTab.PROFILE -> AuthScreen(viewModel = viewModel)
                            ScreenTab.HELP -> ContactHelpScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
