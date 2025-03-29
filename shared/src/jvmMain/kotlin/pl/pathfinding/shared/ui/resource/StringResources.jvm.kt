package pl.pathfinding.shared.ui.resource

import java.util.Locale

@Suppress("ConstantLocale") // No need to dynamically detect language change on desktop
internal actual val currentLanguage: String = Locale.getDefault().language