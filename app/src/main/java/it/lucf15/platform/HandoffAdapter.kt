package it.lucf15.platform

import android.app.*
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import it.lucf15.domain.model.Reading
import it.lucf15.domain.usecase.ValidateReading

class HandoffAdapter(private val activity: Activity, private val validate: ValidateReading) {
    fun receive(intent: Intent): Reading? {
        if (!intent.hasExtra(DOCUMENT)) return null
        val reading =
            validate(
                intent.getStringExtra(DOCUMENT),
                intent.getIntExtra(SECTION, -1),
                intent.getStringExtra(SESSION),
            )
        if (reading == null) Log.w(TAG, "REJECTED incoming state")
        else
            Log.i(
                TAG,
                "OFFER article=${reading.articleId} section=${reading.section} session=${reading.session}",
            )
        return reading
    }

    fun setEnabled(enabled: Boolean) = activity.setHandoffEnabled(enabled, null)

    fun provide(reading: Reading?, info: HandoffActivityDataRequestInfo): HandoffActivityData {
        val extras = PersistableBundle()
        reading?.let {
            extras.putString(DOCUMENT, it.articleId)
            extras.putInt(SECTION, it.section)
            extras.putString(SESSION, it.session)
        }
        Log.i(
            TAG,
            "DATA_REQUEST active=${info.isActiveRequest} article=${reading?.articleId} section=${reading?.section} session=${reading?.session}",
        )
        return HandoffActivityData.Builder(activity.componentName).setExtras(extras).build()
    }

    companion object {
        const val TAG = "HandoffSample"
        const val DOCUMENT = "document_id"
        const val SECTION = "page_index"
        const val SESSION = "reading_session"
    }
}
