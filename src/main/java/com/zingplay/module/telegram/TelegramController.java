package com.zingplay.module.telegram;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramController {

    @Value("${zingplay.control.api}")
    private String controlApi;

    private static final String GET_SOURCE_EXCEPTION = "Exception";

    private static TelegramController instance;
    private final TelegramBotService telegramBotService;

    private static final HashMap<String, MyPair<String, Long>> lastMessage = new HashMap<>();
    private static final Map<TelegramConst, Integer> timeDelay = new HashMap<>();

    static {
        int delay = 24 * 60 * 60 * 1000;
        timeDelay.put(TelegramConst.CHECK_NUM_LOG_SOCKET, delay);
        timeDelay.put(TelegramConst.NO_OFFER_RUNNING, delay);
    }

    public static TelegramController getInstance() {
        return instance;
    }

    @Autowired
    public TelegramController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
        instance = this;
    }

    private boolean checkCanSend(TelegramConst telegramConst, String game, String country, String msg) {
        String typeMessage = telegramConst.name() + "_" + game + "_" + country;
        MyPair<String, Long> orDefault = lastMessage.getOrDefault(typeMessage, new MyPair<>("", 0L));
        long l = System.currentTimeMillis();
        if (l >= (orDefault.getSecond() + timeDelay.getOrDefault(telegramConst, 0))) {
            lastMessage.put(typeMessage, new MyPair<>(msg, l));
            return true;
        }
        return false;
    }

    public void sendInfo(TelegramConst telegramConst, String game, String country, String msg) {
        if (checkCanSend(telegramConst, game, country, msg)) {
            telegramBotService.sendInfo(String.format("%s\n%s", telegramConst.name(), msg));
        }
    }

    public void sendWarning(TelegramConst telegramConst, String game, String country, String msg) {
        if (checkCanSend(telegramConst, game, country, msg)) {
            telegramBotService.sendWarning(String.format("%s\n%s", telegramConst.name(), msg));
        }
    }

    public void sendError(TelegramConst telegramConst, String game, String country, String msg) {
        if (checkCanSend(telegramConst, game, country, msg)) {
            telegramBotService.sendError(String.format("%s\n%s", telegramConst.name(), msg));
        }
    }

    public void checkHealthControlSO() {
        String url = controlApi + "/api/checkHealth";
        try {
            String resource = getURLSource(url);
            if (resource.equals(GET_SOURCE_EXCEPTION)) {
                sendError(TelegramConst.CHECK_HEALTH_CONTROL, "Control", "", "[Control SO] not alive!!!");
            }
        } catch (Exception e) {
            sendError(TelegramConst.CHECK_HEALTH_CONTROL, "Control", "","[Control SO] cant connect!!!");
        }
    }

    public String getURLSource(String url) {
        try {
            URL urlObject = new URL(url);
            URLConnection urlConnection = urlObject.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            return toString(urlConnection.getInputStream());
        } catch (Exception e) {
            System.out.println("getURLSource exception " + e);
        }
        return GET_SOURCE_EXCEPTION;
    }

    private String toString(InputStream inputStream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            return stringBuilder.toString();
        }
    }

}