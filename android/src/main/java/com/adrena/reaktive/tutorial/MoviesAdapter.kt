package com.adrena.reaktive.tutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adrena.reaktive.tutorial.model.MovieModel
import com.bumptech.glide.Glide

class MoviesAdapter: RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    private var mItems = mutableListOf<MovieModel>()

    fun setList(list: List<MovieModel>) {
        mItems.clear()
        mItems.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_movie_item, parent, false))
    }

    override fun getItemCount() = mItems.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = mItems[position]

        Glide.with(holder.itemView.context).load(movie.poster).into(holder.imgCover)

        holder.tvTitle.text = movie.title
    }

    class MovieViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgCover: ImageView = itemView.findViewById(R.id.cover)
        val tvTitle: TextView = itemView.findViewById(R.id.title)
    }

}