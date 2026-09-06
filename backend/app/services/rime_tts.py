import os
from pathlib import Path

import httpx
from dotenv import load_dotenv


load_dotenv()

RIME_API_KEY = os.getenv("RIME_API_KEY")

if not RIME_API_KEY:
    raise ValueError("RIME_API_KEY is missing from the environment.")


def synthesize_speech(
    text: str,
    output_path: str = "tests/rime_output.wav",
) -> str:
    """
    Converts text to speech using Rime and saves the result as a WAV file.
    """

    url = "https://users.rime.ai/v1/rime-tts"

    headers = {
        "Accept": "audio/wav",
        "Authorization": f"Bearer {RIME_API_KEY}",
        "Content-Type": "application/json",
    }

    payload = {
        "text": text,
        "speaker": "celeste",
        "modelId": "arcana",
        "lang": "eng",
    }

    response = httpx.post(
        url,
        headers=headers,
        json=payload,
        timeout=60.0,
    )

    response.raise_for_status()

    path = Path(output_path)
    path.parent.mkdir(parents=True, exist_ok=True)

    path.write_bytes(response.content)

    return str(path)
