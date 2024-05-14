package com.projeto.interdisciplinar.controllers;

import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.projeto.interdisciplinar.models.BlacklistModel;
import com.projeto.interdisciplinar.models.ChatMessage;
import com.projeto.interdisciplinar.models.ChatNotification;
import com.projeto.interdisciplinar.models.ChatRoom;
import com.projeto.interdisciplinar.services.BlacklistService;
import com.projeto.interdisciplinar.services.ChatMessageService;
import com.projeto.interdisciplinar.services.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final BlacklistService blacklistService;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage) throws BadRequestException {
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

    @DeleteMapping("/messages/{senderId}/{messageId}")
    public ResponseEntity<ChatMessage> remove(@PathVariable UUID messageId, @PathVariable UUID senderId)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.chatMessageService.removeMessage(senderId, messageId));
    }

    @GetMapping("/messages/{userId}")
    public ResponseEntity<List<ChatRoom>> findChatRoom(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatRoomService.getAllChatRoomsByUserId(userId));
    }

    @PatchMapping("/messages/{senderId}/{roomId}")
    public ResponseEntity<ChatRoom> removeChatRoom(@PathVariable UUID senderId, @PathVariable UUID roomId)
            throws BadRequestException {
        return ResponseEntity.ok(chatRoomService.removeChatRoom(senderId, roomId));
    }

    @PostMapping("/messages/block")
    public ResponseEntity<BlacklistModel> blockUser(@RequestParam UUID id) throws BadRequestException {
        return ResponseEntity.ok(blacklistService.block(id));
    }

}
