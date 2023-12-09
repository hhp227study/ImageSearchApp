package com.hhp227.imagesearchapp.adapters

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.arch.core.util.Function
import androidx.databinding.BindingAdapter
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hhp227.imagesearchapp.data.Document

@BindingAdapter("submitItems")
fun submitItems(v: Spinner, list: List<Nothing>) {
    (v.adapter as? ArrayAdapter<*>)?.clear()
    (v.adapter as? ArrayAdapter<*>)?.addAll(list)
    (v.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
}

@BindingAdapter("selection")
fun setSelection(v: Spinner, query: String) {
    v.setSelection(0)
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
    // Glide 사용X
    /*if (!imageUrl.isNullOrEmpty()) {
        ImageLoader.loadImage(imageUrl) {
            CoroutineScope(Dispatchers.Main).launch {
                view.setImageBitmap(it)
            }
        }
    }*/
}

@BindingAdapter(value = ["submitData", "filter"])
fun submitData(v: RecyclerView, data: PagingData<Document>, keyword: String?) {
    ((v.adapter as? ConcatAdapter)?.adapters?.first() as? ImagePagingAdapter)?.submitItem(data, keyword)
}

@BindingAdapter("spanSize")
fun spanSize(v: RecyclerView, spanCount: Int) {
    (v.layoutManager as? GridLayoutManager)?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (v.adapter?.getItemViewType(position) == 0) 1 else spanCount
        }
    }
}

@BindingAdapter("onQueryTextSubmit")
fun onQueryTextSubmit(v: SearchView, action: Function<String, Unit>) {
    v.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            action.apply(query)
            v.clearFocus()
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    })
}