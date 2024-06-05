package com.projeto.interdisciplinar.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.models.ChatMessage;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.BlacklistRepository;
import com.projeto.interdisciplinar.repositories.ChatMessageRepository;
import com.projeto.interdisciplinar.repositories.ChatRoomRespository;

import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository repository;
    private final ChatRoomRespository roomRespository;
    private final ChatRoomService chatRoomService;
    private final BlacklistRepository blacklistRepository;

    // criar mensagem
    public ChatMessage save(ChatMessage chatMessage) throws BadRequestException {

        var chatId = chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow();

        var isBlocked = blacklistRepository.alreadyExist(chatMessage.getRecipientId(), chatMessage.getSenderId());

        if (isBlocked != null) {
            throw new BadRequestException("Usuário não autorizado a realizar essa operação.");

        }

        var room = roomRespository.findChatId(chatId, chatMessage.getSenderId());
        var room2 = roomRespository.findChatId(chatId, chatMessage.getRecipientId());

        if (room.getStatus().equals(false) || room2.getStatus().equals(false)) {
            room.setStatus(true);
            room2.setStatus(true);

            roomRespository.save(room);
            roomRespository.save(room2);
        }

        room.setLastMessage(chatMessage.getContent());
        room2.setLastMessage(chatMessage.getContent());

        roomRespository.save(room);
        roomRespository.save(room2);

        chatMessage.setChatId(chatId);
        chatMessage.setTimestamp(LocalDateTime.now());

        repository.save(chatMessage);

        return chatMessage;
    };

    // get das mensagens
    public List<ChatMessage> findChatMessages(UUID senderId, UUID recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);

        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    };

    // apagar mensagem
    public ChatMessage removeMessage(UUID senderId, UUID messageId) throws BadRequestException {
        var message = repository.findById(messageId);

        if (message.get().getSenderId().equals(senderId)) {
            this.repository.deleteById(messageId);
            return null;
        } else {
            throw new BadRequestException("Você não tem permissão para excluir essa mensagem.");
        }
    }

}
