package pl.pathfinding.shared.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

private const val DESKTOP_DEVICE_SPEC = "spec:width=1366dp,height=768dp"

@Preview(name = "desktop", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_NO, device = DESKTOP_DEVICE_SPEC)
@Preview(
    name = "desktop - night",
    locale = "en",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = DESKTOP_DEVICE_SPEC
)
internal annotation class DesktopPreview

private const val PHONE_DEVICE_SPEC = "spec:width=411dp,height=891dp"

@Preview(name = "phone", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_NO, device = PHONE_DEVICE_SPEC)
@Preview(name = "phone - night", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES, device = PHONE_DEVICE_SPEC)
internal annotation class PhonePreview
