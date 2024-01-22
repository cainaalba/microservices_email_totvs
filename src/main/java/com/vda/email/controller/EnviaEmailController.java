package com.vda.email.controller;

import com.vda.email.dto.DadosEmail;
import com.vda.email.service.EnviaEmailRpsNfseService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class EnviaEmailController {

    @Autowired
    private final EnviaEmailRpsNfseService service;

    public EnviaEmailController(EnviaEmailRpsNfseService service) {
        this.service = service;
    }

    @PostMapping("/rps")
    @Transactional
    public ResponseEntity<?> enviarRpsNfse(@RequestBody DadosEmail dados) {
        return service.enviarRpsNfse(dados);
    }
}
