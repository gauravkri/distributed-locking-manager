package com.grv.distributed_locking_manager.serialno;


import com.grv.distributed_locking_manager.lockmanager.DistributedManager;
import com.grv.distributed_locking_manager.serialno.repo.SerialNoConfigRepo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class SerialNoGeneratorImpl implements SerialNoGenerator {

    private final SerialNoConfigRepo repo;

    private final DistributedManager lockManager;
    public SerialNoGeneratorImpl(SerialNoConfigRepo repo, DistributedManager lockManager) {
        this.repo = repo;
        this.lockManager = lockManager;
    }

    public String getNextSerialNo(){

        String serialno= "";
        if (lock("uniqueKey")){
            try{
                serialno = processSerialNo();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                releaseLock("uniqueKey");
            }
        }
        return serialno;
    }


    boolean lock(String fenceToken){
       return lockManager.acquireLocks(fenceToken);
    }


    boolean releaseLock(String fenceToken){
        return lockManager.releaseLock(fenceToken);
    }

    String processSerialNo(){
        CustomSerialConfig config = repo.findByName("test")
                .orElseThrow(() -> new RuntimeException("no serial config found!"));

        CustomSerialConfig nextCounter = getNextCounter(config);
        nextCounter.setId(config.getId());
        log.info("saving config {}",nextCounter);
        repo.save(nextCounter);
        String serialNo = nextCounter.getCounter()+"";
        int diffLength = nextCounter.getLength() - serialNo.length();
        String prefixLength = "0".repeat(diffLength);
        String suffix=   nextCounter.getSuffix();
        if (suffix.equals("$year")){
            suffix =  "/"+LocalDate.now().getYear();
        }
        serialNo = nextCounter.getPrefix()+prefixLength+serialNo+suffix;
        return serialNo;
    }

    CustomSerialConfig getNextCounter(CustomSerialConfig config){
        long currCounter = config.getCounter();
        long maxNo = Long.parseLong("9".repeat(config.getLength()));
        if (currCounter>maxNo){
            config.setLength(config.getLength()+1);
        }
        config.setCounter(currCounter+1);
        return config;
    }

    @PostConstruct
    void insertCustomSerialConfig(){
        CustomSerialConfig config = CustomSerialConfig.builder()
                .name("test")
                .prefix("po/")
                .suffix("$year")
                .build();
        if (!isExist()) {
            repo.save(config);
        }
    }


    boolean isExist(){
        return repo.findByName("test").isPresent();
    }
}
