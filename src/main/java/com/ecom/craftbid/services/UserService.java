package com.ecom.craftbid.services;

import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.dtos.UserDTO;
import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.dtos.ListingDTO;
import com.ecom.craftbid.dtos.UserDTO;
import com.ecom.craftbid.entities.user.Profile;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.exceptions.NotFoundException;
import com.ecom.craftbid.repositories.ListingRepository;
import com.ecom.craftbid.repositories.ProfileRepository;
import com.ecom.craftbid.repositories.UserRepository;
import com.ecom.craftbid.utils.TokenParser;
import com.ecom.craftbid.utils.PhotosManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class UserService {
    @Value("${secureTokenSIgnKey}")
    private String SECRET_KEY;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ProfileRepository profileRepository;

    protected User findUserById(long userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOS = UserDTO.fromUsers(users);

        for (UserDTO userDTO : userDTOS) {
            setUserDTOListings(userDTO);
            setUserDTOWorkedIn(userDTO);
        }

        return userDTOS;
    }

    public Page<User> getAllUsersAdmin(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUser(long id) {
        return findUserById(id);
    }

    public UserDTO findById(long id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        UserDTO userDTO = UserDTO.fromUser(user);

        setUserDTOListings(userDTO);
        setUserDTOWorkedIn(userDTO);

        return userDTO;
    }

    public UserDTO createUser(User user) {
        User createdUser = userRepository.save(user);
        UserDTO userDTO = UserDTO.fromUser(createdUser);

        setUserDTOListings(userDTO);
        setUserDTOWorkedIn(userDTO);

        return userDTO;
    }

    public void deleteUser(long id) {

        userRepository.deleteById(id);
    }

    public UserDTO updateUserPassword(long id, String password) {
        User user = findUserById(id);

        user.setPassword(password);
        userRepository.save(user);
        UserDTO userDTO = UserDTO.fromUser(user);

        setUserDTOListings(userDTO);
        setUserDTOWorkedIn(userDTO);

        return userDTO;
    }

    public Long getMyId(String jwtToken) {
        String email = TokenParser.getEmailFromToken(jwtToken, SECRET_KEY);
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
        return user.getId();
    }

    private void setUserDTOListings(UserDTO userDTO) {
        List<Listing> listings = listingRepository.findByAdvertiserId(userDTO.getId(), Pageable.unpaged()).getContent();
        userDTO.setListings(listings);
    }

    private void setUserDTOWorkedIn(UserDTO userDTO) {
        userDTO.setWorkedIn(getFinishedListingsCount(userDTO));
    }

    private long getFinishedListingsCount(UserDTO userDTO) {
        if (userDTO.getListings() == null) {
            return 0;
        }

        long count = 0;
        List<Listing> listings = listingRepository.findByAdvertiserId(userDTO.getId(), Pageable.unpaged()).getContent();
        for (Listing listing : listings) {
            if (listing.getEnded() != null && listing.getEnded()) {
                count++;
            }
        }

        return count;
    }

    public UserDTO addUserAvatar(long userId, MultipartFile photo) {
        User user = findUserById(userId);
        String addedPhoto = PhotosManager.saveUserAvatar(photo, userId);

        Profile profile = user.getProfile();
        profile.setAvatarUri(addedPhoto);
        user.setProfile(profile);

        userRepository.save(user);
        return UserDTO.fromUser(user);
    }

    public UserDTO removeUserAvatar(long userId, String photoPath) {
        throw new RuntimeException("Not implemented");
    }
}
