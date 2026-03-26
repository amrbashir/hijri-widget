package me.amrbashir.hijriwidget.preference_activity.screens.preferences

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Brightness6
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreTime
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.outlined.TextIncrease
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import me.amrbashir.hijriwidget.R
import me.amrbashir.hijriwidget.android.AlarmReceiver
import me.amrbashir.hijriwidget.formatTime
import me.amrbashir.hijriwidget.preference_activity.LocalAppBarTitle
import me.amrbashir.hijriwidget.preference_activity.LocalNavController
import me.amrbashir.hijriwidget.preference_activity.LocalPreferencesManager
import me.amrbashir.hijriwidget.preference_activity.composableWithAnimatedContentScopeProvider
import me.amrbashir.hijriwidget.preference_activity.composables.PreferenceScreenLayout
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceGroup
import me.amrbashir.hijriwidget.preference_activity.composables.ui.PreferenceTemplate
import me.amrbashir.hijriwidget.preference_activity.composables.ui.ValueSlider
import me.amrbashir.hijriwidget.preference_activity.screens.navigateToAbout

const val PREFERENCES_DESTINATION = "/preferences"
const val PREFERENCES_LIST_DESTINATION = "/preferences/"

fun NavGraphBuilder.preferenceListDestination() {
    composableWithAnimatedContentScopeProvider(route = PREFERENCES_LIST_DESTINATION) { PreferenceListScreen() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PreferenceListScreen() {
    LocalAppBarTitle.current.value = stringResource(R.string.app_name)

    val prefsManager = LocalPreferencesManager.current
    val navController = LocalNavController.current
    val context = LocalContext.current

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var showLocationDialog by remember { mutableStateOf(false) }
    var tempLat by remember { mutableStateOf(prefsManager.prayerLatitude.value.toString()) }
    var tempLng by remember { mutableStateOf(prefsManager.prayerLongitude.value.toString()) }
    var isFetchingLocation by remember { mutableStateOf(false) }

    val fetchLocation = {
        isFetchingLocation = true
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    isFetchingLocation = false
                    if (location != null) {
                        tempLat = location.latitude.toString()
                        tempLng = location.longitude.toString()
                        Toast.makeText(context, "Location updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Cannot get location. Ensure GPS is ON.", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    isFetchingLocation = false
                    Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
        } catch (e: SecurityException) {
            isFetchingLocation = false
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchLocation()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    var showMethodDialog by remember { mutableStateOf(false) }
    val adhanMethods = listOf("MUSLIM_WORLD_LEAGUE", "EGYPTIAN", "KARACHI", "UMM_AL_QURA", "DUBAI", "QATAR", "SINGAPORE", "NORTH_AMERICA")

    PreferenceScreenLayout {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {

            PreferenceGroup(label = "Prayer & Auto-Silent") {

                PreferenceTemplate(
                    label = "Prayer Location",
                    description = "Lat: ${prefsManager.prayerLatitude.value}, Lng: ${prefsManager.prayerLongitude.value}",
                    icon = Icons.Outlined.Place,
                    onClick = {
                        tempLat = prefsManager.prayerLatitude.value.toString()
                        tempLng = prefsManager.prayerLongitude.value.toString()
                        showLocationDialog = true
                    }
                )

                PreferenceTemplate(
                    label = "Calculation Method",
                    description = prefsManager.prayerCalcMethod.value.replace("_", " "),
                    icon = Icons.Outlined.Calculate,
                    onClick = {
                        showMethodDialog = true
                    }
                )

                PreferenceTemplate(
                    label = "Auto-Silent Mode",
                    description = "Mute phone during prayer times. Requires DND permission.",
                    icon = Icons.Outlined.NotificationsOff,
                    endContent = {
                        Switch(
                            checked = prefsManager.autoSilentEnabled.value,
                            onCheckedChange = { isChecked ->
                                prefsManager.autoSilentEnabled.value = isChecked
                                if (isChecked) {
                                    val notificationManager = context.getSystemService(android.app.NotificationManager::class.java)
                                    if (!notificationManager.isNotificationPolicyAccessGranted) {
                                        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        )
                    },
                    onClick = {
                        val newValue = !prefsManager.autoSilentEnabled.value
                        prefsManager.autoSilentEnabled.value = newValue
                        if (newValue) {
                            val notificationManager = context.getSystemService(android.app.NotificationManager::class.java)
                            if (!notificationManager.isNotificationPolicyAccessGranted) {
                                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                                context.startActivity(intent)
                            }
                        }
                    }
                )

                PreferenceTemplate(
                    label = "Silent Duration",
                    description = "Duration to keep the phone muted (${prefsManager.silentDuration.value} mins)",
                    icon = Icons.Outlined.Timer,
                ) {
                    ValueSlider(
                        value = prefsManager.silentDuration.value.toFloat(),
                        onValueChange = { prefsManager.silentDuration.value = it.toInt() },
                        default = prefsManager.silentDuration.default.toFloat(),
                        valueRange = 5F..45F,
                        steps = 8,
                    )
                }
            }

            PreferenceGroup(label = stringResource(R.string.preferences_functionality_group_title)) {
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_date_format_title),
                    description = stringResource(R.string.preferences_date_format_description),
                    icon = Icons.Outlined.Translate,
                    onClick = { navController.navigateToDateFormat() }
                )
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_day_start_title, prefsManager.dayStart.value.formatTime() ?: ""),
                    description = stringResource(R.string.preferences_day_start_description),
                    icon = Icons.Outlined.AccessTime,
                    onClick = { navController.navigateToDayStart() }
                )
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_calendar_calculation_method_title),
                    description = stringResource(R.string.preferences_calendar_calculation_method_description),
                    icon = Icons.Outlined.CalendarMonth,
                    onClick = { navController.navigateToCalendarCalculationMethod() }
                )
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_day_offset_title),
                    description = stringResource(R.string.preferences_day_offset_description),
                    icon = Icons.Outlined.MoreTime,
                ) {
                    ValueSlider(
                        value = prefsManager.dayOffset.value.toFloat(),
                        onValueChange = { prefsManager.dayOffset.value = it.toInt() },
                        default = prefsManager.dayOffset.default.toFloat(),
                        valueRange = -2F..2F,
                        steps = 3,
                    )
                }
            }

            PreferenceGroup(label = stringResource(R.string.preferences_appearance_group_title)) {
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_color_title),
                    description = stringResource(R.string.preferences_color_description),
                    icon = Icons.Outlined.ColorLens,
                    onClick = { navController.navigateToColor() }
                )
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_text_size_title),
                    description = stringResource(R.string.preferences_text_size_description),
                    icon = Icons.Outlined.TextIncrease,
                ) {
                    ValueSlider(
                        value = prefsManager.textSize.value,
                        onValueChange = { prefsManager.textSize.value = it },
                        default = prefsManager.textSize.default,
                        valueRange = 1F..50F,
                        steps = 50,
                    )
                }
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_text_shadow_title),
                    description = stringResource(R.string.preferences_text_shadow_description),
                    icon = Icons.Outlined.Brightness6,
                    endContent = {
                        Switch(
                            checked = prefsManager.textShadow.value,
                            onCheckedChange = { prefsManager.textShadow.value = it }
                        )
                    },
                    onClick = { prefsManager.textShadow.value = !prefsManager.textShadow.value }
                )
            }

            PreferenceGroup(label = stringResource(R.string.preferences_misc_group_title)) {
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_restore_defaults_title),
                    description = stringResource(R.string.preferences_restore_defaults_description),
                    icon = Icons.Outlined.SettingsBackupRestore,
                    onClick = { prefsManager.reset() }
                )
                PreferenceTemplate(
                    label = stringResource(R.string.preferences_about_title),
                    description = stringResource(R.string.preferences_about_description),
                    icon = Icons.Outlined.Info,
                    onClick = { navController.navigateToAbout() }
                )
            }
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Set Coordinates") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempLat,
                        onValueChange = { tempLat = it },
                        label = { Text("Latitude") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = tempLng,
                        onValueChange = { tempLng = it },
                        label = { Text("Longitude") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            if (hasFine || hasCoarse) {
                                fetchLocation()
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isFetchingLocation
                    ) {
                        Text(if (isFetchingLocation) "Fetching GPS..." else "Auto Detect with GPS")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    prefsManager.prayerLatitude.value = tempLat.toFloatOrNull() ?: prefsManager.prayerLatitude.value
                    prefsManager.prayerLongitude.value = tempLng.toFloatOrNull() ?: prefsManager.prayerLongitude.value
                    showLocationDialog = false

                    context.sendBroadcast(Intent(context, AlarmReceiver::class.java))
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showMethodDialog) {
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = { Text("Calculation Method") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    adhanMethods.forEach { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    prefsManager.prayerCalcMethod.value = method
                                    showMethodDialog = false
                                    context.sendBroadcast(Intent(context, AlarmReceiver::class.java))
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = (prefsManager.prayerCalcMethod.value == method),
                                onClick = {
                                    prefsManager.prayerCalcMethod.value = method
                                    showMethodDialog = false
                                    context.sendBroadcast(Intent(context, AlarmReceiver::class.java))
                                }
                            )
                            Text(
                                text = method.replace("_", " "),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMethodDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
