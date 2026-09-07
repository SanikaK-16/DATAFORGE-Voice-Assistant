import os
from dotenv import load_dotenv
from google import genai
from google.genai import types


load_dotenv()

api_key = os.getenv("GEMINI_API_KEY")

if not api_key:
    raise ValueError("GEMINI_API_KEY is missing from the environment.")

client = genai.Client(api_key=api_key)


def generate_response(user_text: str) -> str:
    """
    Sends user text to Gemini and returns a short,
    natural response suitable for a voice assistant.
    """

    response = client.models.generate_content(
        model="gemini-3.6-flash",
        contents=user_text,
        config=types.GenerateContentConfig(
            system_instruction=(
                "You are DataForge, a helpful Android smartphone voice assistant. "
                "Respond naturally and conversationally because your responses will "
                "be spoken aloud using text-to-speech. Keep responses short, clear, "
                "and concise, usually one to three sentences. Do not use Markdown, "
                "headings, bullet points, or long explanations. Do not claim that "
                "you performed an action unless the system confirms it was completed. "
                "If a smartphone action is requested but not yet available, clearly "
                "say what you can do instead."
            )
        ),
    )

    return response.text
