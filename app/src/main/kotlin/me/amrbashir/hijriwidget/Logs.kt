package me.amrbashir.hijriwidget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

internal object Logs {
	enum class Status { Loading, Ready, Failed }

	data class State(
		val logs: String = "",
		val status: Status = Status.Loading,
		val error: Int? = null,
	)

	private val scope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
	private val mutableState by lazy {
		MutableStateFlow(State()).also { state ->
			scope.launch {
				state.subscriptionCount.collectLatest { subscribers ->
					if (subscribers > 0) {
						while (isActive) {
							refresh()
							delay(1000)
						}
					}
				}
			}
		}
	}
	val state: StateFlow<State> get() = mutableState.asStateFlow()

	suspend fun refresh() {
		val onSuccess: (String) -> Unit = { text ->
			mutableState.update { current ->
				val error = if (current.error == R.string.logs_read_failed) null else current.error

				current.copy(
					logs = text,
					status = Status.Ready,
					error = error,
				)
			}
		}

		val onFailure: (Throwable) -> Unit = { failure ->
			logException(failure)
			mutableState.update { it.copy(status = Status.Failed, error = R.string.logs_read_failed) }
		}

		withContext(Dispatchers.IO) {
			runCatching { read() }
				.onSuccess(onSuccess)
				.onFailure(onFailure)
		}
	}

	suspend fun clear() {
		mutableState.update { it.copy(status = Status.Loading, error = null) }
		try {
			withContext(Dispatchers.IO) { clearLogcat() }
			refresh()
		} catch (failure: Exception) {
			if (failure is CancellationException) throw failure
			logException(failure)
			mutableState.update { it.copy(status = Status.Failed, error = R.string.logs_clear_failed) }
		}
	}

	fun send(context: Context) {
		val current = mutableState.value
		if (current.status != Status.Ready) return
		
		val onSuccess: (Boolean) -> Unit = { sent ->
			val error = if (sent) null else R.string.no_email_app
			mutableState.update { it.copy(error = error) }
		}
		val onFailure: (Throwable) -> Unit = { failure ->
			logException(failure)
			mutableState.update { it.copy(error = R.string.logs_send_failed) }
		}

		runCatching { sendDraftEmail(context, current.logs) }
			.onSuccess(onSuccess)
			.onFailure(onFailure)
	}

	fun copy(context: Context) {
		val current = mutableState.value
		if (current.status != Status.Ready) return

		val onSuccess: (Unit) -> Unit = {
			// Android 13+ shows its own confirmation when something is copied.
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
				Toast.makeText(context, R.string.logs_copied, Toast.LENGTH_SHORT).show()
			}
			mutableState.update { it.copy(error = null) }
		}
		val onFailure: (Throwable) -> Unit = { failure ->
			logException(failure)
			mutableState.update { it.copy(error = R.string.logs_copy_failed) }
		}

		runCatching {
			val clipboard = context.getSystemService(ClipboardManager::class.java)
			clipboard.setPrimaryClip(ClipData.newPlainText("Hijri Widget logs", current.logs))
		}.onSuccess(onSuccess)
			.onFailure(onFailure)
	}

	/** Without READ_LOGS, Android only exposes this app's UID. */
	private fun read(): String {
		val process = run("logcat", "-d", "-v", "threadtime", "-t", "2000")
		return try {
			val output = process.inputStream.bufferedReader().use { it.readText() }
			check(process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0) {
				"Could not read app logs"
			}
			output
				.trimEnd()
				.lines()
				.asReversed()
				.joinToString("\n")
		} finally {
			process.destroy()
		}
	}

	/** logd limits clearing to the calling app UID without privileged log credentials. */
	private fun clearLogcat() {
		val process = run("logcat", "-c")
		try {
			check(process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0) {
				"Could not clear app logs"
			}
		} finally {
			process.destroy()
		}
	}

	/** Only offer email clients; opening a draft never sends it automatically. */
	private fun sendDraftEmail(
		context: Context,
		logs: String,
	): Boolean {
		val emailIntent = Intent(Intent.ACTION_SENDTO, android.net.Uri.parse("mailto:amr@amrbashir.me"))
		val emailActivities =
			context.packageManager
				.queryIntentActivities(emailIntent, 0)
				.map {
					ComponentName(it.activityInfo.packageName, it.activityInfo.name)
				}.distinct()

		if (emailActivities.isEmpty()) return false

		val uri = createAttachment(context, logs)

		val intents =
			emailActivities.map { activity ->
				createEmailIntent(activity, uri)
			}

		val chooser = Intent.createChooser(intents.first(), context.getString(R.string.send_logs))
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.drop(1).toTypedArray())

		context.startActivity(chooser)

		return true
	}

	private fun createAttachment(
		context: Context,
		logs: String,
	): Uri {
		val directory = File(context.cacheDir, "shared-logs").apply { mkdirs() }
		val expirationTime = System.currentTimeMillis() - 86_400_000

		// Keep recent attachments valid if the user returns to a draft.
		directory
			.listFiles()
			?.filter {
				it.lastModified() < expirationTime
			}?.forEach { it.delete() }
		val file =
			File(directory, "hijri-widget-logs-${System.currentTimeMillis()}.txt").apply {
				writeText(logs)
			}
		return FileProvider.getUriForFile(context, "${context.packageName}.logs", file)
	}

	private fun createEmailIntent(
		activity: ComponentName,
		uri: Uri,
	): Intent =
		Intent(Intent.ACTION_SEND).apply {
			component = activity
			type = "text/plain"
			putExtra(Intent.EXTRA_EMAIL, arrayOf("amr@amrbashir.me"))
			putExtra(Intent.EXTRA_SUBJECT, "Hijri Widget logs")
			putExtra(Intent.EXTRA_STREAM, uri)
			clipData = ClipData.newRawUri("App logs", uri)
			addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		}

	private fun run(vararg arguments: String): Process =
		ProcessBuilder(*arguments).redirectErrorStream(true).start()
}
