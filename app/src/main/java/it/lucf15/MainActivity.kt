package it.lucf15

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import it.lucf15.app.*
import it.lucf15.domain.usecase.ValidateReading
import it.lucf15.platform.HandoffAdapter
import it.lucf15.ui.feed.*
import it.lucf15.ui.reader.*

class MainActivity : ComponentActivity() {
    private val dependencies
        get() = application as ReaderApplication

    internal val reader: ReaderViewModel by viewModels {
        viewModelFactory {
            initializer {
                ReaderViewModel(
                    dependencies.articles,
                    dependencies.library,
                    createSavedStateHandle(),
                )
            }
        }
    }
    private val feed: FeedViewModel by viewModels {
        viewModelFactory {
            initializer { FeedViewModel(dependencies.articles, dependencies.library) }
        }
    }
    private val handoff by lazy { HandoffAdapter(this, ValidateReading(dependencies.articles)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null)
            handoff.receive(intent)?.let { reader.onEvent(ReaderContract.Event.Receive(it)) }
        setContent { ReaderApp(feed.state, reader.state, feed::onEvent, ::onReaderEvent) }
        updateHandoff()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handoff.receive(intent)?.let { onReaderEvent(ReaderContract.Event.Receive(it)) }
    }

    internal fun onReaderEvent(event: ReaderContract.Event) {
        reader.onEvent(event)
        if (event !is ReaderContract.Event.Position) updateHandoff()
        if (event == ReaderContract.Event.Accept)
            reader.capture()?.let {
                Log.i(
                    HandoffAdapter.TAG,
                    "RESTORED article=${it.articleId} section=${it.section} session=${it.session}",
                )
            }
    }

    private fun updateHandoff() =
        handoff.setEnabled(reader.state.article != null && reader.state.pending == null)

    override fun onHandoffActivityDataRequested(
        info: HandoffActivityDataRequestInfo,
    ): HandoffActivityData = handoff.provide(reader.capture(), info)
}
