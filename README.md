# Ovia — Voice-First Android Assistant

> A hands-free AI voice assistant for Android, combining real-time speech recognition, generative AI, Rime text-to-speech, LiveKit, and Android accessibility capabilities.

---

## 1. Overview

Ovia is a voice-first Android assistant designed for hands-free interaction with digital tasks.

The project combines an Android client with a real-time voice backend. Users can communicate with the assistant through speech, receive AI-generated responses, and hear those responses spoken naturally.

The system is designed around a core voice interaction challenge: **users should not have to wait for an assistant to finish speaking before they can continue the conversation**.

Ovia therefore supports interruption-aware interaction through its real-time LiveKit voice session.

---

## 2. Problem Statement

Traditional mobile interfaces require users to repeatedly look at the screen, locate controls, and interact through touch.

This becomes inconvenient when users are:

- performing tasks while their hands are occupied,
- working away from a desktop,
- moving between tasks,
- relying on screen-light interaction,
- or interacting with a device where continuous touch interaction is undesirable.

Voice is not simply an alternative input method in these situations. It can materially reduce the amount of visual and physical interaction required.

Ovia addresses this problem through a voice-first Android interface backed by a real-time AI voice pipeline.

---

## 3. Target User

Ovia targets users who benefit from hands-free or reduced-screen interaction, including:

- users performing tasks while their hands are occupied,
- users who prefer conversational interaction,
- field and mobile users,
- users who need quick spoken responses,
- and users who benefit from accessibility-oriented interaction.

---

## 4. Why Voice Is Necessary

Removing voice would materially change the intended interaction model.

The assistant is designed around spoken input and spoken output rather than requiring users to continuously type requests and read responses.

The interaction is:

```text
User speaks
    ↓
Speech recognition
    ↓
AI reasoning
    ↓
Speech synthesis
    ↓
User hears response
```

This allows the user to remain focused on the task rather than continuously interacting with a screen.

---

## 5. Key Features

### Voice-first interaction

Users communicate with Ovia through speech and receive spoken responses.

### Real-time voice pipeline

The backend uses LiveKit to coordinate the real-time voice session.

### Speech recognition

Deepgram Nova-3 is used for speech-to-text processing.

### AI response generation

Google Gemini is used to generate conversational responses.

### Rime text-to-speech

Rime is the active text-to-speech provider for the voice experience.

### Interruption-aware interaction

The LiveKit session enables interruptions, allowing users to begin a new turn while the assistant is speaking.

### Android assistant integration

The Android application implements the required voice interaction service components and can be configured as a digital assistant on supported Android environments.

### Accessibility integration

The Android application includes an AccessibilityService for interaction with supported Android UI elements and accessibility actions.

---

## 6. Hard Voice Engineering Problem

### Interruption-aware voice interaction

A major challenge in conversational voice interfaces is that users do not always wait until an assistant has finished speaking.

Natural conversation contains interruptions, corrections, follow-up questions, and changes of intent.

Ovia addresses this through an interruption-aware real-time session.

The active LiveKit session is configured with:

```python
allow_interruptions=True
```

This makes interruption handling a first-class part of the voice interaction rather than treating every response as an uninterrupted request-response transaction.

The acceptance procedure is documented in [`RIME_EVIDENCE.md`](RIME_EVIDENCE.md).

---

## 7. System Architecture

```text
┌───────────────────────────────┐
│       Android Ovia App        │
│                               │
│  Voice UI                     │
│  Speech interaction           │
│  VoiceInteractionService      │
│  AccessibilityService        │
└───────────────┬───────────────┘
                │
                │ Real-time voice session
                ▼
┌───────────────────────────────┐
│          LiveKit              │
│     Real-time communication   │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│       Python Voice Agent      │
│                               │
│  Deepgram STT                 │
│        ↓                      │
│  Gemini LLM                   │
│        ↓                      │
│  Rime TTS                     │
└───────────────┬───────────────┘
                │
                │ Spoken response
                ▼
          Android User
```

