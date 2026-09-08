from app.services.gemini_ai import generate_response


def main():
    user_text = "Hello! Introduce yourself as DataForge, a smartphone voice assistant."

    response = generate_response(user_text)

    print("\nGemini Response:\n")
    print(response)


if __name__ == "__main__":
    main()
