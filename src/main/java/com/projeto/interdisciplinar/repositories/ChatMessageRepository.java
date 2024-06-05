package com.projeto.interdisciplinar.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projeto.interdisciplinar.models.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByChatId(UUID s);

    @Query(value = "SELECT content FROM ( SELECT content, ROW_NUMBER() OVER (ORDER BY timestamp DESC) AS row_num FROM chat_message WHERE recipient_id = ?1) AS ranked_messages WHERE ranked_messages.row_num = 1;", nativeQuery = true)
    String getLastMessages(UUID senderId);

}
