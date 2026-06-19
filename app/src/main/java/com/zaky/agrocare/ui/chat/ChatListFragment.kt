package com.zaky.agrocare.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaky.agrocare.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        // Mengambil data chat dari Repository
        val chatList = com.zaky.agrocare.data.ChatRepository.getChatListSummary()

        val chatListAdapter = ChatListAdapter(chatList) { mitraName ->
            // Buka halaman chat spesifik
            val action = ChatListFragmentDirections.actionChatListToChat(mitraName)
            findNavController().navigate(action)
        }

        binding.rvChatList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatListAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Data class khusus untuk item di chat list
data class ChatListItem(
    val mitraName: String,
    val lastMessage: String,
    val time: String
)
