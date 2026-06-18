package com.zaky.agrocare.ui.education

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaky.agrocare.databinding.ItemEducationCardBinding

class EducationAdapter(private val listModule: List<EducationModule>) :
    RecyclerView.Adapter<EducationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEducationCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEducationCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val module = listModule[position]
        with(holder.binding) {
            tvTitle.text = module.title
            tvDescription.text = module.description
            tvCategory.text = module.category
            // Image loading with Coil (as per dependencies) could be added here
            // Example: ivModuleImage.load(module.imageUrl)
        }
    }

    override fun getItemCount(): Int = listModule.size
}
