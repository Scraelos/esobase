/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
 * @author guest
 */
public class InsertExecutor extends ThreadPoolTaskExecutor{

    public InsertExecutor() {
        this.setCorePoolSize(200);
        this.setMaxPoolSize(300);
        this.setWaitForTasksToCompleteOnShutdown(true);
    }
    
}
