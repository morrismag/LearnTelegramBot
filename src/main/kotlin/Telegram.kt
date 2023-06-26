import TelegramBotService.Companion.CALLBACK_DATA_ANSWER_PREFIX
import TelegramBotService.Companion.LEARN_WORDS
import TelegramBotService.Companion.STATISTICS

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {

        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(updateId)
        println(updates)

        val numberUpdateID = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        updateId = numberUpdateID + 1
        println(numberUpdateID)
        val numberChatID = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toInt()
        println(numberChatID)
        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        println(text)
        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (text?.lowercase() == "/start" && numberChatID != null) {
            telegramBotService.sendMenu(numberChatID)
        }

        if (data?.lowercase() == LEARN_WORDS && numberChatID != null) {
            telegramBotService.checkNextQuestionAndSend(trainer.getNextQuestion(), numberChatID)
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true && numberChatID != null) {
            val indexWord = updates.substringAfterLast(CALLBACK_DATA_ANSWER_PREFIX).substringBefore('"')
            println(indexWord)

            if (trainer.checkAnswer(indexWord.toInt())) {
                telegramBotService.sendMessage(numberChatID, "Правильно")
            } else {
                telegramBotService.sendMessage(
                    numberChatID,
                    "Не правильно: ${trainer.question?.correctAnswer?.wordEnglish} - " +
                            "${trainer.question?.correctAnswer?.wordRussian}"
                )
            }
            telegramBotService.checkNextQuestionAndSend(trainer.getNextQuestion(), numberChatID)
        }

        val wordForStatistics = trainer.getStatistics()
        val textStatistics = "\"Выучено ${wordForStatistics.countLearnWord} из " +
                "${wordForStatistics.countWordInDictionary} слов" +
                " | ${
                    wordForStatistics.countLearnWord.toDouble() /
                            wordForStatistics.countWordInDictionary * 100
                }%\""

        if (data?.lowercase() == STATISTICS && numberChatID != null) {
            telegramBotService.sendMessage(numberChatID, textStatistics)
        }
    }
}

