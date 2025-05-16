package com.example.seriesapp

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compose.AppTheme
import com.example.seriesapp.settings.Genre
import com.example.seriesapp.settings.GenreAdapter


class GenreListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AndroidView(
                        factory = { context ->
                            val view = LayoutInflater.from(context).inflate(R.layout.activity_show_list, null, false)

                            val recyclerView = view.findViewById<RecyclerView>(R.id.rvShows)
                            recyclerView.layoutManager = LinearLayoutManager(context)

                            val genres = listOf(
                                Genre("Драма", "Емоційні історії з глибокими характерами і конфліктами."),
                                Genre("Комедія", "Легкі та веселі сюжети, що викликають сміх."),
                                Genre("Фантастика", "Історії про майбутнє, космос, технології і незвичайне."),
                                Genre("Трилер", "Напружені сюжети з несподіваними поворотами."),
                                Genre("Містика", "Надприродні явища, загадки і таємниці."),
                                Genre("Романтика", "Любовні історії і відносини між героями."),
                                Genre("Документальний", "Реальні події та факти, подані у захопливій формі.")
                            )

                            recyclerView.adapter = GenreAdapter(genres)
                            view
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}