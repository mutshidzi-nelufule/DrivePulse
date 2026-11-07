package com.example.drivepulse.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.drivepulse.R
import com.example.drivepulse.data.local.Currency
import com.example.drivepulse.data.local.DistanceUnit
import com.example.drivepulse.data.local.FuelUnit
import com.example.drivepulse.data.local.ThemeMode
import com.example.drivepulse.ui.components.DrivePulseButton
import com.example.drivepulse.ui.components.DrivePulseBottomNavigation
import com.example.drivepulse.ui.navigation.NavRoutes
import com.example.drivepulse.ui.viewmodel.AuthViewModel
import com.example.drivepulse.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val user = authViewModel.currentUser
    val settings = settingsViewModel.settings
    val vehicles = settingsViewModel.vehicles

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            DrivePulseBottomNavigation(
                currentRoute = NavRoutes.SETTINGS,
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
            // Profile Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.fullName ?: "DrivePulse User",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = user?.email ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // PROMINENT LANGUAGE SELECTION CARD (ENGLISH & TSHIVENDA)
            SettingsSectionCard(title = "${stringResource(R.string.settings_language)} / Luambo", icon = Icons.Default.Language) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButtonOption(
                        label = "English 🇿🇦",
                        selected = settings.language == "English",
                        onClick = { settingsViewModel.updateLanguage("English") }
                    )
                    RadioButtonOption(
                        label = "Tshivenda 🇿🇦",
                        selected = settings.language == "Tshivenda",
                        onClick = { settingsViewModel.updateLanguage("Tshivenda") }
                    )
                }
            }

            // Theme Preference Section
            SettingsSectionCard(title = stringResource(R.string.settings_app_theme), icon = Icons.Default.Palette) {
                Column {
                    ThemeOptionRow(
                        label = "System Default",
                        selected = settings.themeMode == ThemeMode.SYSTEM,
                        onClick = { settingsViewModel.updateTheme(ThemeMode.SYSTEM) }
                    )
                    ThemeOptionRow(
                        label = "Light Theme",
                        selected = settings.themeMode == ThemeMode.LIGHT,
                        onClick = { settingsViewModel.updateTheme(ThemeMode.LIGHT) }
                    )
                    ThemeOptionRow(
                        label = "Dark Theme",
                        selected = settings.themeMode == ThemeMode.DARK,
                        onClick = { settingsViewModel.updateTheme(ThemeMode.DARK) }
                    )
                }
            }

            // Regional & Units Section
            SettingsSectionCard(title = stringResource(R.string.settings_currency_units), icon = Icons.Default.Payments) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Currency Symbol", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Currency.entries.forEach { curr ->
                            RadioButtonOption(
                                label = "${curr.name} (${when (curr) { Currency.ZAR -> "R"; Currency.USD -> "$"; Currency.EUR -> "€"; Currency.GBP -> "£" }})",
                                selected = settings.currency == curr,
                                onClick = { settingsViewModel.updateCurrency(curr) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Distance Unit", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row {
                        RadioButtonOption("Kilometres (km)", selected = settings.distanceUnit == DistanceUnit.KM) {
                            settingsViewModel.updateDistanceUnit(DistanceUnit.KM)
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        RadioButtonOption("Miles (mi)", selected = settings.distanceUnit == DistanceUnit.MILES) {
                            settingsViewModel.updateDistanceUnit(DistanceUnit.MILES)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Fuel Volume Unit", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Row {
                        RadioButtonOption("Litres (L)", selected = settings.fuelUnit == FuelUnit.LITRES) {
                            settingsViewModel.updateFuelUnit(FuelUnit.LITRES)
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        RadioButtonOption("Gallons (gal)", selected = settings.fuelUnit == FuelUnit.GALLONS) {
                            settingsViewModel.updateFuelUnit(FuelUnit.GALLONS)
                        }
                    }
                }
            }

            // Notifications Section
            SettingsSectionCard(title = stringResource(R.string.settings_notifications), icon = Icons.Default.Notifications) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.settings_notifications), style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { settingsViewModel.updateNotifications(it) }
                    )
                }
            }

            // Default Vehicle Preference
            if (vehicles.isNotEmpty()) {
                SettingsSectionCard(title = stringResource(R.string.settings_primary_vehicle), icon = Icons.Default.DirectionsCar) {
                    Column {
                        vehicles.forEach { vehicle ->
                            ThemeOptionRow(
                                label = vehicle.getDisplayName(),
                                selected = settings.defaultVehicleId == vehicle.id,
                                onClick = { settingsViewModel.updateDefaultVehicle(vehicle.id) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Logout Action Button
            DrivePulseButton(
                text = stringResource(R.string.settings_logout),
                onClick = {
                    authViewModel.logout(onLoggedOut = onLogout)
                }
            )
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun ThemeOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun RadioButtonOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
