# Rime Evidence

## 1. Hard Voice Engineering Claim

Ovia is a hands-free Android voice assistant designed for situations where continuously looking at or touching the phone is inconvenient.

The selected hard voice problem is **interruption-aware interaction**.

A user should not have to wait for a spoken response to finish before giving the next instruction. Ovia uses a LiveKit voice session configured with:

```python
allow_interruptions=True
```

This enables the active voice session to handle a new user turn while the assistant is producing a response.

The spoken response is generated using **Rime TTS** through the LiveKit Rime plugin.

---

## 2. Why This Is a Voice-Specific Problem

In a text interface, a user can type a new message while a previous response is displayed.

In a voice interface, the user and assistant share the same audio interaction channel. If the assistant continues speaking while the user is trying to speak, the interaction can become slow and unnatural.

Ovia therefore treats interruption handling as part of the core voice interaction rather than as a cosmetic UI feature.

The goal is to allow the user to naturally take the conversational turn without being forced to wait for the assistant to finish speaking.

---

## 3. Acceptance Test

### Scenario

1. Start an Ovia voice session.
2. Ask a question that produces a spoken response.
3. Begin speaking before the assistant has finished its response.
4. Give a new instruction or question.
5. Observe whether the new user turn is accepted and processed.
6. Continue the conversation from the new turn.

### Expected Result

The user should be able to interrupt the assistant's ongoing spoken response and provide a new instruction without having to wait for the previous response to finish.

The active LiveKit session is configured with:

```python
allow_interruptions=True
```

### Acceptance Criteria

| Criterion                                                | Expected Result |
| -------------------------------------------------------- | --------------- |
| Assistant begins a spoken response                       | PASS            |
| User speaks before the response finishes                 | Supported       |
| New user turn is accepted                                | PASS            |
| Conversation continues from the new turn                 | PASS            |
| User must wait for the complete response before speaking | No              |

---

## 4. Actual Demonstration Result

**Status: PASS**

The interruption scenario was successfully demonstrated in the Ovia voice interaction flow.

During the demonstration, the assistant began producing a spoken response and the user deliberately started speaking before the response had finished. The new user turn was accepted by the active voice session, allowing the conversation to continue without requiring the user to wait for the previous response to finish.

This behavior is enabled by the active LiveKit session configuration:

```python
allow_interruptions=True
```

### Observed Result

| Test                                      | Result |
| ----------------------------------------- | ------ |
| Assistant produces a spoken response      | PASS   |
| User interrupts before completion         | PASS   |
| New user turn is accepted                 | PASS   |
| Conversation continues after interruption | PASS   |

**Overall acceptance result: PASS**

No specific latency or interruption-success percentage is claimed because the current implementation does not include dedicated latency or interruption-rate instrumentation.

---

## 5. Active Voice Pipeline

The active real-time voice pipeline implemented in the repository is:

```text
Android / User Speech
        ↓
LiveKit Voice Session
        ↓
Deepgram Nova-3 STT
        ↓
Gemini 3.6 Flash
        ↓
Rime Coda TTS
        ↓
Rime Astra Voice
        ↓
Spoken Response
```

The active backend implementation is:

```text
backend/app/livekit_agent.py
```

---

## 6. Active Rime Configuration

Ovia uses the LiveKit Rime TTS plugin with the following configuration:

```python
tts=rime.TTS(
    model="coda",
    speaker="astra",
)
```

The active configuration does not set `use_websocket=True`. Therefore, the Rime plugin uses its HTTP transport.

### Configuration

| Parameter      | Active Value                        |
| -------------- | ----------------------------------- |
| Provider       | Rime                                |
| Model          | `coda`                              |
| Speaker        | `astra`                             |
| Language       | English (`eng`, plugin default)     |
| Endpoint       | `https://users.rime.ai/v1/rime-tts` |
| Transport      | HTTP                                |
| Audio format   | PCM                                 |
| Authentication | `RIME_API_KEY`                      |
| Integration    | LiveKit Rime TTS plugin             |

The Rime integration is part of the active real-time voice path rather than an unused dependency.

---

## 7. Why Rime Is Central

Rime is the active text-to-speech provider for the real-time voice agent.

The backend imports the Rime plugin:

```python
from livekit.plugins import deepgram, google, rime
```

and configures Rime as the TTS component:

```python
tts=rime.TTS(
    model="coda",
    speaker="astra",
)
```

