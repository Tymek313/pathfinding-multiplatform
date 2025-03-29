package pl.pathfinding.shared.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

internal actual val currentLanguage: String @Composable get() = LocalConfiguration.current.locales[0].language