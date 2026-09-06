package com.ovia

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionService
import android.speech.SpeechRecognizer

class OviaRecognitionService : RecognitionService() {

    override fun onStartListening(
        recognizerIntent: Intent?,
        listener: Callback?
    ) {
        listener?.results(Bundle().apply {
            putStringArrayList(
                SpeechRecognizer.RESULTS_RECOGNITION,
                arrayListOf()
            )
        })
    }

    override fun onStopListening(listener: Callback?) {
        // Nothing to stop yet
    }

    override fun onCancel(listener: Callback?) {
        // Nothing to cancel yet
    }
}