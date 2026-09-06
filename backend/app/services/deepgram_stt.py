import os
from pathlib import Path

from deepgram import DeepgramClient
from dotenv import load_dotenv


load_dotenv()

api_key = os.getenv("DEEPGRAM_API_KEY")

if not api_key:
    raise ValueError("DEEPGRAM_API_KEY is missing from the environment.")

client = DeepgramClient(api_key=api_key)


def transcribe_audio(audio_path: str) -> str:
    """
    Transcribes an audio file using Deepgram.
    """

    path = Path(audio_path)

    if not path.exists():
        raise FileNotFoundError(f"Audio file not found: {audio_path}")

    with open(path, "rb") as audio_file:
        audio_data = audio_file.read()

        response = client.listen.v1.media.transcribe_file(
            request=audio_data,
            model="nova-3",
            smart_format=True,
        )

    return response.results.channels[0].alternatives[0].transcript
