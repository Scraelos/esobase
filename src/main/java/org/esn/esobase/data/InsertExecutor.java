/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esn.esobase.data;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 *
 * @author guest
 */
@Service
public class InsertExecutor extends ThreadPoolTaskExecutor {

    public InsertExecutor() {
        this.setCorePoolSize(4);
        this.setMaxPoolSize(8);
        this.setWaitForTasksToCompleteOnShutdown(true);
    }

}
