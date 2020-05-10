package br.com.meiadois.decole.presentation.user.education.binding

import br.com.meiadois.decole.R
import br.com.meiadois.decole.data.localdb.entity.Lesson
import br.com.meiadois.decole.databinding.CardLessonBinding
import com.xwray.groupie.databinding.BindableItem

class LessonItem(val lesson: Lesson) : BindableItem<CardLessonBinding>() {

    override fun getLayout(): Int = R.layout.card_lesson

    override fun bind(viewBinding: CardLessonBinding, position: Int) {
        viewBinding.lesson = lesson
    }

}