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
    val unlearnWords: Int = 3,
    val countOfQuestionWords: Int = 4,
    private val fileName: String = "word.txt",
) {
    private var question: Question? = null
    val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val countWordInDictionary = dictionary.count()
        val countLearnWord = dictionary.count { it.correctAnswersCount >= unlearnWords }
        return Statistics(countWordInDictionary, countLearnWord)
    }

    fun getNextQuestion(): Question? {
        val dictionaryUnlearnWords: List<Word> = dictionary.filter { it.correctAnswersCount < unlearnWords }
        if (dictionaryUnlearnWords.isEmpty()) return null
        var translateWords = dictionaryUnlearnWords.shuffled().take(countOfQuestionWords)

        if (dictionaryUnlearnWords.size < countOfQuestionWords) {
            val dictionaryLearnedWords = dictionary.filter { it.correctAnswersCount >= unlearnWords }
            translateWords = dictionaryUnlearnWords + dictionaryLearnedWords.shuffled()
                .take(countOfQuestionWords - dictionaryUnlearnWords.size)
        }

        val unlearnWord = dictionaryUnlearnWords.random()
        question = Question(
            variants = translateWords,
            correctAnswer = unlearnWord,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
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


}

