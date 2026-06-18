package com.zaky.agrocare.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.zaky.agrocare.data.Article
import com.zaky.agrocare.databinding.ItemArticleBinding

class ArticleAdapter(private val listArticle: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = listArticle[position]
        holder.binding.apply {
            tvTitle.text = article.title
            tvDescription.text = article.description
            ivArticle.load(article.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    override fun getItemCount(): Int = listArticle.size
}
