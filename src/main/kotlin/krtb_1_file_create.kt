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
                learnWords(trainer.dictionary)
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

fun learnWords(dictionary: List<Word>) {
    val trainerWord = LearnWordsTrainer()

    while (true) {
        val question = trainerWord.getNextQuestion()

        if (question == null) {
            println("Поздравляю!!! Вы выучили все слова!")
            break
        }

        if (question.variants.size < trainerWord.countOfQuestionWords) {
            val dictionaryLearnedWords = dictionary.filter { it.correctAnswersCount >= trainerWord.unlearnWords }
            question.variants =
                question.variants + dictionaryLearnedWords.shuffled()
                    .take(trainerWord.countOfQuestionWords - question.variants.size)
        }

        println(question.asConsoleString())

        val answerId = readln().toIntOrNull()
        if (answerId == 0) break

        if (trainerWord.checkAnswer(answerId?.minus(1))) {
            println("Верно.")
        } else println("Неверно.")
    }
}

fun Question.asConsoleString(): String {
    val variantWord = this.variants
        .mapIndexed { index: Int, word: Word -> "${index + 1} - ${word.wordRussian}" }
        .joinToString(separator = "\n")
    return this.correctAnswer.wordEnglish + "\n" + variantWord + "\n0 - выйти в меню"
}

