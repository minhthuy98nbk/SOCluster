package com.zingplay.module.telegram;

public class MyPair<K, V> {
    private K first;
    private V second;

    public MyPair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public void setFirst(K value) {
        first = value;
    }

    public V getSecond() {
        return second;
    }

    public void setSecond(V second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "key: " + first + ", value: " + second;
    }
}
