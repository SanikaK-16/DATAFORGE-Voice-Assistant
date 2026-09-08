from app.services.deepgram_stt import transcribe_audio
from app.services.gemini_ai import generate_response
from app.services.rime_tts import synthesize_speech


def run_voice_pipeline(
    audio_path: str,
    output_path: str = "tests/final_response.wav",
):
    """
    Runs the complete DataForge voice pipeline:

    Audio → Deepgram STT → Gemini → Rime TTS → Audio
    """

    print("\n[1/3] Transcribing audio with Deepgram...")
    transcript = transcribe_audio(audio_path)

    print(f"User said: {transcript}")

    print("\n[2/3] Generating AI response with Gemini...")
    ai_response = generate_response(transcript)

    print(f"DataForge says: {ai_response}")

    print("\n[3/3] Converting response to speech with Rime...")
    output_file = synthesize_speech(
        text=ai_response,
        output_path=output_path,
    )

    print("\nPipeline completed successfully!")

    return {
        "transcript": transcript,
        "response": ai_response,
        "audio_path": output_file,
    }