Therefore, generated assistant responses in the active LiveKit voice pipeline are synthesized through Rime.

This makes Rime a core component of the demonstrated voice experience.

---

## 8. Voice Stack

| Component                           | Technology                     |
| ----------------------------------- | ------------------------------ |
| Voice session / real-time transport | LiveKit Agents                 |
| Speech-to-text                      | Deepgram Nova-3                |
| Language model                      | Gemini 3.6 Flash               |
| Text-to-speech                      | Rime Coda                      |
| Rime speaker                        | Astra                          |
| Android client                      | Kotlin / Jetpack Compose       |
| Assistant integration               | Android Voice Interaction APIs |
| Accessibility integration           | Android AccessibilityService   |

---

## 9. Active Interruption Configuration

The active LiveKit agent session is configured as:

```python
session = AgentSession(
    stt=deepgram.STT(
        model="nova-3",
        language="en",
    ),
    llm=google.LLM(
        model="gemini-3.6-flash",
    ),
    tts=rime.TTS(
        model="coda",
        speaker="astra",
    ),
    allow_interruptions=True,
)
```

The interruption behavior is therefore implemented directly in the active real-time agent configuration.

It is not only a UI-level claim.

---

## 10. Failure and Recovery Behavior

### Speech Recognition Failure

If speech recognition does not produce a usable transcript, the assistant cannot generate a meaningful response from that user turn.

### Upstream Service Failure

The voice interaction depends on external services including:

- LiveKit
- Deepgram
- Gemini
- Rime

If an upstream service is unavailable, the voice interaction may fail or terminate.

Ovia does not intentionally fabricate a response when the required upstream service is unavailable.

### Interruption

When a user begins speaking during an assistant response, the active session is configured to allow the new user turn to interrupt the ongoing interaction.

The observed interruption behavior is documented in the acceptance-test result in Section 4.

---

## 11. Reproducibility

### Backend Dependencies

From the repository root:

```bash
pip install -r backend/requirements.txt
```

### Environment Configuration

The repository provides:

```text
.env.example
```

Create a local `.env` file and provide valid credentials for the required services.

The required environment variables are:

```text
LIVEKIT_URL
LIVEKIT_API_KEY
LIVEKIT_API_SECRET
DEEPGRAM_API_KEY
GEMINI_API_KEY
RIME_API_KEY
```

Secrets must remain in the local `.env` file and must not be committed to the repository.

### Run the LiveKit Agent

From the repository root:

```bash
python backend/app/livekit_agent.py dev
```

Then connect the Android client to the configured LiveKit session.

### Reproduce the Voice Test

Repeat the acceptance scenario from Section 3:

1. Start the voice session.
2. Trigger a spoken assistant response.
3. Interrupt the response deliberately.
4. Speak a new instruction.
5. Verify that the new turn is accepted.
6. Continue the conversation.

---

## 12. Known Limitations

- End-to-end voice interaction requires network connectivity.
- LiveKit, Deepgram, Gemini, and Rime availability can affect the experience.
- Perceived response time depends on network conditions, device hardware, speech recognition, model generation, and TTS generation.
- Interruption behavior can depend on audio conditions, timing, and upstream service behavior.
- The current evidence focuses on interruption-aware interaction rather than claiming universal interruption accuracy.
- No latency or interruption-success percentage is claimed because dedicated performance instrumentation is not currently part of the implementation.
- The active Rime integration uses HTTP rather than Rime WebSocket streaming.

---

## 13. Evidence Integrity

This document distinguishes between:

1. **Implemented configuration** — verified from the repository source code.
2. **Expected behavior** — defined by the acceptance test.
3. **Observed demonstration result** — based on the demonstrated interruption scenario.

No fabricated latency, throughput, accuracy, or success-rate numbers are reported.

The PASS result refers specifically to the demonstrated acceptance scenario and should not be interpreted as a claim of universal interruption accuracy under all network, device, or acoustic conditions.

---

## 14. Source of Truth

For implementation verification, the primary source is:

```text
backend/app/livekit_agent.py
```

This file contains the active LiveKit voice session, Deepgram STT, Gemini LLM, Rime TTS configuration, and interruption setting.

The Rime-specific integration is configured through:

```python
rime.TTS(
    model="coda",
    speaker="astra",
)
```

with:

```python
allow_interruptions=True
```

These settings form the basis of the hard voice engineering claim documented in this file.
