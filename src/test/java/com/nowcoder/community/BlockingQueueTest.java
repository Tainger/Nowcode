package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTest {
    public static void main(String[] args) {
        BlockingQueue blockingQueue= new ArrayBlockingQueue(10);
        new Thread(new Producer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();

    }
}

class Producer implements Runnable{
    private BlockingQueue blockingQueue;

    public Producer(BlockingQueue blockingQueue){
        this.blockingQueue = blockingQueue;
    }
    @Override
    public void run() {
        for (int i=0;i<100;i++){
            try {
                blockingQueue.put(i);
                Thread.sleep(new Random().nextInt(2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"put"+i+"进去了");
        }
    }
}
class Consumer implements Runnable{
    private BlockingQueue blockingQueue;

    public Consumer(BlockingQueue blockingQueue){
        this.blockingQueue = blockingQueue;
    }
    @Override
    public void run() {

        for (int i=0;i<100;i++){
            try {
                blockingQueue.take();
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"take"+i+"走了");
        }
    }
}
