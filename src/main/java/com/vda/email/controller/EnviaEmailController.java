package com.vda.email.controller;

import com.vda.email.dto.DadosEmail;
import com.vda.email.service.EnviaEmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class EnviaEmailController {

    @Autowired
    private final EnviaEmailService service;

    public EnviaEmailController(EnviaEmailService service) {
        this.service = service;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> enviar(@RequestBody DadosEmail dados) throws Exception {
            return service.enviar(dados);
    }
}
