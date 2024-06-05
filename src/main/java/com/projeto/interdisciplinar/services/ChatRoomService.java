package com.projeto.interdisciplinar.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.models.ChatRoom;
import com.projeto.interdisciplinar.repositories.ChatMessageRepository;
import com.projeto.interdisciplinar.repositories.ChatRoomRespository;
import com.projeto.interdisciplinar.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRespository chatRoomRespository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;

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

        var recipient = this.userRepository.getReferenceById(recipientId);
        var sender = this.userRepository.getReferenceById(senderId);
        var lastMessage = this.chatMessageRepository.getLastMessages(senderId);

        LocalDateTime createdAt = LocalDateTime.now();

        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .status(true)
                .sender(recipient)
                .createdAt(createdAt)
                .lastMessage(lastMessage)
                .build();

        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .status(true)
                .sender(sender)
                .lastMessage(lastMessage)
                .createdAt(createdAt)
                .build();

        chatRoomRespository.save(senderRecipient);
        chatRoomRespository.save(recipientSender);
        return chatId;
    }

    // get das salas de conversas dos usuários
    public List<ChatRoom> getAllChatRoomsByUserId(UUID userId) {
        return chatRoomRespository.findChatRooms(userId);
    }

    // remover as salas de conversas
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
