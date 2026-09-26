package com.example.drivepulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.drivepulse.ui.components.DrivePulseButton
import com.example.drivepulse.ui.components.DrivePulseTextField
import com.example.drivepulse.ui.components.ErrorMessageCard
import com.example.drivepulse.ui.viewmodel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    vehicleViewModel: VehicleViewModel,
    onNavigateBack: () -> Unit
) {
    val form = vehicleViewModel.formState
    val isEditMode = form.id != null
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Vehicle" else "Add New Vehicle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (form.errorMessage != null) {
                ErrorMessageCard(message = form.errorMessage)
            }

            DrivePulseTextField(
                value = form.make,
                onValueChange = { vehicleViewModel.onMakeChange(it) },
                label = "Make (e.g., Toyota, Ford, VW)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.model,
                onValueChange = { vehicleViewModel.onModelChange(it) },
                label = "Model (e.g., Corolla 1.8, Ranger 2.0)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.yearText,
                onValueChange = { vehicleViewModel.onYearChange(it) },
                label = "Year (e.g., 2022)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.regNumber,
                onValueChange = { vehicleViewModel.onRegNumberChange(it) },
                label = "Registration Number (e.g., ND 123-456)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.odometerText,
                onValueChange = { vehicleViewModel.onOdometerChange(it) },
                label = "Current Odometer Reading (km)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.vin,
                onValueChange = { vehicleViewModel.onVinChange(it) },
                label = "VIN (Optional, up to 17 chars)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.fuelType,
                onValueChange = { vehicleViewModel.onFuelTypeChange(it) },
                label = "Fuel Type (Petrol, Diesel, Hybrid, Electric)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.serviceIntervalText,
                onValueChange = { vehicleViewModel.onServiceIntervalChange(it) },
                label = "Service Interval (km, e.g., 15000)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = form.isDefault,
                    onCheckedChange = { vehicleViewModel.onIsDefaultChange(it) }
                )
                Text(
                    text = "Set as default primary vehicle",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DrivePulseButton(
                text = if (isEditMode) "Update Vehicle" else "Save Vehicle",
                onClick = {
                    vehicleViewModel.saveVehicle(onSuccess = onNavigateBack)
                },
                isLoading = form.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
