package ui.preview

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
internal val DesktopWindowSizeClass = WindowSizeClass.calculateFromSize(
    DpSize(width = 1000.dp, height = 1000.dp)
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
internal val PhoneWindowSizeClass = WindowSizeClass.calculateFromSize(
    DpSize(width = 500.dp, height = 800.dp)
)