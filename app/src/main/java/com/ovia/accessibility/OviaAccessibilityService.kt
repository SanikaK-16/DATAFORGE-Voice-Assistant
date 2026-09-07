package com.ovia.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class OviaAccessibilityService : AccessibilityService() {

    companion object {
        var instance: OviaAccessibilityService? = null
    }

    private var tapTestDone = false
    private var typeTestDone = false

    override fun onServiceConnected() {
        super.onServiceConnected()

        instance = this

        Log.d(
            "OviaAccessibility",
            "Accessibility Service Connected"
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        val packageName = event?.packageName?.toString()

        if (packageName != null) {
            Log.d(
                "OviaAccessibility",
                "Active app: $packageName"
            )
        }

        val rootNode = rootInActiveWindow

        if (rootNode != null) {

            fun scanNode(node: AccessibilityNodeInfo) {

                val text = node.text?.toString()
                val description = node.contentDescription?.toString()

                if (!text.isNullOrBlank()) {
                    Log.d(
                        "OviaAccessibility",
                        "UI Text: $text"
                    )
                }

                if (!description.isNullOrBlank()) {
                    Log.d(
                        "OviaAccessibility",
                        "UI Description: $description"
                    )
                }

                for (i in 0 until node.childCount) {

                    val child = node.getChild(i)

                    if (child != null) {
                        scanNode(child)
                        child.recycle()
                    }
                }
            }

            scanNode(rootNode)

            // Test tap only once
            if (!tapTestDone && packageName == "com.android.chrome") {

                if (findAndTapText("George Krugers")) {

                    tapTestDone = true

                    Log.d(
                        "OviaAccessibility",
                        "Tap successful: George Krugers"
                    )
                }
            }

            // Test typing only once
            if (!typeTestDone && packageName == "com.android.chrome") {

                val editableNode = rootNode.findFocus(
                    AccessibilityNodeInfo.FOCUS_INPUT
                )

                if (editableNode != null && editableNode.isEditable) {

                    if (typeText(editableNode, "Hello Ovia")) {

                        typeTestDone = true

                        Log.d(
                            "OviaAccessibility",
                            "Type successful: Hello Ovia"
                        )
                    }

                    editableNode.recycle()
                }
            }

            rootNode.recycle()
        }
    }

    // Basic tap action
    fun tapNode(node: AccessibilityNodeInfo?): Boolean {

        if (node == null) return false

        return node.performAction(
            AccessibilityNodeInfo.ACTION_CLICK
        )
    }

    // Find a UI element by text and tap it
    fun findAndTapText(text: String): Boolean {

        val rootNode = rootInActiveWindow ?: return false

        val nodes =
            rootNode.findAccessibilityNodeInfosByText(text)

        for (node in nodes) {

            if (node.performAction(
                    AccessibilityNodeInfo.ACTION_CLICK
                )
            ) {

                Log.d(
                    "OviaAccessibility",
                    "Tap successful on node: $text"
                )

                rootNode.recycle()
                return true
            }

            var parent = node.parent

            while (parent != null) {

                if (parent.performAction(
                        AccessibilityNodeInfo.ACTION_CLICK
                    )
                ) {

                    Log.d(
                        "OviaAccessibility",
                        "Tap successful on parent: $text"
                    )

                    rootNode.recycle()
                    return true
                }

                parent = parent.parent
            }
        }

        rootNode.recycle()

        Log.d(
            "OviaAccessibility",
            "Tap failed: $text"
        )

        return false
    }

    // Type text into an editable UI element
    fun typeText(
        node: AccessibilityNodeInfo?,
        text: String
    ): Boolean {

        if (node == null) return false

        if (!node.isEditable) return false

        val arguments = Bundle().apply {

            putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text
            )
        }

        return node.performAction(
            AccessibilityNodeInfo.ACTION_SET_TEXT,
            arguments
        )
    }

    // Swipe action
    fun swipe(
        startX: Float,
        startY: Float,
        endX: Float,
        endY: Float,
        duration: Long = 500
    ): Boolean {

        val path = Path()

        path.moveTo(startX, startY)
        path.lineTo(endX, endY)

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    duration
                )
            )
            .build()

        return dispatchGesture(
            gesture,
            null,
            null
        )
    }

    // Back action
    fun goBack(): Boolean {

        return performGlobalAction(
            GLOBAL_ACTION_BACK
        )
    }

    // Controlled demo workflow
    fun runDemoWorkflow(): Boolean {

        Log.d(
            "OviaAccessibility",
            "Demo workflow started"
        )

        val swipeSuccess = swipe(
            startX = 500f,
            startY = 1400f,
            endX = 500f,
            endY = 500f
        )

        if (!swipeSuccess) {

            Log.d(
                "OviaAccessibility",
                "Demo workflow: swipe failed"
            )

            return false
        }

        Log.d(
            "OviaAccessibility",
            "Demo workflow: swipe completed"
        )

        return true
    }

    override fun onInterrupt() {

        Log.d(
            "OviaAccessibility",
            "Accessibility service interrupted"
        )
    }
}