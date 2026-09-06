
package com.ovia

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ovia.ui.theme.OviaTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var speechRecognizer: SpeechRecognizer? = null

    private var textToSpeech: TextToSpeech? = null

    private var recognizedText by mutableStateOf("")

    private var assistantResponse by mutableStateOf("")

    private var isListening by mutableStateOf(false)

    private val microphonePermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                startSpeechRecognition()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Text-to-Speech
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech?.language = Locale.getDefault()
            }
        }

        // Initialize Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer?.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {
                    isListening = true
                }

                override fun onBeginningOfSpeech() {
                    isListening = true
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    isListening = false
                }

                override fun onError(error: Int) {
                    isListening = false

                    recognizedText = when (error) {
                        SpeechRecognizer.ERROR_AUDIO ->
                            "Error: microphone/audio problem"

                        SpeechRecognizer.ERROR_CLIENT ->
                            "Error: client problem"

                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                            "Error: microphone permission denied"

                        SpeechRecognizer.ERROR_NETWORK ->
                            "Error: network problem"

                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                            "Error: network timeout"

                        SpeechRecognizer.ERROR_NO_MATCH ->
                            "Error: no speech detected"

                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY ->
                            "Error: recognizer is busy"

                        SpeechRecognizer.ERROR_SERVER ->
                            "Error: speech server problem"

                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                            "Error: speech timeout"

                        else ->
                            "Error code: $error"
                    }
                }

                override fun onResults(results: Bundle?) {

                    isListening = false

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    if (!matches.isNullOrEmpty()) {
                        recognizedText = matches[0]
                    }
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {}

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }
        )

        setContent {
            OviaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    OviaHomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        recognizedText = recognizedText,
                        assistantResponse = assistantResponse,
                        isListening = isListening,

                        onStartListening = {
                            requestMicrophonePermission()
                        },

                        onTestResponse = {
                            assistantResponse =
                                "Hello! I am Ovia, your AI voice assistant."

                            speak(assistantResponse)
                        }
                    )
                }
            }
        }
    }

    private fun requestMicrophonePermission() {

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechRecognition()
        } else {
            microphonePermissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    private fun startSpeechRecognition() {

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            )
        }

        speechRecognizer?.startListening(intent)
    }

    // Android Text-to-Speech
    private fun speak(text: String) {

        textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "ovia_response"
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        speechRecognizer?.destroy()
        speechRecognizer = null

        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}

@Composable
fun OviaHomeScreen(
    modifier: Modifier = Modifier,
    recognizedText: String,
    assistantResponse: String,
    isListening: Boolean,
    onStartListening: () -> Unit,
    onTestResponse: () -> Unit
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Ovia",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = "Your AI Voice Assistant",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Button(
            onClick = onStartListening
        ) {
            Text(
                if (isListening)
                    "🎙 Listening..."
                else
                    "🎙 Start Listening"
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onTestResponse
        ) {
            Text("🔊 Test AI Response")
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (recognizedText.isNotEmpty()) {

            Text(
                text = "You said:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = recognizedText,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }

        if (assistantResponse.isNotEmpty()) {

            Text(
                text = "Ovia:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = assistantResponse,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
