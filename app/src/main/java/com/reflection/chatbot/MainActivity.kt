package com.reflection.chatbot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.Chat
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.*
import com.reflection.chatbot.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //private val OPENAI_API_KEY: String = System.getenv("OPENAI_API_KEY") ?: "NO_KEY"
    private val OPENAI_API_KEY = "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" // Change to env

    private val openAI = OpenAI(OPENAI_API_KEY)
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        println("API KEY: $OPENAI_API_KEY")
        chatAdapter = ChatAdapter(mutableListOf())
        binding.rvChatMessages.adapter = chatAdapter
        binding.rvChatMessages.layoutManager = LinearLayoutManager(this)

        binding.submitBtn.setOnClickListener{
            val prompt : String = binding.promptInput.text.toString()
            binding.promptInput.text.clear()

            if(prompt != "")
            {
                chatAdapter.addMessage(
                    Message(
                        isSentByUser = true,
                        content = prompt,
                        tokensUsage = -10
                    )
                )
                CoroutineScope(Dispatchers.IO).launch {
                    sendPrompt()
                }
                /*runBlocking {
                    launch {
                        sendPrompt()
                    }
                }*/
            }
        }

    }

    @OptIn(BetaOpenAI::class)
    suspend fun sendPrompt() {

        val chatMessages = mutableListOf<ChatMessage>()
        chatMessages.add(
            ChatMessage(
                role = ChatRole.System,
                content = "You are ChatGPT, a large language model trained by OpenAI. Answer as concisely as possible. Knowledge cutoff: none Current date: 04/03/2023"
            )
        )
        val messages = chatAdapter.getMessages()
        for (message in messages) {

            val chatMessage = ChatMessage(
                role = if (message.isSentByUser) ChatRole.User else ChatRole.Assistant,
                content = message.content
            )
            chatMessages.add(chatMessage)
        }

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = chatMessages
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)


        var responseContent = completion.choices[0].message?.content
        var tokenUsage = completion.usage?.totalTokens

        if(responseContent == null || tokenUsage == null) {
            responseContent = "Error in regenerating response"
            tokenUsage = 0
        }
        chatAdapter.addMessage(
            Message(
                isSentByUser = false,
                content = responseContent,
                tokensUsage = tokenUsage

            )
        )
    }
}