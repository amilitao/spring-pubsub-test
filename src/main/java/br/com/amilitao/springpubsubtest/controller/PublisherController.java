package br.com.amilitao.springpubsubtest.controller;


import br.com.amilitao.springpubsubtest.controller.dto.LoteMensagemDto;
import br.com.amilitao.springpubsubtest.controller.dto.MensagemDto;
import br.com.amilitao.springpubsubtest.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
public class PublisherController {

    @Autowired
    private PublisherService publisher;

    @PostMapping("/publish")
    public ResponseEntity<Void> publishMessage(@RequestBody MensagemDto msg) {

        publisher.publish(msg);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/publish/batch")
    public ResponseEntity<Void> publishBatch(@RequestBody LoteMensagemDto lote) throws IOException, ExecutionException, InterruptedException {

        publisher.publishWithBatchSettings(lote);

        return ResponseEntity.ok().build();
    }


}
