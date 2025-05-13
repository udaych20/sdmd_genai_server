# SDMD_GENAI_SERVER

#LangChain4j REST API (Java + Ollama)

A lightweight REST API built using plain Java and LangChain4j that integrates with Ollama's LLaMA 3 model to answer natural language prompts. This API is exposed using Java's built-in `HttpServer`.

---

## 🧠 Features

- Uses `LangChain4j` with `Ollama` (LLaMA 3 model)
- No Spring Boot or external framework
- Simple `POST /ask` endpoint
- Returns plain text LLM-generated response

---

## 🧰 Prerequisites

- Java 17+
- Maven 3.x
- [Ollama](https://ollama.com) (running locally)

---

## 🚀 Project Setup in Eclipse

1. **Create a Maven Project**  
   File → New → Maven Project  
   ✅ Check: "Create a simple project (skip archetype selection)"

2. **Group and Artifact Details**
