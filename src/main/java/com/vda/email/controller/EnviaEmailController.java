package com.vda.email.controller;

import com.vda.email.dto.DadosEmailDto;
import com.vda.email.service.EnviaEmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class EnviaEmailController {
    @Autowired
    private final EnviaEmailService service;

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

    @PostMapping("/coleta")
    public ResponseEntity<?> enviarColeta(@RequestParam("json") String dados,
                                          @RequestParam("file") MultipartFile arquivo) {
        try {
            service.enviaEmailColeta(dados, arquivo);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
