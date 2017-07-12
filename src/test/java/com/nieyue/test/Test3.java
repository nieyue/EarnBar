package com.nieyue.test;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.lowagie.text.List;

public class Test3 {
 public static void main(String[] args) throws Exception {
	// BlockingQueue<Runnable> bq=new ArrayBlockingQueue<Runnable>(10);
	 testBlockingQueue();
}
 
 static void testBlockingQueue () throws InterruptedException{

     BlockingQueue queue = new ArrayBlockingQueue(1024);  

     Producer producer = new Producer(queue);  
     Consumer consumer = new Consumer(queue);  

     new Thread(producer).start();  
     new Thread(consumer).start();  

     Thread.sleep(4000); 
 }
 
}
class Producer implements Runnable{  
	  
    protected BlockingQueue queue = null;  
  
    public Producer(BlockingQueue queue) {  
        this.queue = queue;  
    }  
  
    public void run() { 
    	
        try {  
        	System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
           System.out.println(System.currentTimeMillis());
        	ArrayList<Object> l=new ArrayList<Object> ();
            for (int i = 0; i < 1000; i++) {
            	l.add(l.add(new Object()));
			}
        	queue.put(l);  
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            Thread.sleep(1000);  
            System.out.println(System.currentTimeMillis());
            queue.put("2");  
            Thread.sleep(1000);  
            queue.put("3");  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
}  

class Consumer implements Runnable{  
	  
    protected BlockingQueue queue = null;  
  
    public Consumer(BlockingQueue queue) {  
        this.queue = queue;  
    }  
  
    public void run() {  
        try {  
            System.out.println(queue.take());  
            System.out.println(queue.take());  
            System.out.println(queue.take());  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
} 
