from dotenv import load_dotenv

from livekit.agents import (
    Agent,
    AgentSession,
    AgentServer,
    JobContext,
    cli,
)

from livekit.plugins import deepgram, google, rime


load_dotenv()


class DataForgeAssistant(Agent):
    def __init__(self):
        super().__init__(
            instructions=(
                "You are DataForge, an intelligent AI voice assistant. "
                "Your job is to help users discover and understand relevant professional "
                "training programs, courses, skills, and learning opportunities. "
                "Speak naturally and conversationally. Keep responses concise because "
                "your responses will be spoken aloud. "
                "Do not use markdown, bullet points, emojis, or unnecessary formatting. "
                "If you do not have enough information to answer accurately, ask a "
                "clarifying question instead of guessing. "
                "Be helpful, clear, professional, and friendly."
            )
        )


server = AgentServer()


@server.rtc_session(agent_name="dataforge-voice")
async def entrypoint(ctx: JobContext):
    session = AgentSession(
        stt=deepgram.STT(
            model="nova-3",
            language="en",
        ),
        llm=google.LLM(
            model="gemini-3.6-flash",
        ),
        tts=rime.TTS(
            model="coda",
            speaker="astra",
        ),
        allow_interruptions=True,
    )

    await session.start(
        room=ctx.room,
        agent=DataForgeAssistant(),
    )

    await session.generate_reply(
        instructions="Greet the user briefly and ask how you can help."
    )


if __name__ == "__main__":
    cli.run_app(server)
