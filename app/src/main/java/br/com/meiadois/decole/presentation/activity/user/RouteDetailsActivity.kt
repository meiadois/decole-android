package br.com.meiadois.decole.presentation.activity.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.model.Lesson
import kotlinx.android.synthetic.main.activity_route_details.*
import kotlinx.android.synthetic.main.card_step.view.*


class RouteDetailsActivity : AppCompatActivity() {

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_details)

        scroll_container.post {
            scroll_container.smoothScrollTo(0, Int.MAX_VALUE)
        }

        with(lesson_recycler_view){
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)

            adapter = LessonRecyclerAdapter(lessons(), context,
                object : OnClickListener {
                    override fun onItemClick(position: Int) {
                        Log.i("assertClick", "$position")
                    }
                })
        }

    }

    private fun lessons(): List<Lesson> {
        return listOf(
            Lesson(
                "Alterando sua foto de perfil", true
            ),
            Lesson(
                "Alterando sua bio", true
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", false
            ),
            Lesson(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", false
            )
        )
    }

    inner class LessonRecyclerAdapter(
        private val dataset: List<Lesson>,
        private val context: Context,
        val cardClickListener: OnClickListener
    ) :
        RecyclerView.Adapter<LessonRecyclerAdapter.LessonViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
            val view = LayoutInflater
                .from(context)
                .inflate(R.layout.card_step, parent, false)

            return LessonViewHolder(view)
        }

        override fun getItemCount(): Int = dataset.size

        override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
            val lesson = dataset[position]
            holder.bindView(lesson)
        }


        inner class LessonViewHolder(val parent: View) :
            RecyclerView.ViewHolder(parent) {
            private val title = parent.step_title
            private val status_image = parent.status_image
            private val status_rect = parent.status_rect

            fun bindView(lesson: Lesson) {

                parent.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        cardClickListener.onItemClick(position)
                    }
                }
                val imgResource =
                    if (lesson.completed) R.drawable.ic_light_dot_timeline else R.drawable.ic_dark_dot_timeline
                val rectResource =
                    if (lesson.completed) R.drawable.light_timeline_rect else R.drawable.dark_timeline_rect
                status_image.setImageResource(imgResource)
                status_rect.setImageResource(rectResource)
                title.text = lesson.title
            }
        }
    }
}
