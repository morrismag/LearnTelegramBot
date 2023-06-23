import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBotService {

    fun getUpdates(botToken: String, updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(botToken: String, chatId: Int, messageText: String): String {
        val encoded = URLEncoder.encode(
            messageText,
            StandardCharsets.UTF_8
        )
        println(encoded)
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(botToken: String, chatId: Int): String {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
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

    private fun sendQuestion(botToken: String, chatId: Int, inputQuestion: Question): String {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
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

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, botToken: String, numberChatID: Int) {
        val question = trainer.getNextQuestion()
        if (question != null) {
            sendQuestion(botToken, numberChatID, question)
        } else {
            sendMessage(botToken, numberChatID, "Вы выучили все слова в базе.")
        }
    }
}

const val LEARN_WORDS = "learn_words_clicked"
const val STATISTICS = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"