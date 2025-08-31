package com.example.voice_chat_server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class VoiceHandler extends TextWebSocketHandler{
    
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception
    {
        String roomId = getRoomId(session);
        rooms.computeIfAbsent(roomId, id -> new CopyOnWriteArraySet<>()).add(session);
        System.out.println("Connection is Established from " +session.getId()+ " in room "+roomId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception
    {
        String roomId = getRoomId(session);
        String receivedMessage = message.getPayload();
        System.out.print("Received message "+receivedMessage+" from " +session.getId()+" in room "+roomId);
        Set<WebSocketSession> clientsInRoom = rooms.get(roomId);
        for(WebSocketSession client:clientsInRoom)
        {
            if(!client.equals(session))
            {
                try {
                  client.sendMessage(message);   
                } catch (Exception e) {
                    System.err.println("Error sending message to " + client.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
    {
        String roomId = getRoomId(session);
        Set<WebSocketSession> roomSessions = rooms.get(roomId);

        if(roomSessions!=null)
        {
            roomSessions.remove(session);
        if(roomSessions.isEmpty())
        {
            rooms.remove(roomId);
        }
    }
        System.out.println("Connection closed from " + session.getId() + " in room " + roomId);
    }

    private String getRoomId(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/')+1);
    }

    
}
