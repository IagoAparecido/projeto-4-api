package com.projeto.interdisciplinar.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.models.BlacklistModel;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.BlacklistRepository;
import com.projeto.interdisciplinar.repositories.UserRepository;

@Service
public class BlacklistService {

    private BlacklistRepository blacklistRepository;
    private UserRepository userRepository;

    public BlacklistService(BlacklistRepository blacklistRepository, UserRepository userRepository) {
        this.blacklistRepository = blacklistRepository;
        this.userRepository = userRepository;
    }

    public BlacklistModel block(UUID userBlock) throws BadRequestException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = ((UsersModel) authentication.getPrincipal()).getId();
        var user = this.userRepository.getReferenceById(authenticatedUserId);

        Optional<UsersModel> userToBlock = this.userRepository.findById(userBlock);
        if (userToBlock.isEmpty()) {
            throw new BadRequestException("Usuário com id " + userBlock + " não encontrado");
        }

        BlacklistModel alreadyBlocked = this.blacklistRepository.alreadyExist(user.getId(), userToBlock.get().getId());
        if (alreadyBlocked != null) {
            throw new BadRequestException("Usuário já bloqueado!");
        }

        BlacklistModel list = new BlacklistModel();

        LocalDateTime createdAt = LocalDateTime.now();

        list.setCreatedAt(createdAt);
        list.setUser(user);
        list.setUserBlocked(userToBlock.get());

        list.setCreatedAt(createdAt);

        return this.blacklistRepository.save(list);

    }

}
