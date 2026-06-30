package com.zaky.agrocare.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaky.agrocare.data.ChatMessage
import com.zaky.agrocare.databinding.FragmentSupportChatBinding
import com.zaky.agrocare.ui.chat.ChatAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SupportChatFragment : Fragment() {

    private var _binding: FragmentSupportChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<ChatMessage>()
    private val SUPPORT_ID = "AgroCare_Support_Bot"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        messages.clear()
        
        // Ambil data chat dari repository untuk Support
        val existingMessages = com.zaky.agrocare.data.ChatRepository.getMessages(SUPPORT_ID)
        if (existingMessages.isEmpty()) {
            // Tambahkan pesan welcome default jika belum ada chat
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val welcomeMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                text = "Halo! Selamat datang di AgroCare Support. 👋\nAda kendala apa yang bisa kami bantu hari ini?",
                isFromUser = false,
                timestamp = time
            )
            com.zaky.agrocare.data.ChatRepository.addMessage(SUPPORT_ID, welcomeMsg)
            messages.add(welcomeMsg)
        } else {
            messages.addAll(existingMessages)
        }

        chatAdapter = ChatAdapter(messages)
        binding.rvSupportChat.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
            scrollToPosition(messages.size - 1)
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
                
                com.zaky.agrocare.data.ChatRepository.addMessage(SUPPORT_ID, message)
                
                chatAdapter.addMessage(message)
                binding.etMessage.text.clear()
                binding.rvSupportChat.scrollToPosition(messages.size - 1)
                
                simulateSupportReply(text)
            }
        }
    }

    private fun simulateSupportReply(userMessage: String) {
        binding.rvSupportChat.postDelayed({
            if (_binding != null) {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                
                val lowerMsg = userMessage.lowercase(Locale.getDefault())
                val replyText = when {
                    lowerMsg.contains("halo") || lowerMsg.contains("hai") || lowerMsg.contains("min") -> 
                        "Halo! Kami siap membantu Anda. Silakan jelaskan kendala yang Anda alami."
                    lowerMsg.contains("password") || lowerMsg.contains("sandi") || lowerMsg.contains("akun") || lowerMsg.contains("login") -> 
                        "Jika Anda mengalami masalah dengan akun atau lupa kata sandi, Anda bisa mereset kata sandi melalui menu 'Lupa Kata Sandi' di halaman masuk."
                    lowerMsg.contains("rusak") || lowerMsg.contains("kembali") || lowerMsg.contains("retur") -> 
                        "Terkait pengembalian produk rusak, Anda bisa mengajukan komplain maksimal 2x24 jam sejak barang diterima. Pastikan Anda memiliki video unboxing."
                    lowerMsg.contains("lama") || lowerMsg.contains("belum sampai") || lowerMsg.contains("resi") -> 
                        "Mohon maaf atas keterlambatannya. Anda dapat melacak resi pengiriman melalui halaman Profil -> Pesanan Saya. Jika sudah melewati batas waktu estimasi, harap hubungi kami kembali dengan menyertakan Nomor Pesanan."
                    lowerMsg.contains("bayar") || lowerMsg.contains("transfer") || lowerMsg.contains("gagal") -> 
                        "Untuk kendala pembayaran, pastikan Anda mentransfer sesuai nominal hingga 3 digit terakhir. Jika dana sudah terpotong namun status belum berubah, mohon tunggu 1x24 jam ya."
                    lowerMsg.contains("terima kasih") || lowerMsg.contains("ok") || lowerMsg.contains("baik") -> 
                        "Sama-sama! Jangan ragu untuk menghubungi kami lagi jika ada pertanyaan lain. Sehat selalu! 🌱"
                    else -> 
                        "Terima kasih atas pesan Anda. Agen Customer Service kami sedang sibuk saat ini. Mohon tinggalkan keluhan lengkap Anda dan kami akan membalas secepatnya. 👩‍💻"
                }

                val reply = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = replyText,
                    isFromUser = false,
                    timestamp = time
                )
                
                com.zaky.agrocare.data.ChatRepository.addMessage(SUPPORT_ID, reply)
                chatAdapter.addMessage(reply)
                binding.rvSupportChat.scrollToPosition(messages.size - 1)
            }
        }, 1500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
