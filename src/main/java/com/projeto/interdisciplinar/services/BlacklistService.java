package com.projeto.interdisciplinar.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.models.BlacklistModel;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.BlacklistRepository;
import com.projeto.interdisciplinar.repositories.ChatRoomRespository;
import com.projeto.interdisciplinar.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(BlacklistService.class);

    private BlacklistRepository blacklistRepository;
    private UserRepository userRepository;
    private ChatRoomRespository chatRoomRespository;
    private ChatRoomService chatRoomService;

    public BlacklistService(BlacklistRepository blacklistRepository, UserRepository userRepository,
            ChatRoomService chatRoomService,
            ChatRoomRespository chatRoomRespository) {
        this.blacklistRepository = blacklistRepository;
        this.userRepository = userRepository;
        this.chatRoomRespository = chatRoomRespository;
        this.chatRoomService = chatRoomService;

    }

    // bloquear usuários
    public BlacklistModel block(UUID userBlock) throws BadRequestException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = ((UsersModel) authentication.getPrincipal()).getId();
        var user = this.userRepository.getReferenceById(authenticatedUserId);

        Optional<UsersModel> userToBlock = this.userRepository.findById(userBlock);
        if (userToBlock.isEmpty()) {
            throw new BadRequestException("Usuário com id " + userBlock + " não encontrado");
        }

        BlacklistModel list = new BlacklistModel();

        var chatId = chatRoomService.getChatRoomId(user.getId(), userBlock, true)
                .orElseThrow();
        var room = chatRoomRespository.findChatId(chatId, user.getId());

        BlacklistModel alreadyBlocked = this.blacklistRepository.alreadyExist(user.getId(), userToBlock.get().getId());
        if (alreadyBlocked != null) {
            this.blacklistRepository.deleteById(alreadyBlocked.getId());
            room.setIsBlocked(false);
            this.chatRoomRespository.save(room);
            return null;
        }

        LocalDateTime createdAt = LocalDateTime.now();

        room.setIsBlocked(true);
        this.chatRoomRespository.save(room);

        list.setCreatedAt(createdAt);
        list.setUser(user);
        list.setUserBlocked(userToBlock.get());
        list.setCreatedAt(createdAt);

        return this.blacklistRepository.save(list);
    }

    // get dos usuário bloqueados
    public List<BlacklistModel> findUsersBlocked() throws BadRequestException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        // Debugging the type of principal
        logger.info("Principal class: {}", principal.getClass().getName());

        if (principal instanceof UsersModel) {
            UUID authenticatedUserId = ((UsersModel) principal).getId();
            var user = this.userRepository.getReferenceById(authenticatedUserId);
            return this.blacklistRepository.findByUser(user.getId());
        } else {
            throw new BadRequestException("Invalid user principal");
        }

    }

}
