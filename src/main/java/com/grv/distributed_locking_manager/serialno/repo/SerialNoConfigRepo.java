package com.grv.distributed_locking_manager.serialno.repo;

import com.grv.distributed_locking_manager.serialno.CustomSerialConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SerialNoConfigRepo extends MongoRepository<CustomSerialConfig, String> {

    Optional<CustomSerialConfig> findByName(String name);
}
