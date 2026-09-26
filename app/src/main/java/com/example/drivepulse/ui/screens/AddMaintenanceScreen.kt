package com.example.drivepulse.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.drivepulse.ui.components.DrivePulseButton
import com.example.drivepulse.ui.components.DrivePulseTextField
import com.example.drivepulse.ui.components.ErrorMessageCard
import com.example.drivepulse.ui.viewmodel.MaintenanceViewModel
import com.example.drivepulse.util.MaintenanceValidator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddMaintenanceScreen(
    viewModel: MaintenanceViewModel,
    onNavigateBack: () -> Unit
) {
    val form = viewModel.formState
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Maintenance Service", fontWeight = FontWeight.Bold) },
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

            Text("Select Service Category", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MaintenanceValidator.SERVICE_CATEGORIES.forEach { category ->
                    FilterChip(
                        selected = form.serviceType == category,
                        onClick = { viewModel.onServiceTypeChange(category) },
                        label = { Text(category) }
                    )
                }
            }

            DrivePulseTextField(
                value = form.serviceType,
                onValueChange = { viewModel.onServiceTypeChange(it) },
                label = "Service Type",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.costText,
                onValueChange = { viewModel.onCostChange(it) },
                label = "Cost (R)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.odometerText,
                onValueChange = { viewModel.onOdometerChange(it) },
                label = "Current Odometer Reading (km)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.nextOdometerText,
                onValueChange = { viewModel.onNextOdometerChange(it) },
                label = "Next Service Due Odometer (km)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = "Description / Notes (Optional)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(16.dp))

            DrivePulseButton(
                text = "Save Service Record",
                onClick = {
                    viewModel.saveMaintenanceRecord(onSuccess = onNavigateBack)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
