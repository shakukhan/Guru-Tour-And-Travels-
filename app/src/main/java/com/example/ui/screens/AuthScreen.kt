package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldTertiary
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.MainViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedRole by remember { mutableStateOf(currentUser?.role ?: UserRole.PASSENGER) }
    var isSignUp by remember { mutableStateOf(false) }

    // Form fields
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Gurugram") }
    var vehicleModel by remember { mutableStateOf("Maruti Suzuki Dzire") }
    var vehicleNumber by remember { mutableStateOf("HR 26 DL 4492") }
    var licenseNumber by remember { mutableStateOf("HR-26-2019-00481") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("current_profile_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
                                color = if (currentUser?.role == UserRole.DRIVER) EmeraldTertiary else NavyPrimary,
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (currentUser?.role == UserRole.DRIVER) Icons.Default.DirectionsCar else Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = currentUser?.name ?: "Guest User",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${currentUser?.role?.name} • ${currentUser?.city}",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                if (currentUser?.role == UserRole.DRIVER && !currentUser?.vehicleNumber.isNullOrBlank()) {
                                    Text(
                                        text = "${currentUser?.vehicleModel} (${currentUser?.vehicleNumber})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AmberSecondary
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("sign_out_button")
                        ) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color.Red)
                        }
                    }
                }
            }
        }

        // Quick Demo Accounts Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Instant 1-Tap Demo Switcher",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Easily test both passenger booking & driver acceptance flows:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.loginDemoPassenger()
                                selectedRole = UserRole.PASSENGER
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_passenger_login_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Passenger", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.loginDemoDriver()
                                selectedRole = UserRole.DRIVER
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("demo_driver_login_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Driver", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Role & Sign-in / Sign-up selector
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Account Portal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Role Segmented Control
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = selectedRole == UserRole.PASSENGER,
                            onClick = { selectedRole = UserRole.PASSENGER },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            modifier = Modifier.testTag("auth_role_passenger")
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Passenger")
                        }
                        SegmentedButton(
                            selected = selectedRole == UserRole.DRIVER,
                            onClick = { selectedRole = UserRole.DRIVER },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            modifier = Modifier.testTag("auth_role_driver")
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Driver")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sign In vs Sign Up Tabs
                    TabRow(
                        selectedTabIndex = if (isSignUp) 1 else 0,
                        containerColor = Color.Transparent
                    ) {
                        Tab(
                            selected = !isSignUp,
                            onClick = { isSignUp = false },
                            text = { Text("Log In", fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = isSignUp,
                            onClick = { isSignUp = true },
                            text = { Text("Register New", fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Form Fields
                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_input_name"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Mobile Number (+91)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_input_phone"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Base City in Haryana") },
                            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_input_city"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Driver specific fields
                        if (selectedRole == UserRole.DRIVER) {
                            OutlinedTextField(
                                value = vehicleModel,
                                onValueChange = { vehicleModel = it },
                                label = { Text("Vehicle Make & Model (e.g. Maruti Dzire, Ertiga)") },
                                leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_input_vmodel"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = vehicleNumber,
                                onValueChange = { vehicleNumber = it },
                                label = { Text("Commercial Vehicle No (e.g. HR 26 AB 1234)") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_input_vnumber"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = licenseNumber,
                                onValueChange = { licenseNumber = it },
                                label = { Text("Driving License Number") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_input_license"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_input_email"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_input_password"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Action Button
                    Button(
                        onClick = {
                            if (isSignUp) {
                                val newUser = UserAccount(
                                    id = "usr_" + UUID.randomUUID().toString().take(8),
                                    name = name.ifBlank { if (selectedRole == UserRole.PASSENGER) "Vikas Sharma" else "Suresh Singh" },
                                    phone = phone.ifBlank { "+91 98120 77123" },
                                    email = email.ifBlank { "user@gurutravels.com" },
                                    role = selectedRole,
                                    city = city.ifBlank { "Gurugram" },
                                    vehicleModel = if (selectedRole == UserRole.DRIVER) vehicleModel.ifBlank { "Maruti Dzire" } else "",
                                    vehicleNumber = if (selectedRole == UserRole.DRIVER) vehicleNumber.ifBlank { "HR 26 AB 1234" } else "",
                                    licenseNumber = if (selectedRole == UserRole.DRIVER) licenseNumber.ifBlank { "HR-26-2020-0099" } else ""
                                )
                                viewModel.registerUser(newUser)
                            } else {
                                viewModel.loginUser(email.ifBlank { "demo@gurutravels.com" }, selectedRole)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedRole == UserRole.DRIVER) AmberSecondary else NavyPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isSignUp) "Create ${selectedRole.name.lowercase().capitalize()} Account" else "Log In to Guru Travels",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    GoogleSignInButton(
                        selectedRole = selectedRole,
                        onAuthSuccess = { displayName, userEmail, uid ->
                            val user = UserAccount(
                                id = uid,
                                name = displayName.ifBlank { if (selectedRole == UserRole.PASSENGER) "Google Passenger" else "Google Driver" },
                                phone = phone.ifBlank { "+91 98120 45678" },
                                email = userEmail,
                                role = selectedRole,
                                city = city.ifBlank { "Gurugram" },
                                vehicleModel = if (selectedRole == UserRole.DRIVER) vehicleModel.ifBlank { "Maruti Dzire" } else "",
                                vehicleNumber = if (selectedRole == UserRole.DRIVER) vehicleNumber.ifBlank { "HR 26 AB 1234" } else ""
                            )
                            viewModel.registerUser(user)
                        },
                        onAuthError = { err ->
                            Log.w("AuthScreen", "Google Sign-In note: $err")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GoogleSignInButton(
    selectedRole: UserRole,
    onAuthSuccess: (name: String, email: String, uid: String) -> Unit,
    onAuthError: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = {
            isLoading = true
            val clientId = try {
                context.getString(com.example.R.string.default_web_client_id)
            } catch (e: Exception) {
                isLoading = false
                onAuthError("Missing Google Client ID: ${e.message}")
                return@OutlinedButton
            }

            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

            coroutineScope.launch {
                try {
                    val activity = context as? Activity
                    if (activity == null) {
                        isLoading = false
                        onAuthError("Activity context not available")
                        return@launch
                    }
                    val result = credentialManager.getCredential(activity, request)
                    val credential = result.credential
                    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(authCredential)
                            .addOnSuccessListener { authResult ->
                                isLoading = false
                                val firebaseUser = authResult.user
                                if (firebaseUser != null) {
                                    onAuthSuccess(
                                        firebaseUser.displayName ?: "",
                                        firebaseUser.email ?: "",
                                        firebaseUser.uid
                                    )
                                } else {
                                    onAuthError("Firebase user null")
                                }
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                Log.e("Auth", "Firebase auth failed", exception)
                                onAuthError(exception.localizedMessage ?: "Firebase authentication failed")
                            }
                    } else {
                        isLoading = false
                        onAuthError("Unexpected credential type returned")
                    }
                } catch (e: GetCredentialCancellationException) {
                    Log.w("Auth", "Google Sign-In cancelled or dismissed: ${e.message}", e)
                    isLoading = false
                } catch (e: Exception) {
                    Log.e("Auth", "Google Sign-In failed", e)
                    isLoading = false
                    onAuthError(e.localizedMessage ?: "Sign-in failed")
                }
            }
        },
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("google_sign_in_button"),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EmeraldTertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in with Google Account",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

