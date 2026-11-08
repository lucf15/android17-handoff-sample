package it.lucf15

import android.app.*
import android.content.*
import android.os.*
import it.lucf15.ui.reader.ReaderContract.Event
import java.util.concurrent.atomic.AtomicReference

class HandoffReaderTestRunner : Instrumentation() {
    override fun onCreate(arguments: Bundle?) {
        super.onCreate(arguments)
        start()
    }

    override fun onStart() {
        val result = Bundle()
        var activity: MainActivity? = null
        var code = Activity.RESULT_OK
        try {
            activity = startActivitySync(incoming(3, "cold-contract")) as MainActivity
            val current = activity
            onMain {
                check(current.reader.state.pending?.section == 3)
                check(current.reader.state.article == null)
                check(!current.isHandoffEnabled)
                current.onReaderEvent(Event.Accept)
                checkPayload(current, 3, "cold-contract", true)
                current.onReaderEvent(Event.Position(5))
                checkPayload(current, 5, "cold-contract", false)
                // Restore the visible section after the direct payload callback check.
                current.onReaderEvent(Event.Position(3))
            }
            settleFrames()
            targetContext.startActivity(incoming(2, "dismiss-contract"))
            awaitIntent(current, "dismiss-contract")
            onMain {
                check(current.reader.state.pending?.section == 2)
                check(current.reader.state.section == 3)
                current.onReaderEvent(Event.Dismiss)
                checkPayload(current, 3, "cold-contract", false)
            }
            targetContext.startActivity(incoming(6, "warm-contract"))
            awaitIntent(current, "warm-contract")
            onMain {
                current.onReaderEvent(Event.Accept)
                checkPayload(current, 6, "warm-contract", true)
            }
            settleFrames()
            targetContext.startActivity(incoming(999, "invalid-contract"))
            awaitIntent(current, "invalid-contract")
            onMain {
                check(current.reader.state.pending == null)
                checkPayload(current, 6, "warm-contract", false)
            }
            onMain {
                current.onReaderEvent(Event.Back)
                check(!current.isHandoffEnabled)
            }
            result.putString(
                "stream",
                "PASS: explicit consent, decline preserves reading, active/cached payload, warm delivery, malformed position rejected, feed opts out. Synthetic contract tests, not cross-device evidence.\n",
            )
        } catch (t: Throwable) {
            code = Activity.RESULT_CANCELED
            result.putString("stream", "FAIL: ${t.stackTraceToString()}\n")
        } finally {
            activity?.let { runOnMainSync { it.finish() } }
        }
        finish(code, result)
    }

    private fun settleFrames() {
        waitForIdleSync()
        SystemClock.sleep(500)
        waitForIdleSync()
    }

    private fun incoming(section: Int, session: String) =
        Intent()
            .setComponent(ComponentName(targetContext, MainActivity::class.java))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra("document_id", "quiet-design")
            .putExtra("page_index", section)
            .putExtra("reading_session", session)

    private fun onMain(block: () -> Unit) {
        val failure = AtomicReference<Throwable>()
        runOnMainSync {
            try {
                block()
            } catch (t: Throwable) {
                failure.set(t)
            }
        }
        failure.get()?.let { throw AssertionError(it) }
    }

    private fun awaitIntent(activity: Activity, session: String) {
        val deadline = SystemClock.uptimeMillis() + 10000
        while (SystemClock.uptimeMillis() < deadline) {
            val value = AtomicReference<String>()
            runOnMainSync { value.set(activity.intent.getStringExtra("reading_session")) }
            if (value.get() == session) return
            SystemClock.sleep(50)
        }
        error("onNewIntent timed out")
    }

    private fun checkPayload(
        activity: MainActivity,
        section: Int,
        session: String,
        active: Boolean,
    ) {
        check(activity.isHandoffEnabled)
        val data = activity.onHandoffActivityDataRequested(HandoffActivityDataRequestInfo(active))
        check(data.componentName == activity.componentName)
        check(data.extras?.getString("document_id") == "quiet-design")
        check(data.extras?.getInt("page_index") == section) {
            "Expected section $section, actual ${data.extras?.getInt("page_index")}"
        }
        check(data.extras?.getString("reading_session") == session)
    }
}
