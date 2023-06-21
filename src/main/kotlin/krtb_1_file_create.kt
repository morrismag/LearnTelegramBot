data class Word(
    val wordEnglish: String = "",
    val wordRussian: String = "",
    var correctAnswersCount: Int = 0
)

fun main() {

    val trainer = try {

        LearnWordsTrainer(3, 4)
    } catch (e: Exception) {
        println("Невозможно загрузить файл")
        return
    }

    while (true) {
        println("Меню:\n 1 – Учить слова\n 2 – Статистика\n 0 – Выход")
        println("Выберите пункт меню - введите его число:")

        when (readln()) {
            "1" -> {
                println("Вы зашли в экран \"Учить слова\"")
                trainer.learnWords(trainer.dictionary)
            }

            "2" -> {
                val statistics = trainer.getStatistics()
                println("Вы зашли в экран \"Статистика\"")
                println()
                println(
                    "Выучено ${statistics.countLearnWord} из ${statistics.countWordInDictionary} слов" +
                            "| ${statistics.countLearnWord.toDouble() / statistics.countWordInDictionary * 100}%."
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

