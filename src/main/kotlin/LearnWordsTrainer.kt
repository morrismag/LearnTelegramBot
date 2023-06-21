import java.io.File

class Statistics(
    val countWordInDictionary: Int,
    val countLearnWord: Int,
)

data class Question(
    var variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val unlearnWords: Int = 3,
    private val countOfQuestionWords: Int = 4,
    private val fileName: String = "word.txt",
) {
    private var question: Question? = null
    val dictionary = loadDictionary()

    fun learnWords(dictionary: List<Word>) {


        while (true) {
            val question = getNextQuestion()

            if (question == null) {
                println("Поздравляю!!! Вы выучили все слова!")
                break
            }

            if (question.variants.size < countOfQuestionWords) {
                val dictionaryLearnedWords = dictionary.filter { it.correctAnswersCount >= unlearnWords }
                question.variants =
                    question.variants + dictionaryLearnedWords.shuffled()
                        .take(countOfQuestionWords - question.variants.size)
            }

            println(question.asConsoleString())

            val answerId = readln().toIntOrNull()
            if (answerId == 0) break

            if (checkAnswer(answerId?.minus(1))) {
                println("Верно.")
            } else println("Неверно.")
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

    private fun Question.asConsoleString(): String {
        val variantWord = this.variants
            .mapIndexed { index: Int, word: Word -> "${index + 1} - ${word.wordRussian}" }
            .joinToString(separator = "\n")
        return this.correctAnswer.wordEnglish + "\n" + variantWord + "\n0 - выйти в меню"
    }

    private fun getNextQuestion(): Question? {
        val dictionaryUnlearnWords: List<Word> = dictionary.filter { it.correctAnswersCount < unlearnWords }
        if (dictionaryUnlearnWords.isEmpty()) return null
        val translateWords = dictionaryUnlearnWords.shuffled().take(countOfQuestionWords)
        val unlearnWord = translateWords.random()
        question = Question(
            variants = translateWords,
            correctAnswer = unlearnWord,
        )
        return question
    }

    private fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount += 1
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false
    }
}

