package com.vda.email.controller;

import com.vda.email.dto.DadosEmailDto;
import com.vda.email.service.EnviaEmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EnviaEmailController {

    @Autowired
    private final EnviaEmailService service;

    public EnviaEmailController(EnviaEmailService service) {
        this.service = service;
    }

    @PostMapping("/rps")
    @Transactional
    public ResponseEntity<?> enviarRpsNfse(@RequestBody DadosEmailDto dados) {
        service.enviarRpsNfse(dados);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(@RequestBody DadosEmailDto dados) {
        try {
            service.enviaEmail(dados);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }
}
