import java.io.File

fun main() {
    val wordsFile = File("word.txt")
    wordsFile.createNewFile()
    wordsFile.writeText(
        "hello привет\n" +
                "dog собака\n" +
                "cat кошка\n"
    )
    val listString = wordsFile.readLines()

    for (i in listString.indices) {
        println(listString[i])
    }
}