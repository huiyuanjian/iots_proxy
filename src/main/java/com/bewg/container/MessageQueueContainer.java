package com.bewg.container;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueueContainer {

    private MessageQueueContainer() {
    }

    private static MessageQueueContainer instance = new MessageQueueContainer();

    public static MessageQueueContainer getInstance() {
        return instance;
    }

    private static Queue<List<List<Byte>>> msgQueue = new ConcurrentLinkedQueue<>();

    /**
     * 入队
     */
    public void enqueue(List<List<Byte>> item) {
        msgQueue.add(item);
    }

    /**
     * 出队不移出队列
     */
    public List<List<Byte>> tryPeek() {
        return msgQueue.peek();
    }

    /**
     * 出队移出队列
     */
    public List<List<Byte>> tryDequeue() {
        return msgQueue.poll();
    }

    public static Queue<List<List<Byte>>> getMsgQueue() {
        return msgQueue;
    }

    public static void setMsgQueue(Queue<List<List<Byte>>> msgQueue) {
        MessageQueueContainer.msgQueue = msgQueue;
    }
}
