fun main() {
    print("Digite seu peso: ")
    val peso: Double = readln().toDouble()

    print("Digite sua altura: ")
    val altura: Double = readln().toDouble()

    val imc = peso / (altura * altura)

    if(imc <= 18.5){
        println("Magreza")
    }else if(imc <= 24.9){
        println("Peso normal")
    }else if(imc <= 29.9){
        println("Sobrepeso")
    }else if(imc <= 39.99){
        println("Obesidade")
    }else{
        println("Obesidade grave")
    }
}
main()