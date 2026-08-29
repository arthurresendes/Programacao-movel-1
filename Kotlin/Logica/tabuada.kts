fun main(){
    print("Digite o numero da tabuada: ")
    val tabuada = readln().toInt()
    for (i in 1..10) {
        println(i * tabuada)
    }
}

main()