---

## 8. End-to-End Voice Pipeline

The active real-time pipeline is:

```text
User Speech
     ↓
LiveKit
     ↓
Deepgram Nova-3
     ↓
Google Gemini 3.6 Flash
     ↓
Rime Coda / Astra
     ↓
Spoken Audio
     ↓
User
```

The repository also contains a direct pipeline implementation that follows:

```text
Audio
  ↓
Deepgram STT
  ↓
Gemini
  ↓
Rime TTS
  ↓
Audio file
```

The active real-time demo path is the LiveKit agent.

---

## 9. Rime Integration

Rime is central to the spoken response experience.

### Active configuration

| Parameter | Configuration |
|---|---|
| Provider | Rime |
| Model | `coda` |
| Speaker | `astra` |
| Language | English |
| Endpoint | `https://users.rime.ai/v1/rime-tts` |
| Audio format | PCM |
| Transport | HTTP |

The Rime integration is accessed through the LiveKit Rime TTS plugin.

The project intentionally documents the exact active configuration so that the voice experience can be reproduced and evaluated.

---

## 10. Technology Stack

### Android

- Kotlin
- Jetpack Compose
- Android Voice Interaction APIs
- Android Speech APIs
- Android Accessibility APIs

### Backend

- Python
- FastAPI
- LiveKit Agents
- LiveKit real-time communication

### AI / Voice Services

- Deepgram Nova-3 — Speech-to-Text
- Google Gemini 3.6 Flash — Large Language Model
- Rime Coda / Astra — Text-to-Speech
- LiveKit — Real-time voice transport and agent session management

---

## 11. Repository Structure

