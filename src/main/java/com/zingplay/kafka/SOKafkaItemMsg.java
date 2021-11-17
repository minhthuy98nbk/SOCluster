package com.zingplay.kafka;

public class SOKafkaItemMsg {
    public String topic;
    public String key;
    public String msg;

    SOKafkaItemMsg(String topic, String key, String msg) {
        this.topic = topic;
        this.key = key;
        this.msg = msg;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
