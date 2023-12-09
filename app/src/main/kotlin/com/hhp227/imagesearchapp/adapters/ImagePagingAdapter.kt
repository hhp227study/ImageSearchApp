package com.hhp227.imagesearchapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.paging.filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hhp227.imagesearchapp.data.Document
import com.hhp227.imagesearchapp.databinding.ItemImageBinding
import com.hhp227.imagesearchapp.utilities.ALL

class ImagePagingAdapter(
    private val lifecycle: Lifecycle
) : PagingDataAdapter<Document, ImagePagingAdapter.ItemHolder>(ImageDiffCallback()), Filterable {
    lateinit var originData: PagingData<Document>

    val loadState get() = loadStateFlow.asLiveData()

    val collections get() = snapshot().items.mapNotNull(Document::collection).toSortedSet()

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                return FilterResults().apply {
                    values =
                        if (p0.isNullOrEmpty() || p0 == ALL) originData
                        else originData.filter { it.collection == p0 }
                }
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                submitData(lifecycle, p1?.values as? PagingData<Document> ?: PagingData.empty())
            }
        }
    }

    fun submitItem(data: PagingData<Document>, text: String?) {
        val keyword = text ?: return
        originData = data

        if (keyword.isEmpty()) submitData(lifecycle, data)
        else filter.filter(keyword)
    }

    inner class ItemHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Document?) = with(binding) {
            document = item

            executePendingBindings()
        }
    }
}

class ImageDiffCallback : DiffUtil.ItemCallback<Document>() {
    override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
        return oldItem.thumbnailUrl == newItem.thumbnailUrl
    }
}