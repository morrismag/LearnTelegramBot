import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$URL_API_TELEGRAM$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(json: Json, chatId: Long, messageText: String): String {
        val urlSendMessage = "$URL_API_TELEGRAM$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = messageText,
        )
        val requestBodyString = json.encodeToString(requestBody)
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(json: Json, chatId: Long): String {
        val urlSendMessage = "$URL_API_TELEGRAM$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyBoard(text = "Изучать слова", callbackData = LEARN_WORDS),
                        InlineKeyBoard(text = "Статистика", callbackData = STATISTICS),
                    ),
                    listOf(
                        InlineKeyBoard(text = "Сбросить прогресс", callbackData = RESET_CLICKED),
                    )
                )
            )
        )

        val requestBodyString = json.encodeToString(requestBody)
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    private fun sendQuestion(json: Json, chatId: Long, inputQuestion: Question): String {
        val urlSendMessage = "$URL_API_TELEGRAM$botToken/sendMessage"

        val listOfInlineKeyboard: MutableList<List<InlineKeyBoard>> = inputQuestion.variants.mapIndexed { index, word ->
            listOf(
                InlineKeyBoard(
                    text = word.wordRussian, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
                )
            )
        }.toMutableList()
        listOfInlineKeyboard.add(listOf(InlineKeyBoard(text = "Вернуться в меню", callbackData = START)))

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = inputQuestion.correctAnswer.wordEnglish,
            replyMarkup = ReplyMarkup(
                listOfInlineKeyboard
            )
        )

        val requestBodyString = json.encodeToString(requestBody)
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun checkNextQuestionAndSend(json: Json, question: Question?, numberChatID: Long) {
        if (question != null) {
            sendQuestion(json, numberChatID, question)
        } else {
            sendMessage(json, numberChatID, "Вы выучили все слова в базе.")
        }
    }

    companion object {
        const val LEARN_WORDS = "learn_words_clicked"
        const val STATISTICS = "statistics_clicked"
        const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
        const val URL_API_TELEGRAM = "https://api.telegram.org/bot"
        const val RESET_CLICKED = "reset_clicked"
        const val START = "/start"
    }
}

