package com.ecom.craftbid.services;

import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    protected User findUserById(long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(long id) {
        return findUserById(id);
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
}
