package com.serikscode;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingPongController {
    record PingPong(String result){};

    @GetMapping("/ping")
    public PingPong getPingPong(){
        return new PingPong("Pong");
    }

    @GetMapping("/ping123")
    public PingPong getPingPong123(){
        return new PingPong("Pong");
    }
}
