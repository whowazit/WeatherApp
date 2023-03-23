package com.jayjohn.app.feature.presentation.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jayjohn.app.R
import com.jayjohn.app.feature.domain.model.Location
import com.jayjohn.app.feature.domain.model.LocationData

class LocationAdapter (
    context: Context
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null

    private val inflater = LayoutInflater.from(context)
    private val locations = ArrayList<LocationData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(inflater.inflate(R.layout.item_location, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            locations[position]?.let {
                holder.onBind(it)
            }
        }
    }

    override fun getItemCount(): Int = locations.count()

    fun setLocationsToList(locations: List<LocationData>) {
        this.locations.clear()
        this.locations.addAll(locations)
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvLocation = view.findViewById<TextView>(R.id.locationTv)

        fun onBind(
            data: LocationData
        ) {
            tvLocation.text = SpannableStringBuilder()
                .bold { append(data.name) }
                .append(" - ${data.region}")

            tvLocation.setOnClickListener {
                onItemClick?.invoke("${data.latitude},${data.longitude}")
            }
        }
    }
}