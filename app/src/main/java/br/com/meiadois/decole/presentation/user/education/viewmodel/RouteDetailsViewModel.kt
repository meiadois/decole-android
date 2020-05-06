package br.com.meiadois.decole.presentation.user.education.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.meiadois.decole.data.localdb.entity.Route
import br.com.meiadois.decole.data.model.Lesson
import br.com.meiadois.decole.data.repository.RouteRepository
import br.com.meiadois.decole.util.lazyDeferred

class RouteDetailsViewModel(
    val repository: RouteRepository
) : ViewModel() {

    val lessons by lazyDeferred {
        //        repository.getRoutes()
        getLessons()
    }

    fun onItemClick(item: Route, view: View) {

    }

    //TODO Remove
    fun getLessons() = mockLessons()

    private fun mockLessons(): LiveData<List<Lesson>> {
        val data = MutableLiveData<List<Lesson>>()
        data.postValue(
            listOf(
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
        )

        return data
    }

}