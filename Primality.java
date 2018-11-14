/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject1;

/**
 *
 * @author Diana Zhanseit
 */
import org.apache.commons.math3.primes.Primes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Primality {
    static SimpleDateFormat sdf = null;
    
    public static void main(String[] args) {
        sdf = new SimpleDateFormat("HH:mm:ss.S");
        
        CountDownLatch latch0 = new CountDownLatch(4);
        CountDownLatch latch1 = new CountDownLatch(4);
        CountDownLatch latch2 = new CountDownLatch(4);
        CountDownLatch latch3 = new CountDownLatch(4);
        
        ExecutorService service = Executors.newFixedThreadPool(4);
        
        System.out.println("Running multiple threads " + sdf.format(new Date()));
        Future future0 = service.submit(new MyCallable(0,2500000,latch0));
        Future future1 = service.submit(new MyCallable(2500000, 5000000, latch1));
        Future future2 = service.submit(new MyCallable(5000000, 7500000, latch2));
        Future future3 = service.submit(new MyCallable(7500000, 10000000, latch3));
        
        try {
            latch0.await();
            latch1.await();
            latch2.await();
            latch3.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(Primality.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            future0.get();
            future1.get();
            future2.get();
            future3.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(Primality.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Primality.class.getName()).log(Level.SEVERE, null, ex);
        }
        service.shutdown();
        System.out.println("End running multiple threads " + sdf.format(new Date()));
        
        //Single-thread 
        service = Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(1);
        System.out.println("Single thread" + sdf.format(new Date()));
        Future future = service.submit(new MyCallable(0, 10000000, latch));
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(Primality.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            future.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(Primality.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(Primality.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("End Running single thread" + sdf.format(new Date()));
        service.shutdown();
        
        
    }
    public static class MyCallable implements Callable<Boolean[]> {
       private int begin;
       private int end;
       private CountDownLatch latch;
       
    public MyCallable(int i, int j, CountDownLatch ilatch) {
        begin = i;
        end = j;
        latch = ilatch;
    }

        @Override
        public Boolean[] call() throws Exception {
            Boolean[] result = new Boolean[10000000];
            for(int i = begin; i < end; i++) {
                result[i] = Primes.isPrime(i);
                latch.countDown();                
            }
            System.out.println("Thread from" + begin + "to" + end + "  : " + sdf.format(new Date()));
            return new Boolean[0];
        }    
    }
}
