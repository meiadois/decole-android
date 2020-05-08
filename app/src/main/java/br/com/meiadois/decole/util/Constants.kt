package br.com.meiadois.decole.util

import br.com.meiadois.decole.data.model.Step

class Constants {
    companion object {
        fun steps(maxX: Int, maxY: Int) = listOf(
            Step("Abra o app do Instagram!", 0, maxY / 2),
            Step(
                "Olá! Eu sou o assistente de toque do Decole, clique na seta abaixo para prosseguir.",
                0,
                maxY / 2
            ),
            Step("Vá para seu perfil do Instagram", 0, maxY / 2),
            Step(
                "Sugestões para sua biografia:\n 1.Fale sobre o seu negócio;\n 2.Inclua um telefone para contato.",
                0,
                maxY / 2
            )
        )
    }
}