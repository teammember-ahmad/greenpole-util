/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.greenpole.util.threadfactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Akinwale Agbaje
 */
public class GreenpoleNotifierFactory implements ThreadFactory {
    private final AtomicInteger count = new AtomicInteger(0);
    private final String notifier;
    
    /**
     * Creates new threads according to the specified pool size in the executor.
     * @param notifier the name of the notifier which the threads are created for
     */
    public GreenpoleNotifierFactory(String notifier) {
        this.notifier = notifier;
    }

    @Override
    public Thread newThread(Runnable r) {
        String qualifyname = notifier + "-" + count.incrementAndGet();
        Thread thread = new Thread(r, qualifyname);
        return thread;
    }
}
