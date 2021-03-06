package com.sriram.wally.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.sriram.wally.R
import com.sriram.wally.models.response.Photo
import com.sriram.wally.utils.DiffCallback
import com.sriram.wally.utils.Logger
import kotlinx.android.synthetic.main.item_image.view.*

class PhotoListAdapter(context: Context, val picasso: Picasso) : RecyclerView.Adapter<PhotoListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val images = arrayListOf<Photo>()
    private var mListener: PhotoListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = images.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(images[position])

    fun setImages(images: ArrayList<Photo>) {
        Logger.e("Images set")
        val callback = DiffCallback(this.images, images)
        val result = DiffUtil.calculateDiff(callback)

        result.dispatchUpdatesTo(this)
        this.images.clear()
        this.images.addAll(images)
    }

    fun onItemClickListener(listener: PhotoListener) {
        mListener = listener
    }

    fun getImages(): ArrayList<Photo> {
        return images
    }

    interface PhotoListener {
        fun onPhotoClicked(photo: Photo)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                mListener?.onPhotoClicked(images[adapterPosition])
            }
        }

        fun bind(image: Photo) {
            Logger.e(image.urls?.regular ?: "No regular image")

            val placeholder = ColorDrawable(Color.parseColor(image.color))

            picasso.load(image.urls?.regular)
                    .error(android.R.color.black)
                    .placeholder(placeholder)
                    .into(itemView.item_image)
        }
    }
}