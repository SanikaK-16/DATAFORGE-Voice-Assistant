from app.pipeline.voice_pipeline import run_voice_pipeline


def main():
    result = run_voice_pipeline(
        audio_path="tests/sample_audio.wav",
        output_path="tests/final_response.wav",
    )

    print("\n--- FINAL RESULT ---")
    print(f"Transcript: {result['transcript']}")
    print(f"AI Response: {result['response']}")
    print(f"Audio File: {result['audio_path']}")


if __name__ == "__main__":
    main()
