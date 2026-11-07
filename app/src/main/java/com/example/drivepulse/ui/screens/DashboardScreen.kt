package com.example.drivepulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.drivepulse.R
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.drivepulse.ui.components.DrivePulseButton
import com.example.drivepulse.ui.theme.StatusError
import com.example.drivepulse.ui.theme.StatusSuccess
import com.example.drivepulse.ui.theme.StatusWarning
import com.example.drivepulse.ui.viewmodel.AuthViewModel
import com.example.drivepulse.ui.viewmodel.DashboardViewModel

import com.example.drivepulse.ui.components.DrivePulseBottomNavigation
import com.example.drivepulse.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val user = authViewModel.currentUser
    val state = dashboardViewModel.uiState
    val vehicle = state.selectedVehicle
    val health = state.healthScore

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DrivePulse",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(NavRoutes.VEHICLES) }) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "My Vehicles",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { onNavigate(NavRoutes.SETTINGS) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { authViewModel.logout(onLoggedOut = onLogout) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            DrivePulseBottomNavigation(
                currentRoute = NavRoutes.DASHBOARD,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User & Selected Vehicle Welcome Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hello, ${user?.fullName ?: "Driver"}! 👋",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = vehicle?.getDisplayName() ?: "No vehicle selected",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Vehicle Health Score Card
            if (health != null) {
                HealthScoreCard(healthResult = health)
            }

            // Quick Action Buttons
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DrivePulseButton(
                    text = stringResource(R.string.btn_add_fuel),
                    onClick = { dashboardViewModel.openQuickAddFuel() },
                    icon = Icons.Default.LocalGasStation,
                    modifier = Modifier.weight(1f)
                )
                DrivePulseButton(
                    text = stringResource(R.string.btn_add_service),
                    onClick = { dashboardViewModel.openQuickAddMaintenance() },
                    icon = Icons.Default.Build,
                    modifier = Modifier.weight(1f)
                )
            }

            // Key Statistics Grid
            Text(
                text = "Running Stats Summary",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    title = stringResource(R.string.label_current_odometer),
                    value = "${vehicle?.currentOdometer ?: 0} km",
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCard(
                    title = stringResource(R.string.label_avg_fuel_economy),
                    value = if (state.avgFuelConsumption > 0) "%.1f L/100km".format(state.avgFuelConsumption) else "-- L/100km",
                    icon = Icons.Default.LocalGasStation,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardStatCard(
                    title = stringResource(R.string.label_fuel_spent),
                    value = "R %.2f".format(state.totalFuelSpending),
                    icon = Icons.Default.LocalGasStation,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCard(
                    title = stringResource(R.string.label_maint_spent),
                    value = "R %.2f".format(state.totalMaintenanceSpending),
                    icon = Icons.Default.Build,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Quick Add Fuel Dialog
    if (state.isQuickAddFuelOpen) {
        QuickAddFuelDialog(
            currentOdometer = vehicle?.currentOdometer ?: 0,
            onDismiss = { dashboardViewModel.closeQuickAddFuel() },
            onConfirm = { odo, litres, price, date ->
                dashboardViewModel.quickAddFuel(odo, litres, price, date)
            }
        )
    }

    // Quick Add Maintenance Dialog
    if (state.isQuickAddMaintenanceOpen) {
        QuickAddMaintenanceDialog(
            currentOdometer = vehicle?.currentOdometer ?: 0,
            onDismiss = { dashboardViewModel.closeQuickAddMaintenance() },
            onConfirm = { type, cost, odo, date, nextOdo ->
                dashboardViewModel.quickAddMaintenance(type, cost, odo, date, nextOdo)
            }
        )
    }
}

@Composable
fun HealthScoreCard(healthResult: com.example.drivepulse.util.HealthScoreResult) {
    val scoreColor = when {
        healthResult.score >= 85 -> StatusSuccess
        healthResult.score >= 70 -> StatusWarning
        else -> StatusError
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Vehicle Health Score",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = healthResult.statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scoreColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Gauge Score Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color = scoreColor.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${healthResult.score}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = scoreColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = healthResult.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            if (healthResult.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                healthResult.recommendations.forEach { rec ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (healthResult.score >= 85) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = scoreColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = rec,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun QuickAddFuelDialog(
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onConfirm: (odometer: Int, litres: Double, price: Double, date: String) -> Unit
) {
    var odoText by remember { mutableStateOf(currentOdometer.toString()) }
    var litresText by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("23.80") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Add Fuel Log") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (errorMsg != null) {
                    Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = odoText,
                    onValueChange = { odoText = it },
                    label = { Text("Odometer Reading (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = litresText,
                    onValueChange = { litresText = it },
                    label = { Text("Litres Filled") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price Per Litre (R)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val odo = odoText.toIntOrNull()
                val litres = litresText.toDoubleOrNull()
                val price = priceText.toDoubleOrNull()
                if (odo == null || odo < 0) {
                    errorMsg = "Please enter a valid odometer reading."
                } else if (litres == null || litres <= 0) {
                    errorMsg = "Please enter valid fuel litres."
                } else if (price == null || price <= 0) {
                    errorMsg = "Please enter a valid price."
                } else {
                    onConfirm(odo, litres, price, "2026-03-25")
                }
            }) {
                Text("Save Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun QuickAddMaintenanceDialog(
    currentOdometer: Int,
    onDismiss: () -> Unit,
    onConfirm: (serviceType: String, cost: Double, odometer: Int, date: String, nextOdometer: Int) -> Unit
) {
    var serviceType by remember { mutableStateOf("Oil Change") }
    var costText by remember { mutableStateOf("") }
    var odoText by remember { mutableStateOf(currentOdometer.toString()) }
    var nextOdoText by remember { mutableStateOf((currentOdometer + 15000).toString()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Maintenance Service") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (errorMsg != null) {
                    Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = { Text("Service Type (e.g., Oil Change)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Cost (R)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = odoText,
                    onValueChange = { odoText = it },
                    label = { Text("Service Odometer (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = nextOdoText,
                    onValueChange = { nextOdoText = it },
                    label = { Text("Next Service Due At (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val cost = costText.toDoubleOrNull()
                val odo = odoText.toIntOrNull()
                val nextOdo = nextOdoText.toIntOrNull() ?: 0
                if (serviceType.isBlank()) {
                    errorMsg = "Service type cannot be empty."
                } else if (cost == null || cost < 0) {
                    errorMsg = "Please enter a valid cost."
                } else if (odo == null || odo < 0) {
                    errorMsg = "Please enter a valid odometer reading."
                } else {
                    onConfirm(serviceType, cost, odo, "2026-03-25", nextOdo)
                }
            }) {
                Text("Save Service")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
