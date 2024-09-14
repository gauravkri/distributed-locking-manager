package com.grv.distributed_locking_manager.serialno.controller;

import com.grv.distributed_locking_manager.serialno.SerialNoGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/serial")
public class SerialNoController {

    private final SerialNoGenerator service;

    public SerialNoController(SerialNoGenerator service) {
        this.service = service;
    }

    @GetMapping("/")
    public String getSerialNexNo(){
        return service.getNextSerialNo();
    }
}
