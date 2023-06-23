fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService()
    val trainer = LearnWordsTrainer()

    val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    while (true) {

        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(botToken, updateId)
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
            telegramBotService.sendMenu(botToken, numberChatID)
        }

        if (data?.lowercase() == LEARN_WORDS && numberChatID != null) {
            telegramBotService.checkNextQuestionAndSend(trainer, botToken, numberChatID)
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true && numberChatID != null) {
            val indexWord = updates.substringAfterLast(CALLBACK_DATA_ANSWER_PREFIX).substringBefore('"')
            println(indexWord)

            if (trainer.checkAnswer(indexWord.toInt())) {
                telegramBotService.sendMessage(botToken, numberChatID, "Правильно")
            } else {
                telegramBotService.sendMessage(
                    botToken, numberChatID,
                    "Не правильно: ${trainer.question?.correctAnswer?.wordEnglish} - " +
                            "${trainer.question?.correctAnswer?.wordRussian}"
                )
            }
            telegramBotService.checkNextQuestionAndSend(trainer, botToken, numberChatID)
        }

        val textStatistics = "\"Выучено ${trainer.getStatistics().countLearnWord} из " +
                "${trainer.getStatistics().countWordInDictionary} слов" +
                " | ${
                    trainer.getStatistics().countLearnWord.toDouble() /
                            trainer.getStatistics().countWordInDictionary * 100
                }%\""
        if (data?.lowercase() == STATISTICS && numberChatID != null) {
            telegramBotService.sendMessage(botToken, numberChatID, textStatistics)
        }
    }
}

