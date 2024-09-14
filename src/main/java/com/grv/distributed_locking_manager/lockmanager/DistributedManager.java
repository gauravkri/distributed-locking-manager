package com.grv.distributed_locking_manager.lockmanager;

public interface DistributedManager {

    boolean acquireLocks(String fenceToken);

    boolean releaseLock(String fenceToken);
}
