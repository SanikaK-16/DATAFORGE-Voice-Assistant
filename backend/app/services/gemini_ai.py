import os
from dotenv import load_dotenv
from google import genai


load_dotenv()

api_key = os.getenv("GEMINI_API_KEY")

if not api_key:
    raise ValueError("GEMINI_API_KEY is missing from the environment.")

client = genai.Client(api_key=api_key)


def generate_response(user_text: str) -> str:
    """
    Sends user text to Gemini and returns the assistant's response.
    """

    response = client.models.generate_content(
        model="gemini-3.6-flash",
        contents=user_text,
    )

    return response.text
