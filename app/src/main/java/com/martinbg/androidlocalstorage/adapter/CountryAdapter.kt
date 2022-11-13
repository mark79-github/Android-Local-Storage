package com.martinbg.androidlocalstorage.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.martinbg.androidlocalstorage.R
import com.martinbg.androidlocalstorage.data.CountryModel
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
        fun bind(item: CountryModel) {
            binding.apply {
                tvCName.text = item.name
                tvCCapital.text = item.capital
                tvCRegion.text = item.region
                val isConnected: Boolean = Prefs[R.string.has_internet_connection.toString()]
                if (isConnected) {
                    imgFlag.scaleType = ImageView.ScaleType.FIT_XY
                    imgFlag.load(item.flags.png) {
                        placeholder(R.drawable.flag_placeholder)
                    }
                } else {
                    imgFlag.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    imgFlag.load(R.drawable.flag_placeholder) {
                        placeholder(R.drawable.flag_placeholder)
                    }
                }
                root.setOnClickListener {
                    val intent = Intent(context, CountryDetailsActivity::class.java)
                    intent.putExtra(R.string.intent_extra_attribute_country_name.toString(), item.name)
                    context.startActivity(intent)
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<CountryModel>() {
        override fun areItemsTheSame(
            oldItem: CountryModel,
            newItem: CountryModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CountryModel,
            newItem: CountryModel
        ): Boolean {
            return oldItem.flags.png == newItem.flags.png
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}