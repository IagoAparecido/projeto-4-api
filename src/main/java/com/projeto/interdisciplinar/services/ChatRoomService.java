package com.projeto.interdisciplinar.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.models.ChatRoom;
import com.projeto.interdisciplinar.repositories.ChatRoomRespository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRespository chatRoomRespository;

    public Optional<UUID> getChatRoomId(
            UUID senderId, UUID recipientId, boolean createNewRoomIfNotExists) {
        return chatRoomRespository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    private UUID createChatId(UUID senderId, UUID recipientId) {
        var chatId = UUID.randomUUID();
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .status(true)
                .build();

        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .status(true)
                .build();

        chatRoomRespository.save(senderRecipient);
        chatRoomRespository.save(recipientSender);
        return chatId;
    }

    public List<ChatRoom> getAllChatRoomsByUserId(UUID userId) {
        var rooms = chatRoomRespository.findChatRooms(userId);
        return rooms;
    }

    public ChatRoom removeChatRoom(UUID senderId, UUID roomId) throws BadRequestException {
        var room = chatRoomRespository.findChatRoom(roomId);

        if (room.getSenderId().equals(senderId)) {
            room.setStatus(false);
            return this.chatRoomRespository.save(room);
        } else {
            throw new BadRequestException("Você não tem permissão para remover essa conversa.");
        }
    }
}
