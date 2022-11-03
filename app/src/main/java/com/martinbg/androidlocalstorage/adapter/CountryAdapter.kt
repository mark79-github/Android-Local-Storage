package com.martinbg.androidlocalstorage.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.martinbg.androidlocalstorage.R
import com.martinbg.androidlocalstorage.data.Country
import com.martinbg.androidlocalstorage.databinding.ItemCountryLayoutBinding
import com.martinbg.androidlocalstorage.ui.CountryDetailsActivity
import com.martinbg.androidlocalstorage.utils.Prefs


class CountryAdapter : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    private lateinit var binding: ItemCountryLayoutBinding
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemCountryLayoutBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        val isConnected: Boolean = Prefs["isConnected"]

        fun bind(item: Country) {
            binding.apply {
                tvCName.text = item.name
                tvCCapital.text = item.capital
                tvCRegion.text = item.region

                when (isConnected) {
                    true -> imgFlag.load(item.flags.png) {
                        crossfade(true)
                        placeholder(R.drawable.flag_placeholder)
                        scale(Scale.FILL)
                    }
                    false -> imgFlag.load(R.drawable.flag_placeholder) {
                        crossfade(true)
                        placeholder(R.drawable.flag_placeholder)
                        scale(Scale.FIT)
                    }
                }

                root.setOnClickListener {
                    val intent = Intent(context, CountryDetailsActivity::class.java)
                    intent.putExtra("name", item.name)
                    context.startActivity(intent)
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(
            oldItem: Country,
            newItem: Country
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: Country,
            newItem: Country
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}