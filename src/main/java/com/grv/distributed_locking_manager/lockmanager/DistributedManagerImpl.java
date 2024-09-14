package com.grv.distributed_locking_manager.lockmanager;


import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DistributedManagerImpl implements DistributedManager{

    @Autowired
    private RedissonClient redisson;

    long acquireLockTimeout = 90;

    long autoUnlockWait = 120;

//    public DistributedManagerImpl(RedissonClient redisson) {
//        this.redisson = redisson;
//    }

    public boolean acquireLocks(String fenceToken){
        RLock rLock = redisson.getFairLock(fenceToken);
        try{
            return rLock.tryLock(acquireLockTimeout,autoUnlockWait, TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



    public boolean releaseLock(String fenceToken){
        RLock rLock = redisson.getFairLock(fenceToken);

        if (!rLock.isHeldByCurrentThread()){
            log.info("Not locked by current thread not able to unlock");
            return false;
        }
        rLock.unlock();
        return true;
    }
}
