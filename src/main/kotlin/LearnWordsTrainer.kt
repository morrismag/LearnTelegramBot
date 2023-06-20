import java.io.File

class Statistics(
    val countWordInDictionary: Int,
    val countLearnWord: Int,
)

class LearnWordsTrainer(
    private val unlearnWords: Int = 3,
    private val countOfQuestionWords: Int = 4,
    private val fileName: String = "word.txt",
) {
    val dictionary = loadDictionary()

    fun learnWords(dictionary: List<Word>) {
        var dictionaryUnlearnWords: List<Word>

        while (true) {
            dictionaryUnlearnWords = dictionary.filter { it.correctAnswersCount < unlearnWords }

            if (dictionaryUnlearnWords.isEmpty()) {
                println("Поздравляю!!! Вы выучили все слова!")
                break
            }

            var translateWords = dictionaryUnlearnWords.shuffled().take(countOfQuestionWords)
            val unlearnWord = translateWords.random()

            if (translateWords.size < countOfQuestionWords) {
                val dictionaryLearnedWords = dictionary.filter { it.correctAnswersCount >= unlearnWords }
                translateWords =
                    translateWords + dictionaryLearnedWords.shuffled().take(countOfQuestionWords - translateWords.size)
            }

            println(questionToString(translateWords, unlearnWord))

            val answerId = readln().toIntOrNull()
            val correctAnswerId = translateWords.indexOf(unlearnWord) + 1

            when (answerId) {
                correctAnswerId -> {
                    println("Верно.")
                    println()
                    unlearnWord.correctAnswersCount += 1
                    saveDictionary(dictionary)
                }

                0 -> break
                else -> println("Неверно.")
            }
        }
    }

    fun getStatistics(): Statistics {
        val countWordInDictionary = dictionary.count()
        val countLearnWord = dictionary.count { it.correctAnswersCount >= unlearnWords }
        return Statistics(countWordInDictionary, countLearnWord)
    }

    private fun createFileDictionary(): File {
        val wordsFile = File(fileName)
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

    private fun loadDictionary(): List<Word> {
        try {
            val dictionary = mutableListOf<Word>()
            val wordsFile = File(fileName)

            if (!wordsFile.exists()) createFileDictionary()

            val listString: List<String> = wordsFile.readLines()

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
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFileRewrite = File(fileName)
        wordsFileRewrite.writeText("")

        dictionary.forEach {
            wordsFileRewrite.appendText(
                it.wordEnglish + "|" +
                        it.wordRussian + "|" + it.correctAnswersCount + "\n"
            )
        }
    }

    private fun questionToString(translateWords: List<Word>, unlearnWord: Word): String {
        val variantWord = translateWords
            .mapIndexed { index: Int, word: Word -> "${index + 1} - ${word.wordRussian}" }
            .joinToString(separator = "\n")
        return unlearnWord.wordEnglish + "\n" + variantWord + "\n0 - выйти в меню"
    }
}
