package com.example.voice_chat_server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VoiceHandler voiceHandler;

    public WebSocketConfig(VoiceHandler voiceHandler) {
        this.voiceHandler = voiceHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(voiceHandler, "/voice/{room}")
                .setAllowedOrigins("*");

    }


    
}