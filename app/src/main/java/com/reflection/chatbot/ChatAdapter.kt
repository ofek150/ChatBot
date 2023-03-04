package com.reflection.chatbot

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reflection.chatbot.databinding.MessageItemBinding

class ChatAdapter(
    private val messages: MutableList<Message>
    ) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private lateinit var rv: RecyclerView

    class ChatViewHolder(val binding:MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding =
            MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        rv = recyclerView
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val curMessage = messages[position]
        val role : String  = if(curMessage.isSentByUser) {
            "User"
        } else {
            "Assistant"
        }
        val tokenUsageText: String = when (curMessage.tokensUsage) {
            -10 -> "N/A"
            0 -> "N/A"
            else -> curMessage.tokensUsage.toString()
        }

        holder.binding.roleText.text = role
        holder.binding.messageText.text = curMessage.content
        holder.binding.tokenUsage.text = "Total token usage: $tokenUsageText"
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: Message) {
        messages.add(message)
        Handler(Looper.getMainLooper()).post {
            notifyItemInserted(messages.size - 1)
            scrollToBottom()
        }
    }

    fun removeLastMessage() {
        val lastMessageIndex = messages.size - 1
        messages.removeAt(lastMessageIndex)
        Handler(Looper.getMainLooper()).post {
            notifyItemRemoved(lastMessageIndex)
            scrollToBottom()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun deleteChatHistory() {
        messages.clear()
        Handler(Looper.getMainLooper()).post {
            notifyDataSetChanged()
            scrollToBottom()
        }
    }

    fun getMessages() : MutableList<Message>
    {
        return messages
    }

    fun scrollToBottom()
    {
        rv.smoothScrollToPosition(itemCount - 1)
    }


}