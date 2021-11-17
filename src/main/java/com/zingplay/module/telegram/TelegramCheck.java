package com.zingplay.module.telegram;

import com.zingplay.kafka.KafkaConsumerController;
import com.zingplay.socket.SocketConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;

@Configuration
@EnableScheduling
@EnableAsync
public class TelegramCheck {

    private static final Logger logger = LoggerFactory.getLogger(TelegramCheck.class);

    public static int CONNECTION_TIMEOUT = 8000;
    public static int SOCKET_TIMEOUT = 8000;

//     @Scheduled(fixedDelay = 1000)
//     public void scheduleFixedDelayTask() {
//         System.out.println(
//                 "Fixed delay task - " + System.currentTimeMillis() / 1000);
//     }
//     @Async
//     @Scheduled(fixedRate = 1000)
//     public void scheduleFixedRateTaskAsync() throws InterruptedException {
//         System.out.println(
//                 "Fixed rate task async - " + System.currentTimeMillis() / 1000);
//         Thread.sleep(2000);
//     }

    @Scheduled(fixedDelay = 10000, initialDelay = 2000)
    public void scheduleFixedRateWithInitialDelayTask() {
//        long now = System.currentTimeMillis() / 1000;
//        System.out.println("Fixed rate task with one second initial delay - " + now);
        TelegramController.getInstance().checkHealthControlSO();
    }

//    @Scheduled(fixedDelay = 1000, initialDelay = 0)
    public void fakeLog() {
        try {
            Thread.sleep(1000);
            int[] timeSleep = new int[]{0,1,0,3,1,3,4,7,10};
            Thread.sleep(timeSleep[new Random().nextInt(timeSleep.length)]*1000);
            long time = System.currentTimeMillis()/1000;
            String[] actions = new String[]{
                    SocketConst.ACTION_LOGIN,
                    SocketConst.ACTION_STATS_GAME,
                    SocketConst.ACTION_USER_PAYMENT,
                    SocketConst.ACTION_USER_BUY_OFFER_V2,
                    SocketConst.ACTION_USER_REQUEST_OFFERS};
            String action = actions[new Random().nextInt(actions.length)];
            int userId = new Random().nextInt( 100) + 1000;
            switch (action){
                case SocketConst.ACTION_LOGIN:
                    KafkaConsumerController.getInstance().process(SocketConst.BACK_END_TOPIC,"dev|dev|" + action,
                            "2|" + userId + "|" + time + "|" + time + "|1000|0|" + time);
                    break;
                case SocketConst.ACTION_STATS_GAME:
                    KafkaConsumerController.getInstance().process(SocketConst.BACK_END_TOPIC,"dev|dev|" + action,
                            "2|" + userId + "|123|1|" + time);
                    break;
                case SocketConst.ACTION_USER_PAYMENT:
                    KafkaConsumerController.getInstance().process(SocketConst.BACK_END_TOPIC,"dev|dev|" + action,
                            "2|" + userId + "|123|ru|1|" + time);
                    break;
                case SocketConst.ACTION_USER_BUY_OFFER_V2:
                    KafkaConsumerController.getInstance().process(SocketConst.BACK_END_TOPIC,"dev|dev|" + action,
                            "2|" + userId + "|dfgsdsd3423|ru|123|" + time);
                    break;
                case SocketConst.ACTION_USER_REQUEST_OFFERS:
                    KafkaConsumerController.getInstance().process(SocketConst.BACK_END_TOPIC,"dev|dev|" + action,
                            "2|" + userId + "| " + time + "|ru");
                    break;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
