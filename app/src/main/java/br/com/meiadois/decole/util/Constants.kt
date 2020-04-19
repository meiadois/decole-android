package br.com.meiadois.decole.util

import br.com.meiadois.decole.model.Step

class Constants {
    companion object {
        fun steps(maxX: Int, maxY: Int) = listOf(
            Step("Abrimos o Instagram para você. Clique nas setas abaixo para prosseguir.", maxX/2, maxY/2),
            Step("Vá para seu perfil do Instagram", maxX/2, maxY/2),
            Step("Sugestões para sua biografia:\n 1.Fale sobre o seu negócio;\n 2.Inclua um telefone para contato.", maxX/2, maxY/2)
        )
    }
}