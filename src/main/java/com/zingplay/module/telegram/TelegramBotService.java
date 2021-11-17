package com.zingplay.module.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.util.List;
//https://api.telegram.org/bot1624746857:AAFH2ygS6FlmH5qhcWifQnR_ScjbgONZ6w4/getUpdates
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    public static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    @Value("${botTele.token}")
    private String token;

    @Value("${botTele.username}")
    private String username;

    @Value("${botTele.groupChatId}")
    public long groupChatId;
    @Value("${app.name}")
    public String mode;
    @Value("${botTele.tagUsers}")
    public String tagUsers;


    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", username, token);
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    // @PostConstruct
    // public void registerBot() {
    //      try {
    //          TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    //          botsApi.registerBot(this);
    //          sendInfo("cluster run");
    //      } catch (TelegramApiException e) {
    //          e.printStackTrace();
    //      }
    // }
    public void sendInfo(String text){
        sendMessage(String.format("\uD83C\uDF40 <i>%s</i>\n<b>%s</b>", mode, text));
    }
    public void sendWarning(String text){
        sendMessage(String.format("⚠️ <i>%s</i>\n<b>%s</b>", mode, text));
    }
    public void sendError(String text){
        sendMessage(String.format("\uD83C\uDD98 <i>%s</i>\n<b>%s</b>\n%s", mode, text, tagUsers));
    }
    private void sendMessage(String msg){
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(groupChatId));
        response.setText(msg);
        response.setParseMode("HTML");
//        try {
//            execute(response);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onUpdateReceived(Update update) {
//        if (update.hasMessage()){
//            Message message = update.getMessage();
//            System.out.println("message : " + message);
//        }
    }
}
