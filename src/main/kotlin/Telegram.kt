import TelegramBotService.Companion.CALLBACK_DATA_ANSWER_PREFIX
import TelegramBotService.Companion.LEARN_WORDS
import TelegramBotService.Companion.RESET_CLICKED
import TelegramBotService.Companion.START
import TelegramBotService.Companion.STATISTICS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String? = null,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyBoard>>,
)

@Serializable
data class InlineKeyBoard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0L
    val telegramBotService = TelegramBotService(botToken)
    val json = Json { ignoreUnknownKeys = true }
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {

        Thread.sleep(2000)

        val result: Result<String> = runCatching { telegramBotService.getUpdates(updateId) }
        if (result.isFailure) continue
        val responseString = result.getOrDefault("")
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, telegramBotService, trainers, responseString) }
        updateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdate(
    update: Update,
    json: Json,
    telegramBotService: TelegramBotService,
    trainers: HashMap<Long, LearnWordsTrainer>,
    responseString: String
) {

    val text = update.message?.text
    val numberChatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(numberChatId) { LearnWordsTrainer("$numberChatId.txt") }

    if (text?.lowercase() == START) {
        telegramBotService.sendMenu(json, numberChatId)
    }

    if (data?.lowercase() == LEARN_WORDS) {
        telegramBotService.checkNextQuestionAndSend(json, trainer.getNextQuestion(), numberChatId)
    }

    if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
        val indexWord = responseString.substringAfterLast(CALLBACK_DATA_ANSWER_PREFIX).substringBefore('"')
        println(indexWord)

        if (trainer.checkAnswer(indexWord.toInt())) {
            telegramBotService.sendMessage(json, numberChatId, "Правильно")
        } else {
            telegramBotService.sendMessage(
                json,
                numberChatId,
                "Не правильно: ${trainer.question?.correctAnswer?.wordEnglish} - " +
                        "${trainer.question?.correctAnswer?.wordRussian}"
            )
        }
        telegramBotService.checkNextQuestionAndSend(json, trainer.getNextQuestion(), numberChatId)

    }

    val wordForStatistics = trainer.getStatistics()
    val textStatistics = "\"Выучено ${wordForStatistics.countLearnWord} из " +
            "${wordForStatistics.countWordInDictionary} слов" +
            " | ${
                wordForStatistics.countLearnWord.toDouble() /
                        wordForStatistics.countWordInDictionary * 100
            }%\""

    if (data?.lowercase() == STATISTICS) {
        telegramBotService.sendMessage(json, numberChatId, textStatistics)
    }

    if (data == RESET_CLICKED) {
        trainer.resetProgress()
        telegramBotService.sendMessage(json, numberChatId, "Прогресс сброшен")
    }
}