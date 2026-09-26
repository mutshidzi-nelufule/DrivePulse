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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.drivepulse.ui.viewmodel.FuelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFuelScreen(
    fuelViewModel: FuelViewModel,
    onNavigateBack: () -> Unit
) {
    val form = fuelViewModel.formState
    val totalCost = fuelViewModel.calculateCurrentTotalCost()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Fuel Fill-Up", fontWeight = FontWeight.Bold) },
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

            // Total Cost Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Calculated Total Cost", style = MaterialTheme.typography.bodySmall)
                    Text("R %.2f".format(totalCost), style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                }
            }

            DrivePulseTextField(
                value = form.odometerText,
                onValueChange = { fuelViewModel.onOdometerChange(it) },
                label = "Current Odometer Reading (km)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.litresText,
                onValueChange = { fuelViewModel.onLitresChange(it) },
                label = "Litres Filled",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.pricePerLitreText,
                onValueChange = { fuelViewModel.onPriceChange(it) },
                label = "Price Per Litre (R)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.fuelStation,
                onValueChange = { fuelViewModel.onStationChange(it) },
                label = "Fuel Station Name (Optional)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            DrivePulseTextField(
                value = form.notes,
                onValueChange = { fuelViewModel.onNotesChange(it) },
                label = "Notes (Optional)",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = form.isFullTank,
                    onCheckedChange = { fuelViewModel.onIsFullTankChange(it) }
                )
                Text(
                    text = "Filled to full tank (Recommended for accurate L/100km)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DrivePulseButton(
                text = "Save Fuel Log",
                onClick = {
                    fuelViewModel.saveFuelLog(onSuccess = onNavigateBack)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
