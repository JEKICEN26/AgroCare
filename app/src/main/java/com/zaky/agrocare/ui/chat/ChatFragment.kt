package com.zaky.agrocare.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaky.agrocare.data.ChatMessage
import com.zaky.agrocare.databinding.FragmentChatBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val args: ChatFragmentArgs by navArgs()
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvChatTitle.text = args.mitraName
        
        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        // Ambil data dari repository
        messages.clear()
        messages.addAll(com.zaky.agrocare.data.ChatRepository.getMessages(args.mitraName))

        chatAdapter = ChatAdapter(messages)
        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val message = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = text,
                    isFromUser = true,
                    timestamp = time
                )
                
                // Simpan ke repository
                com.zaky.agrocare.data.ChatRepository.addMessage(args.mitraName, message)
                
                // Update UI
                chatAdapter.addMessage(message)
                binding.etMessage.text.clear()
                binding.rvChat.scrollToPosition(messages.size - 1)
                
                // Simulate reply
                simulateReply(text)
            }
        }
    }

    private fun simulateReply(userMessage: String) {
        binding.rvChat.postDelayed({
            if (_binding != null) {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                
                val lowerMsg = userMessage.lowercase(Locale.getDefault())
                val replyText = when {
                    lowerMsg.contains("halo") || lowerMsg.contains("hai") || lowerMsg.contains("hi") || lowerMsg.contains("min") || lowerMsg.contains("permisi") -> 
                        "Halo Kak! Selamat datang di toko kami. Ada yang bisa dibantu hari ini? 😊"
                    lowerMsg.contains("stok") || lowerMsg.contains("ready") || lowerMsg.contains("ada") -> 
                        "Untuk produk tersebut stoknya masih ready dan aman Kak. Silakan bisa langsung diorder ya!"
                    lowerMsg.contains("kirim") || lowerMsg.contains("kapan") || lowerMsg.contains("ongkir") || lowerMsg.contains("proses") -> 
                        "Pesanan yang masuk sebelum jam 15:00 akan kami proses dan kirim di hari yang sama ya Kak. 🚚"
                    lowerMsg.contains("terima kasih") || lowerMsg.contains("makasih") || lowerMsg.contains("ok") || lowerMsg.contains("sip") -> 
                        "Sama-sama Kak! Ditunggu orderannya ya. 🙏"
                    lowerMsg.contains("kurang") || lowerMsg.contains("diskon") || lowerMsg.contains("nego") ->
                        "Maaf Kak, untuk harganya sudah pas/netto ya. Kualitas kami jamin terbaik! 🌿"
                    else -> 
                        "Baik Kak. Jika ada pertanyaan lebih lanjut terkait detail produk kami, silakan sampaikan ya. Kami siap membantu! 🌾"
                }

                val reply = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = replyText,
                    isFromUser = false,
                    timestamp = time
                )
                
                // Simpan ke repository
                com.zaky.agrocare.data.ChatRepository.addMessage(args.mitraName, reply)
                
                // Update UI
                chatAdapter.addMessage(reply)
                binding.rvChat.scrollToPosition(messages.size - 1)
            }
        }, 1500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
