package com.vda.email.controller;

import com.vda.email.dto.DadosEmail;
import com.vda.email.service.EnviaEmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnviaEmailController {

    private final EnviaEmailService service;

    public EnviaEmailController(EnviaEmailService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> enviar(@RequestBody DadosEmail dados) {
        try {
            return service.enviar(dados);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
