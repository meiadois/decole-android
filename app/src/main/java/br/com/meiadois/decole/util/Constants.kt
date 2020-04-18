package br.com.meiadois.decole.util

import br.com.meiadois.decole.model.Step

class Constants {
    companion object {
        fun steps(maxX: Int, maxY: Int) = listOf(
            Step("Clique em seu perfil", 0, maxY),
            Step("Clique em sua biografia", maxX, maxY),
            Step("Clique 3", 0, 0),
            Step("Clique 4", maxX, 0)
        )
    }
}