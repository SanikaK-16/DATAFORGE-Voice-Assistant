package com.ovia

import android.os.Bundle
import android.service.voice.VoiceInteractionSession

class OviaVoiceInteractionSession(
    private val context: android.content.Context
) : VoiceInteractionSession(context) {

    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
    }

    override fun onHide() {
        super.onHide()
    }
}