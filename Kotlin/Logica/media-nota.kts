fun main() {
    print("Digite a nota da atividade: ")
    val atividade: Double = readln().toDouble()
    print("Digite a nota do primeiro trabalho: ")
    val trabalho1: Double = readln().toDouble()
    print("Digite a nota do 2 trabalho: ")
    val trabalho2: Double = readln().toDouble()
    val media: Double = ((atividade *0.20 )+ (trabalho1 *0.40) + (trabalho2 *0.40))/(0.20+0.40+0.40)
    println("Media: $media ")
}

main()