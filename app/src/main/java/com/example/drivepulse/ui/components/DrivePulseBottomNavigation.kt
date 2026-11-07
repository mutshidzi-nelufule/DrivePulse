package com.example.drivepulse.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.drivepulse.R
import com.example.drivepulse.ui.navigation.NavRoutes

enum class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    Dashboard(NavRoutes.DASHBOARD, R.string.nav_dashboard, Icons.Default.Dashboard),
    Vehicles(NavRoutes.VEHICLES, R.string.nav_vehicles, Icons.Default.DirectionsCar),
    Fuel(NavRoutes.FUEL, R.string.nav_fuel, Icons.Default.LocalGasStation),
    Maintenance(NavRoutes.MAINTENANCE, R.string.nav_services, Icons.Default.Build),
    Reports(NavRoutes.REPORTS, R.string.nav_reports, Icons.Default.BarChart)
}

@Composable
fun DrivePulseBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = BottomNavItem.entries

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            val titleText = stringResource(item.titleResId)
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = titleText) },
                label = { Text(text = titleText) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
        }
    }
}
