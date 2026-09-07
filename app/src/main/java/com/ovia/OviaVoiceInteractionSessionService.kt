package com.ovia

import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService

class OviaVoiceInteractionSessionService : VoiceInteractionSessionService() {

    override fun onNewSession(args: android.os.Bundle?): VoiceInteractionSession {
        return OviaVoiceInteractionSession(this)
    }
}