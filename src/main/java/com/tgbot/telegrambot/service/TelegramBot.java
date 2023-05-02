package com.tgbot.telegrambot.service;

import com.tgbot.telegrambot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    static final String HELP_TEXT = "This bot was created as a debut project" +
            ", as well as to hone skills of interaction with Telegram libraries \n\n" +
            "You can execute commands from the main menu on the left down corner or typing a command: \n" +
            "Type /start to see welcome message\n" +
            "Type /mydata to see data stored about yourself\n" +
            "Type /deletedata to delete data from stored\n" +
            "Type /settings to set your preference\n" +
            "Type /help to see this message again";

    public TelegramBot(BotConfig config) {
        this.config = config;
        //список меню
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "start work with your bot"));
        listOfCommands.add(new BotCommand("/help", "get help info"));
        listOfCommands.add(new BotCommand("/mydata", "get my data"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));
        listOfCommands.add(new BotCommand("/settings", "set ur preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands,
                    new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }

    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    //переопределённый метод, реализующий работу бота
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                default:
                    sendMessage(chatId, "Sory, cmnd was not recognized");
            }
        }
    }

    //метод, реализующий приветствие
    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", nice to meet u!";

        log.info("Replied to user " + name);

        sendMessage(chatId, answer);
    }

    //метод, реализующий отправку сообщения
    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
