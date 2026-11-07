package com.example.koin.data

object DadosEntrePaginas {
    var id: Long? = null
    var nome: String = ""
    var email: String = ""

    fun limpar() {
        id = null
        nome = ""
        email = ""
    }
}