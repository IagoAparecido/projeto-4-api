package com.projeto.interdisciplinar.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projeto.interdisciplinar.models.ChatRoom;

public interface ChatRoomRespository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(UUID senderId, UUID recipientId);

    @Query(value = "SELECT * FROM chat_room WHERE sender_id = ?1 AND status = true", nativeQuery = true)
    List<ChatRoom> findChatRooms(UUID senderId);

    @Query(value = "SELECT * FROM chat_room WHERE id = ?1", nativeQuery = true)
    ChatRoom findChatRoom(UUID roomId);

    @Query(value = "SELECT * FROM chat_room WHERE chat_id = ?1 AND sender_id = ?2", nativeQuery = true)
    ChatRoom findChatId(UUID chatId, UUID sender_id);

}
