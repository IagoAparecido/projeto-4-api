package com.projeto.interdisciplinar.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.projeto.interdisciplinar.models.ChatMessage;
import com.projeto.interdisciplinar.models.ChatNotification;
import com.projeto.interdisciplinar.models.ChatRoom;
import com.projeto.interdisciplinar.models.PostsModel;
import com.projeto.interdisciplinar.services.ChatMessageService;
import com.projeto.interdisciplinar.services.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage) {
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId().toString(), "/queue/messages",
                ChatNotification.builder()
                        .id(savedMsg.getId())
                        .senderId(savedMsg.getSenderId())
                        .recipientId(savedMsg.getRecipientId())
                        .content(savedMsg.getContent())
                        .build());
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable("senderId") UUID senderId,
            @PathVariable("recipientId") UUID recipientId) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    // @GetMapping("/messages/{userId}")
    // public ResponseEntity<List<ChatRoom>> findChatRoom(@PathVariable UUID userId)
    // {
    // return ResponseEntity.ok(chatRoomService.getAllChatRoomsByUserId(userId));
    // }

}
