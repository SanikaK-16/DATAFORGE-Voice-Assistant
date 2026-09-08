from app.services.rime_tts import synthesize_speech


def main():
    text = "Hello! I am DataForge, your personal smartphone voice assistant."

    output_file = synthesize_speech(text)

    print("\nRime TTS successful!")
    print(f"Audio saved to: {output_file}")


if __name__ == "__main__":
    main()
