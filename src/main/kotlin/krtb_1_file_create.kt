import java.io.File
import kotlin.system.exitProcess

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
        var inputItem = readln()

        while (inputItem !in listOf("0", "1", "2")) {
            println("Введите правильный пункт меню:")
            inputItem = readln()
        }

        when (inputItem) {
            "1" -> {
                println("Вы зашли в экран \"Учить слова\"")
                var dictionaryUnlearnWords = dictionary.filter { it.correctAnswersCount < 3 }

                var exitLoop1 = false
                while (!exitLoop1 && (dictionary.filter { it.correctAnswersCount < 3 }).isNotEmpty()) {
                    dictionaryUnlearnWords = dictionary.filter { it.correctAnswersCount < 3 }

                    val n: Int = when {
                        dictionaryUnlearnWords.size >= 4 -> 4
                        else -> {
                            dictionaryUnlearnWords.size
                        }
                    }

                    val translateWords = dictionaryUnlearnWords.shuffled().take(n)
                    val unlearnWord = (0 until n).random()

                    when {
                        n >= 4 -> {
                            println("Слово: ${translateWords[unlearnWord].wordEnglish}")
                            println(
                                "Введи правильный ответ: \n" +
                                        "1 - ${translateWords[0].wordRussian}\n" +
                                        "2 - ${translateWords[1].wordRussian}\n" +
                                        "3 - ${translateWords[2].wordRussian}\n" +
                                        "4 - ${translateWords[3].wordRussian}\n"
                            )
                            println("0 - выход в меню")
                        }

                        n == 3 -> {
                            println("Слово: ${translateWords[unlearnWord].wordEnglish}")
                            println(
                                "Введи правильный ответ: \n" +
                                        "1 - ${translateWords[0].wordRussian}\n" +
                                        "2 - ${translateWords[1].wordRussian}\n" +
                                        "3 - ${translateWords[2].wordRussian}\n" +
                                        "4 - ${dictionary[(0..dictionary.size).random()].wordRussian}\n"
                            )
                            println("0 - выход в меню")
                        }

                        n == 2 -> {
                            println("Слово: ${translateWords[unlearnWord].wordEnglish}")
                            println(
                                "Введи правильный ответ: \n" +
                                        "1 - ${translateWords[0].wordRussian}\n" +
                                        "2 - ${dictionary[(0 until dictionary.size).random()].wordRussian}\n" +
                                        "3 - ${translateWords[1].wordRussian}\n" +
                                        "4 - ${dictionary[(0 until dictionary.size).random()].wordRussian}\n"
                            )
                            println("0 - выход в меню")
                        }

                        n == 1 -> {
                            println("Слово: ${translateWords[unlearnWord].wordEnglish}")
                            println(
                                "Введи правильный ответ: \n" +
                                        "1 - ${translateWords[0].wordRussian}\n" +
                                        "2 - ${dictionary[(0 until dictionary.size).random()].wordRussian}\n" +
                                        "3 - ${dictionary[(0 until dictionary.size).random()].wordRussian}\n" +
                                        "4 - ${dictionary[(0 until dictionary.size).random()].wordRussian}\n"
                            )
                            println("0 - выход в меню")
                        }
                    }
                    val answer = readln().toIntOrNull() ?: 999

                    if (unlearnWord == (answer - 1)) {
                        println("Верно.")
                        println()
                        translateWords[unlearnWord].correctAnswersCount += 1
                        saveDictionary(dictionary)
                    } else if (answer == 0) {
                        println("Возврат к прошлому меню.")
                        exitLoop1 = true
                        break
                    } else println("Неверно.")
                }
                if (!exitLoop1) {
                    println("Поздравляю!!! Вы выучили все слова!")
                    exitProcess(0)
                } else {
                    continue
                }
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
                println("0 - выход в меню")
                val answer = readln().toIntOrNull() ?: 999
                if (answer == 0) {
                    continue
                } else {
                    exitProcess(0)
                }
            }

            "0" -> {
                println(
                    "Мы надеемся, что наша программа была Вам полезна.\n" +
                            "Ждем Вас снова."
                )
                exitProcess(0)
            }
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

