package com.zaky.agrocare.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaky.agrocare.databinding.ItemChatListBinding

class ChatListAdapter(
    private val items: List<ChatListItem>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val item = items[adapterPosition]
                onClick(item.mitraName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvMitraName.text = item.mitraName
        holder.binding.tvLastMessage.text = item.lastMessage
        holder.binding.tvTime.text = item.time
    }

    override fun getItemCount(): Int = items.size
}
