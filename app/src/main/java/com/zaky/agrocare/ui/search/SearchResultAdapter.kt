package com.zaky.agrocare.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.zaky.agrocare.R
import com.zaky.agrocare.data.Product
import com.zaky.agrocare.data.SearchItem
import com.zaky.agrocare.ui.education.EducationModule

class SearchResultAdapter(
    private val onProductClick: (Product) -> Unit,
    private val onEducationClick: (EducationModule) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_RESULT = 1
    }

    private var items: List<SearchItem> = emptyList()

    fun submitList(newItems: List<SearchItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SearchItem.Header -> VIEW_TYPE_HEADER
            is SearchItem.ProductResult, is SearchItem.EducationResult -> VIEW_TYPE_RESULT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_search_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_search_result, parent, false)
            ResultViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SearchItem.Header -> {
                (holder as HeaderViewHolder).bind(item)
            }
            is SearchItem.ProductResult -> {
                (holder as ResultViewHolder).bindProduct(item.product, onProductClick)
            }
            is SearchItem.EducationResult -> {
                (holder as ResultViewHolder).bindEducation(item.module, onEducationClick)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvSectionHeader)
        fun bind(header: SearchItem.Header) {
            tvTitle.text = header.title
        }
    }

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvSearchItemTitle)
        private val tvSubtitle: TextView = view.findViewById(R.id.tvSearchItemSubtitle)
        private val tvType: TextView = view.findViewById(R.id.tvSearchItemType)
        private val ivImage: ShapeableImageView = view.findViewById(R.id.ivSearchItem)

        fun bindProduct(product: Product, onClick: (Product) -> Unit) {
            tvTitle.text = product.name
            tvSubtitle.text = product.price
            tvType.text = "Produk"
            tvType.setTextColor(itemView.context.getColor(R.color.colorPrimary))
            
            ivImage.load(product.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
            }

            itemView.setOnClickListener { onClick(product) }
        }

        fun bindEducation(module: EducationModule, onClick: (EducationModule) -> Unit) {
            tvTitle.text = module.title
            tvSubtitle.text = module.category
            tvType.text = "Artikel"
            tvType.setTextColor(itemView.context.getColor(R.color.colorAccentOrange))

            // Load default image or actual image if available
            ivImage.load(android.R.drawable.ic_menu_sort_by_size) {
                crossfade(true)
            }

            itemView.setOnClickListener { onClick(module) }
        }
    }
}
