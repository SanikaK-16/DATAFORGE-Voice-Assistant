from app.services.deepgram_stt import transcribe_audio


def main():
    audio_path = "tests/sample_audio.wav"

    transcript = transcribe_audio(audio_path)

    print("\nDeepgram Transcript:\n")
    print(transcript)


if __name__ == "__main__":
    main()
