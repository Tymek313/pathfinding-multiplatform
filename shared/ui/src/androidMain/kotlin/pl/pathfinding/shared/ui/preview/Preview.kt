package pl.pathfinding.shared.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

private const val desktopDeviceSpec = "spec:width=1366dp,height=768dp"

@Preview(name = "desktop", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_NO, device = desktopDeviceSpec)
@Preview(name = "desktop - night", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES, device = desktopDeviceSpec)
internal annotation class DesktopPreview

private const val phoneDeviceSpec = "spec:width=411dp,height=891dp"

@Preview(name = "phone", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_NO, device = phoneDeviceSpec)
@Preview(name = "phone - night", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES, device = phoneDeviceSpec)
internal annotation class PhonePreview