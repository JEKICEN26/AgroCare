package com.zaky.agrocare.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaky.agrocare.data.ChatMessage
import com.zaky.agrocare.databinding.ItemChatMessageBinding

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        with(holder.binding) {
            if (message.isFromUser) {
                llUserMessage.visibility = View.VISIBLE
                llMitraMessage.visibility = View.GONE
                tvUserMessage.text = message.text
                tvUserTime.text = message.timestamp
            } else {
                llUserMessage.visibility = View.GONE
                llMitraMessage.visibility = View.VISIBLE
                tvMitraMessage.text = message.text
                tvMitraTime.text = message.timestamp
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
