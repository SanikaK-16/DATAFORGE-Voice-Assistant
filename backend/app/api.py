from fastapi import FastAPI
from pydantic import BaseModel
import os
from dotenv import load_dotenv
from google import genai

load_dotenv()

app = FastAPI()


class VoiceRequest(BaseModel):
    text: str


# Gemini client
client = genai.Client(api_key=os.getenv("GEMINI_API_KEY"))


@app.get("/")
async def home():
    return {"status": "DataForge Voice API is running"}


@app.post("/voice")
async def process_voice(request: VoiceRequest):

    try:
        response = client.models.generate_content(
            model="gemini-3.6-flash",
            contents=(
                "You are DataForge, an intelligent AI voice assistant. "
                "Help users discover and understand professional training "
                "programs, courses, skills, and learning opportunities. "
                "Speak naturally and conversationally. "
                "Keep responses concise because the response will be spoken aloud. "
                "Do not use markdown, bullet points, emojis, or unnecessary formatting. "
                "Be helpful, clear, professional, and friendly.\n\n"
                f"User: {request.text}"
            ),
        )

        return {"response": response.text}

    except Exception as e:
        print("Gemini error:", e)

        return {"response": "Sorry, I am having trouble processing that right now."}
