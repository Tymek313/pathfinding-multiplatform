package ui.preview

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
internal val DesktopWindowSizeClass = WindowSizeClass.calculateFromSize(
    Size(width = 1000f, height = 1000f),
    Density(density = 1f, fontScale = 1f)
)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
internal val PhoneWindowSizeClass = WindowSizeClass.calculateFromSize(
    Size(width = 800f, height = 800f),
    Density(density = 1f, fontScale = 1f)
)