package br.com.meiadois.decole.presentation.user.education.binding

import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.databinding.CardLessonBinding
import com.xwray.groupie.databinding.BindableItem

class LessonItem(val lesson: Lesson) : BindableItem<CardLessonBinding>() {

    override fun getLayout(): Int = R.layout.card_lesson

    override fun bind(viewBinding: CardLessonBinding, position: Int) {
        viewBinding.lesson = lesson

        val imageRes =
            when {
                lesson.locked -> R.drawable.ic_light_dot_timeline_locked
                lesson.completed -> R.drawable.ic_dark_dot_timeline
                else -> R.drawable.ic_light_dot_timeline
            }

        viewBinding.statusImage.setImageResource(imageRes)
    }


}