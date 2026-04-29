package fr.jg.sosalert.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle

class SosAlertWidgetConfigActivity : BaseWidgetPermissionsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setResult(RESULT_CANCELED)
        super.onCreate(savedInstanceState)
    }

    override fun onPermissionsHandled() {
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, result)
        finish()
    }
}
