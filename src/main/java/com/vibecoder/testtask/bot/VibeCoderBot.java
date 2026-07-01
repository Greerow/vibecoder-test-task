package com.vibecoder.testtask.bot;

import com.vibecoder.testtask.config.BotProperties;
import com.vibecoder.testtask.service.OpenRouterService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class VibeCoderBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final OpenRouterService openRouterService;

    public VibeCoderBot(
            BotProperties botProperties,
            OpenRouterService openRouterService
    ) {
        this.botProperties = botProperties;
        this.openRouterService = openRouterService;
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        System.out.println("Получено сообщение: " + text);

        if (text.equals("/start")) {

            sendText(chatId,
                    """
                    👋 Привет!

                    Я VibeCoder Bot.

                    Пришли мне сообщение клиента,
                    а я превращу его в карточку заявки.
                    """);

            return;
        }

        try {

            String answer = openRouterService.ask(text);

            sendText(chatId, answer);

        } catch (Exception e) {

            e.printStackTrace();

            sendText(chatId,
                    "⚠️ Не удалось обработать сообщение. Попробуйте позже.");
        }
    }

    private void sendText(Long chatId, String text) {

        SendMessage message = new SendMessage();

        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}