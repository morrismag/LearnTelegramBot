import java.io.File

data class Word(
    val wordEnglish: String = "",
    val wordRussian: String = "",
    var correctAnswersCount: Int = 0
)

fun main() {
    /** Создание и наполнение файла списком слов для изучения*/
    val wordsFile = File("word.txt")
    wordsFile.createNewFile()
    wordsFile.writeText(
        "hello|привет\n" +
                "dog|собака|10\n" +
                "door|дверь|1\n" +
                "sun|солнце|7\n" +
                "wind|ветер|1\n" +
                "room|комната|1\n" +
                "truth|правда|3\n" +
                "king|король|1\n" +
                "victory|победа|1\n" +
                "cat|кошка|5\n"
    )
    val listString: List<String> = wordsFile.readLines()
    val dictionary = mutableListOf<Word>()

    /**цикл считывания строк из файла и занесение их в список*/
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

    println("Меню:\n 1 – Учить слова\n 2 – Статистика\n 0 – Выход")
    println("Выберите пункт меню - введите его число:")
    var inputItem = readln()

    while (inputItem !in listOf("0", "1", "2")) {
        println("Введите правильный пункт меню:")
        inputItem = readln()
    }

    when (inputItem) {
        "1" -> println("Вы зашли в экран \"Учить слова\"")
        "2" -> {
            println("Вы зашли в экран \"Статистика\"")

            /** Общее количество элементов (слов для обучения) в списке dictionary*/
            val countWordInDictionary = dictionary.count()

            /** Количество выученных слов в dictionary*/
            val countLearnWord = dictionary.count { it.correctAnswersCount >= 3 }
            println()
            println(
                "Выучено $countLearnWord из $countWordInDictionary слов" +
                        "| ${countLearnWord.toDouble() / countWordInDictionary * 100}%."
            )
        }

        "0" -> println(
            "Мы надеемся, что наша программа была Вам полезна.\n" +
                    "Ждем Вас снова."
        )
    }
}