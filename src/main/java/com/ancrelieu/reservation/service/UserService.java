package com.ancrelieu.reservation.service;

import com.ancrelieu.reservation.entity.User;
import com.ancrelieu.reservation.exception.ConflictException;
import com.ancrelieu.reservation.exception.NotFoundException;
import com.ancrelieu.reservation.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Gestion des comptes utilisateurs : inscription, connexion, consultation.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String email, String password, String fullName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Un compte existe déjà avec cet email");
        }
        return userRepository.save(new User(email, password, fullName));
    }

    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> password.equals(user.getPassword()))
                .orElseThrow(() -> new ConflictException("Identifiants invalides"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
    }
}
