package fr.jg.sosalert.widget

import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RequestPermissionsOnWidgetClickActivity : BaseWidgetPermissionsActivity() {

    override fun onPermissionsHandled() {
        lifecycleScope.launch {
            SosAlertWidget().updateAll(this@RequestPermissionsOnWidgetClickActivity)
            finish()
        }
    }
}
