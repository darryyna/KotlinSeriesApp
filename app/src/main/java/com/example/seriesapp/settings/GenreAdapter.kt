package com.example.seriesapp.settings

import com.example.seriesapp.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Genre(
    val title: String,
    val description: String
)

class GenreAdapter(private val genres: List<Genre>) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    inner class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGenreTitle: TextView = itemView.findViewById(R.id.tvGenreTitle)
        val tvGenreDescription: TextView = itemView.findViewById(R.id.tvGenreDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.genre_list_item, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.tvGenreTitle.text = genre.title
        holder.tvGenreDescription.text = genre.description
    }

    override fun getItemCount(): Int = genres.size
}
