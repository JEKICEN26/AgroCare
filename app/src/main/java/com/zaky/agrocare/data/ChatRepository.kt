package com.zaky.agrocare.data

import com.zaky.agrocare.ui.chat.ChatListItem

object ChatRepository {
    // Menyimpan riwayat chat berdasarkan nama mitra
    private val chats = mutableMapOf<String, MutableList<ChatMessage>>()

    init {
        // Data awal (dummy) agar kotak masuk tidak kosong
        chats["Agro Sukses Berkarya"] = mutableListOf(
            ChatMessage("1", "Apakah stok pupuk organik ready?", true, "10:00"),
            ChatMessage("2", "Ready Kak, silakan langsung diorder ya 😊", false, "10:05")
        )
        
        chats["Toko Pertanian Sejahtera"] = mutableListOf(
            ChatMessage("3", "Terima kasih pesanannya sudah sampai dengan aman", true, "Kemarin"),
            ChatMessage("4", "Sama-sama Kak, ditunggu orderan selanjutnya 🙏", false, "Kemarin")
        )
    }

    fun getMessages(mitraName: String): MutableList<ChatMessage> {
        if (!chats.containsKey(mitraName)) {
            chats[mitraName] = mutableListOf()
        }
        return chats[mitraName]!!
    }

    fun addMessage(mitraName: String, message: ChatMessage) {
        if (!chats.containsKey(mitraName)) {
            chats[mitraName] = mutableListOf()
        }
        chats[mitraName]?.add(message)
    }

    fun getChatListSummary(): List<ChatListItem> {
        return chats.filter { it.value.isNotEmpty() }.map { (mitraName, messageList) ->
            val lastMsg = messageList.last()
            ChatListItem(
                mitraName = mitraName,
                lastMessage = lastMsg.text,
                time = lastMsg.timestamp
            )
        }.reversed() // Chat terbaru di atas (karena Map menyimpan urutan insertion)
    }
}
