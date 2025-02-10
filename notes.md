## How AI assistant change the way we work?

- AI assistant in software engineering

  - writting code
  - explaining code
  - modifying/simplifying/refactoring code
  - identifying bugs

- AI assistant in software testing
  - functional vs non-functional requirements (e.g. we are building calculator app)
    - functional: specific behaviors or functions; example - correct math results in a calculator
    - non-functional: overall qualities like performance, accessibility and scalability; example - process time (performance), resource usage
  - it's about expected vs actual; the discrepancy between these two is called a bug
  - test cases - test & verify predefined scenarios/steps
- using an AI assistant:

  - to create test cases
  - to generate test data
  - to create test scripts (e.g. from the description or curl command)

- ChatGPT (LLM):
  - understand and generate natural language text (e.g. English)
  - common tasks
- GitHub Copilot
  - focus on code
  - specialized capabilites for coding tasks

## Generative AI & Large Language Models

### Gen AI

1. Artificial intelligence (AI) - Mimic human intelligence by using models trained on data to recognize patterns and make decisions autonomously
2. Machine learning (ML) - focuses on algorithms that let AI systems learn from data and self-improve with training
3. Deep Learning (DL) - uses neural networks with many layers, allowing automatic feature learning and pattern prediction from raw data
4. Generative AI (Gen AI) - creates new content (like test, images) using deep neural networks trained on existing data; example it can learn from a huge set of animal images and create new images of those animals; the same stands for text

- Text: ChatGPT, Hugging Face
- Image: DALL-E, Stable Diffusion
- Audio: Google Wavenet, OpenAI Jukebox
- Video: Movie Gen, Invideo

### Large Language Models (LLM)

- AI trained based on huge amount of text data
- Understands and generates human-like language
- Handles natural language (e.g. English) processing tasks
- "Large" refers to extensive neural network size
- Famous LLM - ChatGPT
- LLM with specific task - GitHub Copilot
- LLMs are trained to predict the next word
- fine-tuned LLM is trained to follow instructions/solve specific task
- Prompt = input to LLM

### Prompt engineering

- design effective prompts for AI assistant input
- instruction to produce the expected output

- **write clear and specific instructions** (detailed, long enough, goal specific, add constraints, ask for specific style etc.)
- **use delimiters** to separate instructions and text parts
  - `###our-text###`
  - ---our-text---
  - `"""our-text"""`
  - `###our-text###`
  - ` ```our-text````
  - `<our-text>`
  - `<tag>our-text</tag>` -> e.g. `<task></task>`
- **ask for specific output format** - CSV, JSON, XML...
- **explain the steps** - for more complex tasks, guide the LLM step-by-step
- **role prompting** - assigning a specific role to the AI assistant (give some context as well)
- **conversation with LLM** - multiple prompts, can be effective; risk of the output bias if the conversation is long

### LLM limitations

1. knowledge cutoff - the LLM only knows about the data it was fed during the training; cutoff date is the last date when the model's training data is updated
   - events and information after this point are not included in its knowledge
   - ChatGPT paid version can browse the internet to find relevant information
2. Outdated information - something might changed between the cutoff point and present time
3. Hallucination - respond with factually incorrect answer, but seems correct
4. Proprietary information - they are not available (talking about company e.g.)

- Objectives when asking for response from LLM:

  1. up to date information
  2. factually accurate
  3. use properitary information as knowledge

- How to achieve these objectives?
  1. create & pre-train new LLM
  2. fine tune existing LLM with additional data
  3. use RAG - retrieval augmented generation

## Retrieval augmented generation

- goal: adding relevant information
- RAG concept:

  - adding information programmatically
  - LLM generative ability + real-time information retrieval
  - fetch releavant infomrmation then integrate to prompt
  - minimizing hallucination
  - reducing reliance to existing knowledge

- RAG flow:

  1. user gives prompt
  2. RAG searches for relevant information
  3. retrieved information are integrated with the original prompt (augmentation step)
  4. give augmented prompt to LLM
  5. LLM responds with contextual answer

- RAG advantage = relevant information added in the background produces more accurate response

### RAG system design

- technical key points for data source
  - location (private data or public information, one more sources)
  - format (PDF, text, structured or unstructured)
  - count & size - millions of small pieces of data or a few hundred items where each of them has size more than 10 MBs
- non-technical key points

  - data source prioritization - are all data sources equally relevant for some prompt/context

- Indexing pipeline (creates and updates the knowledge base):

1. Connect to information source through connector
2. Extract & parse relevant information
3. Split long information to smaller, more manageable chunks
4. Convert information chunks into suitable format (embeddings)
5. Store the information chunks to storage (embeddings are suitable for storing it in vector databases)
6. this cohort of data now represents the knowledge base which can be used by RAG process

- indexing pipeline should be run before the RAG process is created
- run it regularly to update the knowledge base
- data size & number of data sources can affect the indexing pipeline implementation

- RAG has 3 main components:

1. Retriever (retrieval)
   - scans knowledge base to find relevant information
   - accuracy is crucial for entire process
   - contributes heavily to overall RAG process latency since it can be computationally very expensive
2. Prompt management (augmentation)
   - combines retrieved information with the user's prompt
   - combined prompt quality impacts generated response quality
   - closely related to prompt engineering technique
3. LLM setup (generation)
   - Large Language Modelsgenerate the final response
   - RAG process may use multiple LLMs

- additional components

1. Caching
   - stores data in memory for faster access
   - semantic caching mechanism (similar keys get the same data)
   - optimizes system performance
2. Safeguards
   - pre-established rules for compliance
   - example: mark sensitive data
3. Security
   - security measures to counter emerging threats (prompt injections) and prevent data leaks

## Basic RAG

### Indexing pipeline

- Basic indexing pipeline:

  1. Read the document
  2. Transform (split/chunk) text
  3. Store

- Example:

  1. Read the HTML documents
  2. Parse the document, so tags are removed and only text is extracted
  3. Split the text and store as multiple records/files

#### Reading the document

- Two simplest Document readers implemented by Spring AI are:

1. `TextReader` - plain text
2. `JsonReader` - JSON

- PDF readers

1. `PagePdfDocumentReader` - read PDF & split based on page breaks
2. `ParagraphPdfDocumentReader` - read PDF & split based on table of contents. Cannot be used on PDF without the table of contents.

- Document reader for various types

1. `TikaDocumentReader` - read various types based on Apache Tika. Includes PDF, HTML, Microsoft Excel, Microsoft PowerPoint, CSV...

#### Transforming the document

- Why split?

  1. Context window of LLM - LLMs have a limited context window, meaning the number of tokens they can process. If the prompt contains more tokens than acceptable, excess text is skipped or rejected
  2. `Lost in the middle` problem - if the vital part of the prompt is burried in the middle of the text, the LLM can lost the point
  3. Ease of search - shorter texts are easier for search

- Data conversion (Embedding)
  - Computer core function is based on numbers
  - Data must be converted into a numerical form

#### Storage

- store the information (processed data)

### Basic RAG processor

- Use knowledge base in RAG process

1. Retrieve text file produced by basic indexing pipeline
2. Augment as custom context + user question
   - Use Spring AI class `PromptTemplate`
3. Generate the response using LLM provider

- String templates -> `Give me {count} foods that made from ingredient.`
- Spring AI advisors - called before and after interactions with the chat client
