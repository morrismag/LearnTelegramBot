import kotlinx.serialization.Serializable
import java.io.File

class Statistics(
    val countWordInDictionary: Int,
    val countLearnWord: Int,
)

@Serializable
data class Word(
    val wordEnglish: String = "",
    val wordRussian: String = "",
    var correctAnswersCount: Int = 0
)

data class Question(
    var variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    private val fileName: String = "word.txt",
    private val unlearnWords: Int = 3,
    private val countOfQuestionWords: Int = 4,
) {
    var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val countWordInDictionary = dictionary.count()
        val countLearnWord = dictionary.count { it.correctAnswersCount >= unlearnWords }
        return Statistics(countWordInDictionary, countLearnWord)
    }

    fun getNextQuestion(): Question? {
        val dictionaryUnlearnWords: List<Word> = dictionary.filter { it.correctAnswersCount < unlearnWords }
        if (dictionaryUnlearnWords.isEmpty()) return null
        var translateWords = dictionaryUnlearnWords.shuffled().take(countOfQuestionWords)

        val unlearnWord = translateWords.random()

        if (dictionaryUnlearnWords.size < countOfQuestionWords) {
            val dictionaryLearnedWords = dictionary.filter { it.correctAnswersCount >= unlearnWords }
            translateWords = dictionaryUnlearnWords + dictionaryLearnedWords.shuffled()
                .take(countOfQuestionWords - dictionaryUnlearnWords.size)
        }


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
                saveDictionary()
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
            val wordsFile = File(fileName)
            if (!wordsFile.exists()) {
                File("word.txt").copyTo(wordsFile)
            }
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
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary() {
        val wordsFileRewrite = File(fileName)
        wordsFileRewrite.writeText("")

        dictionary.forEach {
            wordsFileRewrite.appendText(
                it.wordEnglish + "|" +
                        it.wordRussian + "|" + it.correctAnswersCount + "\n"
            )
        }
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}

