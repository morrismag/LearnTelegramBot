import java.io.File

data class Word(
    val wordEnglish: String = "",
    val wordRussian: String = "",
    var correctAnswersCount: Int = 0
)

fun main() {
    /** Создание и наполнение файла списком слов для изучения*/
    val fileName = "word.txt"
    val wordsFile = File(fileName)

    if (!wordsFile.exists()) createFileDictionary(fileName)

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

    while (true) {

        println("Меню:\n 1 – Учить слова\n 2 – Статистика\n 0 – Выход")
        println("Выберите пункт меню - введите его число:")
        val inputItem = readln()

        when (inputItem) {
            "1" -> {
                println("Вы зашли в экран \"Учить слова\"")
                learnWords(dictionary)
            }

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
                println()
            }

            "0" -> {
                println(
                    "Мы надеемся, что наша программа была Вам полезна.\n" +
                            "Ждем Вас снова."
                )
                return
            }

            else -> println("Введите правильный пункт меню:\n")
        }
    }
}

fun saveDictionary(dictionary: MutableList<Word>) {
    val wordsFileRewrite = File("word.txt")
    wordsFileRewrite.writeText("")

    dictionary.forEach {
        wordsFileRewrite.appendText(
            it.wordEnglish + "|" +
                    it.wordRussian + "|" + it.correctAnswersCount + "\n"
        )
    }
}

fun createFileDictionary(nameFile: String): File {
    val wordsFile = File(nameFile)
    wordsFile.createNewFile()
    wordsFile.writeText(
        "hello|привет|2\n" +
                "dog|собака|10\n" +
                "door|дверь|3\n" +
                "sun|солнце|7\n" +
                "wind|ветер|3\n" +
                "room|комната\n" +
                "truth|правда|3\n" +
                "king|король|2\n" +
                "victory|победа|3\n" +
                "cat|кошка|5\n"
    )
    return wordsFile
}

fun learnWords(dictionary: MutableList<Word>) {
    var dictionaryUnlearnWords = dictionary.filter { it.correctAnswersCount < 3 }

    while (true) {
        dictionaryUnlearnWords = dictionary.filter { it.correctAnswersCount < 3 }

        if (dictionaryUnlearnWords.isEmpty()) {
            println("Поздравляю!!! Вы выучили все слова!")
            break
        }

        var translateWords = dictionaryUnlearnWords.shuffled().take(4)
        val unlearnWord = translateWords.random()

        if (translateWords.size < 4) {
            val dictionaryLearnedWords = dictionary.filter { it.correctAnswersCount >= 3 }
            translateWords = translateWords + dictionaryLearnedWords.shuffled().take(4 - translateWords.size)
        }

        println("Слово: ${unlearnWord.wordEnglish}")
        println(
            "Введи правильный ответ: \n" +
                    "1 - ${translateWords[0].wordRussian}\n" +
                    "2 - ${translateWords[1].wordRussian}\n" +
                    "3 - ${translateWords[2].wordRussian}\n" +
                    "4 - ${translateWords[3].wordRussian}\n"
        )
        println("0 - выход в меню")

        val answer = readln().toIntOrNull() ?: 999

        if (answer in (1..4) && unlearnWord.wordEnglish == translateWords[answer - 1].wordEnglish) {
            println("Верно.")
            println()
            unlearnWord.correctAnswersCount += 1
            saveDictionary(dictionary)
        } else if (answer == 0) {
            println("Возврат к прошлому меню.")
            break
        } else println("Неверно.")
    }
}