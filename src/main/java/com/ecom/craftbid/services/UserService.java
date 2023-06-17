package com.ecom.craftbid.services;

import com.ecom.craftbid.dtos.UserDTO;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.UserRepository;
import com.ecom.craftbid.utils.TokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    @Value("${secureTokenSIgnKey}")
    private String SECRET_KEY;

    @Autowired
    private UserRepository userRepository;

    protected User findUserById(long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public Page<User> getAllUsersAdmin(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUser(long id) {
        return findUserById(id);
    }

    public User findById(long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        return user;
    }

    public User createUser(User user) {

        return userRepository.save(user);
    }

    public void deleteUser(long id) {

        userRepository.deleteById(id);
    }

    public User updateUserPassword(long id, String password) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);

        user.setPassword(password);
        return userRepository.save(user);

    }

    public Long getMyId(String jwtToken) {
        String email = TokenParser.getEmailFromToken(jwtToken, SECRET_KEY);
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        return user.getId();
    }
}
