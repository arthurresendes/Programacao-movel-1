package com.example.eleicao.data

object PesquisaAtual {

    var candidatoEspontaneo: String = ""

    var voto: String = ""

    var problemas: String = ""

    var nome: String = ""

    var telefone: String = ""

    fun limpar() {
        candidatoEspontaneo = ""
        voto = ""
        problemas = ""
        nome = ""
        telefone = ""
    }
}