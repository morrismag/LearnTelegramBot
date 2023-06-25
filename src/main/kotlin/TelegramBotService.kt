import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBotService(private val botToken: String) {

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$URL_API_TELEGRAM$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: Int, messageText: String): String {
        val encoded = URLEncoder.encode(
            messageText,
            StandardCharsets.UTF_8
        )
        println(encoded)
        val urlSendMessage = "$URL_API_TELEGRAM$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: Int): String {
        val urlSendMessage = "$URL_API_TELEGRAM$botToken/sendMessage"
        val sendMenuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Основное меню",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "Изучить слова",
            					"callback_data": "$LEARN_WORDS"

            				},
            				{
            					"text": "Статистика",
            					"callback_data": "$STATISTICS"

            				}
            			]
            		]
            	}
            }
        """.trimIndent()

        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    private fun sendQuestion(chatId: Int, inputQuestion: Question): String {
        val urlSendMessage = "$URL_API_TELEGRAM$botToken/sendMessage"
        val sendMessageBody = """
            {
            	"chat_id": $chatId,
            	"text": "${inputQuestion.correctAnswer.wordEnglish}",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "${inputQuestion.variants[0].wordRussian}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 0}"

            				},
            				{
            					"text": "${inputQuestion.variants[1].wordRussian}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 1}"

            				},
            				{
            					"text": "${inputQuestion.variants[2].wordRussian}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 2}"

            				},
            				{
            					"text": "${inputQuestion.variants[3].wordRussian}",
            					"callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 3}"

            				}                        
            			]
            		]
            	}
            }
        """.trimIndent()

        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMessageBody))
            .build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun checkNextQuestionAndSend(question: Question?, numberChatID: Int) {
        if (question != null) {
            sendQuestion(numberChatID, question)
        } else {
            sendMessage(numberChatID, "Вы выучили все слова в базе.")
        }
    }

    companion object {
        const val LEARN_WORDS = "learn_words_clicked"
        const val STATISTICS = "statistics_clicked"
        const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
        const val URL_API_TELEGRAM = "https://api.telegram.org/bot"
    }
}

