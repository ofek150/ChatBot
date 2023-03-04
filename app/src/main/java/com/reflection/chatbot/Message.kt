package com.reflection.chatbot

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole

data class Message @OptIn(BetaOpenAI::class) constructor(
    val isSentByUser: Boolean,
    val content: String,
    val tokensUsage: Int
)