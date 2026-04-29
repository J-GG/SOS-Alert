package fr.jg.sosalert.widget

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import fr.jg.sosalert.R
import fr.jg.sosalert.ui.theme.darkScheme
import fr.jg.sosalert.ui.theme.lightScheme

private val glanceColors = ColorProviders(light = lightScheme, dark = darkScheme)

class SosAlertWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val hasRequiredPermissions = ContextCompat.checkSelfPermission(
            context, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED)

        provideContent {
            GlanceTheme(colors = glanceColors) {
                if (hasRequiredPermissions) {
                    WidgetContent(context)
                } else {
                    WidgetPermissionError(context)
                }
            }
        }
    }
}

@Composable
private fun WidgetContent(context: Context) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(100.dp)
            .background(GlanceTheme.colors.error)
            .clickable(actionRunCallback<SendAlertAction>()),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = context.getString(R.string.widget_button),
            style = TextStyle(
                color = GlanceTheme.colors.onError,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun WidgetPermissionError(context: Context) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(100.dp)
            .background(GlanceTheme.colors.inverseSurface)
            .clickable(actionStartActivity(
                Intent(context, RequestPermissionsOnWidgetClickActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = context.getString(R.string.widget_button),
            style = TextStyle(
                color = GlanceTheme.colors.inverseOnSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}
