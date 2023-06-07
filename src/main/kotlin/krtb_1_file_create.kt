import java.io.File

data class Word(
    var wordEnglish: String = "",
    var wordRussian: String = "",
    var correctAnswersCount: Int = 0
)

fun main() {
    val wordsFile = File("word.txt")
    wordsFile.createNewFile()
    wordsFile.writeText(
        "hello|привет\n" +
                "dog|собака|1\n" +
                "cat|кошка\n"
    )
    val listString: List<String> = wordsFile.readLines()
    val dictionary = mutableListOf<Word>()

    for (line in listString) {
        val wordList = line.split("|")
        val word = Word(
            wordEnglish = wordList[0],
            wordRussian = wordList[1],
            correctAnswersCount = if (wordList.count() >= 3) wordList[2].toInt()
            else 0
        )

        dictionary.add(word)
    }
    println(dictionary)
}