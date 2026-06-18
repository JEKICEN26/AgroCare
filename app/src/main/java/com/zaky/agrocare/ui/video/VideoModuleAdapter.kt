package com.zaky.agrocare.ui.video

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zaky.agrocare.databinding.ItemVideoModuleBinding

class VideoModuleAdapter(private val videos: List<VideoModule>) : RecyclerView.Adapter<VideoModuleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemVideoModuleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = videos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        
        holder.binding.tvVideoTitle.text = video.title
        holder.binding.tvVideoChannel.text = video.channel
        holder.binding.tvVideoDescription.text = video.description
        
        // Load YouTube Thumbnail using Coil
        val thumbnailUrl = "https://img.youtube.com/vi/${video.id}/hqdefault.jpg"
        holder.binding.ivThumbnail.load(thumbnailUrl) {
            crossfade(true)
            placeholder(android.R.color.darker_gray)
            error(android.R.color.darker_gray)
        }
        
        holder.binding.root.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=${video.id}"))
            holder.itemView.context.startActivity(intent)
        }
    }
}
