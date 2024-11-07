package micro.repl.ma7moud3ly.screens.explorer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val TAG = "FilesPicker"

// accepted MIME types by files picker
private val acceptedFiles = arrayOf(
    "text/x-python",
    "text/plain",
    "text/json",
    "application/zip",
    "application/octet-stream",
)

/**
 * Creates and remembers a [FilePickerResult] instance.
 *
 * This composable function provides a way to pick a file using the system file picker
 * and receive the result as a [FilePickerResult] object. The file picker is configured
 * to accept script types types
 *
 * When the user selects a file, the [FilePickerResult.pickFile] callback is invoked
 * with the file name and its content as a byte array.
 *
 * @return A [FilePickerResult] instance that can be used to pick a file.
 */
@Composable
fun rememberFilesPickerResult(): FilePickerResult {
    val context = LocalContext.current // Get the current context
    var onResult: ((String, ByteArray) -> Unit)? = null // Callback to handle the file picker result

    // Create a launcher for the file picker activity
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = object : ActivityResultContracts.GetContent() {
            override fun createIntent(context: Context, input: String): Intent {
                // Create an intent to launch the file picker
                return super.createIntent(context, input).putExtra(
                    Intent.EXTRA_MIME_TYPES, // Specify the accepted MIME types
                    acceptedFiles
                )
            }
        },
    ) { uri: Uri? ->
        // Handle the file picker result
        uri?.let {
            // If a URI is returned, query the content resolver for the file name
            context.contentResolver.query(
                uri, null, null,
                null, null
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                cursor.moveToFirst()
                cursor.getString(nameIndex) // Get the file name
            }?.let { fileName ->
                Log.v(TAG, "fileName - $fileName") // Log the file name
                // Open an input stream to read the file content
                context.contentResolver.openInputStream(uri)?.use {
                    val fileBytes: ByteArray =
                        it.readBytes() // Read the file content as a byte array
                    onResult?.invoke(
                        fileName,
                        fileBytes
                    ) // Invoke the callback with the file name and content
                }
            }
        }
    }

    // Remember the FilePickerResult instance
    return remember {
        FilePickerResult(
            pickFile = { callback ->
                onResult = callback // Store the callback
                filePickerLauncher.launch("*/*") // Launch the file picker activity
            }
        )
    }
}


@Stable
class FilePickerResult(
    val pickFile: (callback: (String, ByteArray) -> Unit) -> Unit
)