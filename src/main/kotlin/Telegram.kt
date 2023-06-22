fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService()

    while (true) {

        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(botToken, updateId)
        println(updates)

        val updateIdRegex: Regex = "\"update_id\":(.+?),".toRegex()
        val matchResultUpdateID: MatchResult? = updateIdRegex.find(updates)
        val groupsUpdateID = matchResultUpdateID?.groups
        val numberUpdateID = groupsUpdateID?.get(1)?.value
        println(numberUpdateID)
        if (numberUpdateID != null) {
            updateId = numberUpdateID.toInt() + 1
        }

        val chatIdRegex: Regex = "\"id\":(.+?),".toRegex()
        val matchResultChatId: MatchResult? = chatIdRegex.find(updates)
        val groupsChatId = matchResultChatId?.groups
        val numberChatID = groupsChatId?.get(1)?.value?.toInt()
        println(numberChatID)

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

        var sendMessageUser = ""
        if (numberChatID != null && text != null) {
            sendMessageUser = telegramBotService.sendMessage(botToken, numberChatID, text)
        }
        println(sendMessageUser)
    }
}