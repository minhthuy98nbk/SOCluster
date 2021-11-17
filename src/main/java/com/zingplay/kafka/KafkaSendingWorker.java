package com.zingplay.kafka;

import com.zingplay.log.LogKafka;
import com.zingplay.module.telegram.TelegramConst;
import com.zingplay.module.telegram.TelegramController;
import com.zingplay.scheduler.CoreTaskScheduler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class KafkaSendingWorker {
    private static KafkaSendingWorker instance;
    //private ExecutorService executorService;
    //private boolean started;
    private ArrayBlockingQueue<SOKafkaItemMsg> logQueue;

    public static KafkaSendingWorker getInstance(){
        if(instance == null){
            instance =new KafkaSendingWorker();
        }
        return instance;
    }

    public KafkaSendingWorker() {
        instance = this;
        logQueue = new ArrayBlockingQueue<SOKafkaItemMsg>(100000);

    }

    public void start(){
        CoreTaskScheduler.getInstance().scheduleTask(new WriteWorker(1), 0, 1, TimeUnit.MILLISECONDS);
        //this.executorService = Executors.newFixedThreadPool(2);
        //for(int i = 0; i < 2; i++) {
        //    this.executorService.execute(new WriteWorker(i));
        //}
        //started = true;
    }

    public int getQueueLength() {
        return logQueue.size();
    }

    public int getRemainCapacity(){
        return logQueue.remainingCapacity();
    }


    class WriteWorker implements Runnable {
        WriteWorker(int index) {
            Thread.currentThread().setName("system-offers-kafka-worker-" + index);
        }
        @Override
        public void run() {
            //while (started){
            try {
                SOKafkaItemMsg msg = logQueue.poll();
                write(msg);
            }catch (Exception e){
                e.printStackTrace();
                TelegramController.getInstance().sendWarning(TelegramConst.KAFKA_SENDING, "", "", e.getMessage());
            }
            //}
        }
    }

    public void send(final String topic, final String key, final String message){
        SOKafkaItemMsg msg = new SOKafkaItemMsg(topic, key, message);
        // LogKafka.getInstance().info("Kafka|addSend|{}|{}|{}", topic, key, message);
        //LogSystemAction.getInstance().info("send....." + topic);
        try {
            logQueue.add(msg);
        }catch (Exception e){
            e.printStackTrace();
            TelegramController.getInstance().sendWarning(TelegramConst.KAFKA_ADD_QUEUE, "", "", e.getMessage());
        }
    }
    private void write(SOKafkaItemMsg msg) {
        if(msg == null) return;
        //LogSystemAction.getInstance().info("write....." + msg.topic);
        KafkaService.getInstance().sendMessage(msg.getTopic(),msg.getKey(),msg.getMsg());
    }
}
