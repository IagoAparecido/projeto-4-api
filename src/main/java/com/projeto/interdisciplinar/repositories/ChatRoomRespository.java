package com.projeto.interdisciplinar.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projeto.interdisciplinar.models.ChatRoom;

public interface ChatRoomRespository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(UUID senderId, UUID recipientId);

    // @Query(value = "SELECT * FROM chat_room WHERE sender_id = ?1", nativeQuery =
    // true)
    // List<ChatRoom> findChatRooms(UUID senderId);

}