```text
DATAFORGE-Voice-Assistant/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/com/ovia/
│           │   ├── MainActivity.kt
│           │   ├── OviaVoiceInteractionService.kt
│           │   ├── OviaVoiceInteractionSessionService.kt
│           │   ├── OviaVoiceInteractionSession.kt
│           │   ├── OviaRecognitionService.kt
│           │   └── accessibility/
│           │       └── OviaAccessibilityService.kt
│           │
│           └── res/
│
├── backend/
│   ├── app/
│   │   ├── api.py
│   │   ├── livekit_agent.py
│   │   ├── pipeline/
│   │   └── services/
│   │
│   ├── tests/
│   └── requirements.txt
│
├── .env.example
├── RIME_EVIDENCE.md
├── README.md
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

---

## 12. Android Setup

### Requirements

- Android Studio
- Android SDK
- Android SDK Platform compatible with the project
- JDK supported by the Android Gradle configuration
- Android device or emulator

### Clone the repository

```bash
git clone https://github.com/SanikaK-16/DATAFORGE-Voice-Assistant.git
cd DATAFORGE-Voice-Assistant
```

### Open in Android Studio

Open the repository root in Android Studio and allow Gradle to synchronize the project.

### Build

Windows:

```powershell
.\gradlew.bat assembleDebug
```

Linux/macOS:

```bash
./gradlew assembleDebug
```

The generated debug APK is produced under:

```text
app/build/outputs/apk/debug/
```

---

## 13. Android Permissions

The application requires microphone access for voice interaction.

The user must grant the microphone permission when requested.

Accessibility functionality requires the user to explicitly enable the Ovia AccessibilityService through Android settings.

Voice assistant functionality may also require selecting Ovia as the device's supported digital assistant on Android versions and device configurations that expose this option.

---

## 14. Backend Setup

Create a Python virtual environment:

```bash
python -m venv .venv
```

Windows:

```powershell
.venv\Scripts\activate
```

Install dependencies:

```bash
pip install -r backend/requirements.txt
```

---

## 15. Environment Configuration

Create a local environment file using the provided template:

```bash
copy .env.example .env
```

or create `.env` manually.

Required credentials include:

```text
LIVEKIT_URL
LIVEKIT_API_KEY
LIVEKIT_API_SECRET
DEEPGRAM_API_KEY
GEMINI_API_KEY
RIME_API_KEY
```

**Never commit real API keys or secrets to Git.**

The committed `.env.example` contains placeholders only.

---

## 16. Running the Voice Agent

From the repository root:

```bash
python backend/app/livekit_agent.py dev
```

The agent connects to the configured LiveKit environment and exposes the real-time voice session.

A compatible LiveKit client must be connected to interact with the agent.

---

## 17. Failure Behavior

Ovia depends on several network-based services.

Possible failure conditions include:

- unavailable network connection,
- unavailable LiveKit session,
- speech recognition failure,
- AI service failure,
- text-to-speech service failure,
- microphone permission denial,
- unavailable or disabled accessibility service.

The system should not fabricate a successful result when an upstream service fails.

For voice recognition failures, the assistant may be unable to produce a response.

For service or network failures, the interaction may terminate or fail to produce spoken output.

---

## 18. Known Limitations

- The voice pipeline requires network connectivity.
- Third-party service availability can affect response time and reliability.
- Android assistant and accessibility capabilities vary between Android versions and device manufacturers.
- Some OEM Android interfaces may restrict or expose assistant functionality differently.
- Speech recognition performance can vary with background noise, microphone quality, and accent.
- Interruption behavior depends on the real-time session and network conditions.
- The current evidence focuses on interruption-aware interaction rather than claiming universal interruption success.

---

## 19. Evidence and Reproducibility

The repository includes:

```text
RIME_EVIDENCE.md
```

This document contains:

- the hard voice engineering claim,
- acceptance criteria,
- reproduction procedure,
- Rime configuration,
- observed result,
- failure behavior,
- and known limitations.

The purpose is to allow judges to understand and reproduce the main voice-engineering claim without relying solely on the recorded demo.

---

## 20. Demo

The submitted demo demonstrates:

1. The target user and problem.
2. The normal end-to-end voice interaction.
3. The selected hard voice problem.
4. A deliberate interruption/stress case.
5. The observed result.
6. The active Rime configuration.

The demo is limited to the required submission duration.

---

## 21. Third-Party Services

| Service | Purpose |
|---|---|
| LiveKit | Real-time voice communication and agent sessions |
| Deepgram | Speech-to-text |
| Google Gemini | AI response generation |
| Rime | Text-to-speech |
| Android Accessibility APIs | Device UI interaction capabilities |

Use of these services may require separate accounts, API keys, and applicable provider terms.

---

## 22. Configuration and Security

No production credentials should be committed to the repository.

Use:

```text
.env.example
```

as the configuration template and create a local `.env` file containing actual credentials.

Before submission, verify that:

- no API key is present in source files,
- no `.env` file is committed,
- no access tokens are committed,
- no private credentials are included in documentation,
- and only placeholder values are present in `.env.example`.

---

## 23. Testing

### Android build

```bash
./gradlew assembleDebug
```

Windows:

```powershell
.\gradlew.bat assembleDebug
```

### Voice acceptance test

Follow the interruption test documented in:

```text
RIME_EVIDENCE.md
```

The test consists of starting a voice response, deliberately interrupting it, and verifying that the subsequent user turn is accepted by the active voice session.

---

## 24. Future Improvements

Potential future improvements include:

- stronger interruption evaluation metrics,
- multilingual voice routing,
- improved latency measurement,
- more robust offline/error recovery,
- richer Android action execution,
- improved accessibility coverage,
- persistent conversational context,
- automated voice evaluation,
- and production deployment hardening.

---

## 25. Project Status

The Android application builds successfully using the project's Gradle configuration.

The repository contains the Android client, voice interaction services, accessibility integration, backend voice agent, and Rime-based speech synthesis integration required for the demonstrated prototype.

---

## 26. License

This project is provided for hackathon/prototype purposes.

Third-party services and libraries remain subject to their respective licenses and terms.