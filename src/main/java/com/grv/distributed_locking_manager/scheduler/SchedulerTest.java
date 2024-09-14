package com.grv.distributed_locking_manager.scheduler;

import com.grv.distributed_locking_manager.lockmanager.DistributedManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class SchedulerTest {


    private final DistributedManager lockManager;

    private  static List<String> list = new ArrayList<>();

    public SchedulerTest(DistributedManager lockManager) {
        this.lockManager = lockManager;
    }


    static {
        list.add("one");
        list.add("two");
        list.add("three");
    }




    @Scheduled(fixedRate = 1000)
    void schedulerMethod(){
        list.parallelStream().forEach(this::process);
    }

    void  process(String s){
        if (lock("schedulerUnique1-"+s)){
            try {
                performTask(s);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                unlock("schedulerUnique1-"+s);
            }
        }
    }

    boolean lock(String fenceToken){
       return lockManager.acquireLocks(fenceToken);
    }

    boolean unlock(String fenceToken){
        return lockManager.releaseLock(fenceToken);
    }

    void performTask(String task){
        System.out.println("Running billing script for task: " + task);

        // Introduce delay to simulate long-running process
        try {
            System.out.println("Simulating delay in logic script for task: " + task);
            if (task.equals("one"))
                Thread.sleep(5000);
            if (task.equals("two"))
                Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted during logic script for task: " + task);
        }

        // After the delay, continue with logic
        System.out.println("Completed billing for task: " + task);
    }
}
