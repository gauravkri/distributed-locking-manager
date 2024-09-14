package com.grv.distributed_locking_manager.serialno;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document("custom_serial_config")
public class CustomSerialConfig {

    @Id
    String id;

    String name;

    @Builder.Default
    Long counter=0L;

    @Builder.Default
    Integer length=3;

    String prefix;

    String suffix;

}
