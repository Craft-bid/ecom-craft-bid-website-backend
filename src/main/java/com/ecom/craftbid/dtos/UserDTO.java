package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.listing.Listing;
import com.ecom.craftbid.entities.user.PersonalData;
import com.ecom.craftbid.entities.user.Profile;
import com.ecom.craftbid.entities.user.User;
import com.ecom.craftbid.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String image;
    private Role role;
    private String surname;
    private String country;
    private String city;
    private boolean verified;
    private String stars;
    private String phoneNumber;
    private String aboutMe;
    private Date joined;
    private long workedIn;
    private long averageRating;
    List<ListingDTO> listings = new ArrayList<>();

    public static UserDTO fromUser(User user) {
        Profile profile = user.getProfile();
        List<PersonalData> personalDataList = profile != null ? profile.getPersonalData() : null;
        PersonalData personalData = null;
        if (personalDataList != null && personalDataList.size() >= 1)
            personalData = personalDataList.get(0);

        String lastName = personalData != null ? personalData.getLastName() : null;
        String name = personalData != null ? personalData.getFirstName() : null;
        String country = personalData != null ? personalData.getCountry() : null;
        String city = personalData != null ? personalData.getCity() : null;
        Double averageRating = profile != null ? profile.getAverageRating() : null;
        String stars = String.valueOf(profile != null ? profile.getAverageRating() : null);
        String phoneNumber = personalData != null ? personalData.getPhoneNumber() : null;
        String aboutMe = profile != null ? profile.getDescription() : null;
        boolean verified = true;
        Date joined = new Date(); // TODO: get from user.getJoined()

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getProfile() != null ? name : null)
                .email(user.getEmail())
                .image(user.getProfile() != null ? user.getProfile().getAvatarUri() : null)
                .role(user.getRole())
                .surname(user.getProfile() != null ? lastName : null)
                .country(user.getProfile() != null ? country : null)
                .city(user.getProfile() != null ? city : null)
                .verified(verified)
                .stars(stars)
                .phoneNumber(phoneNumber)
                .aboutMe(aboutMe)
                .joined(joined)
                .averageRating(averageRating != null ? averageRating.longValue() : 0)
                .listings(new ArrayList<>())
                .build();
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings.stream()
                .map(ListingDTO::fromListing)
                .collect(Collectors.toList());
    }

    public void setWorkedIn(long workedIn) {
        this.workedIn = workedIn;
    }

    public static List<UserDTO> fromUsers(List<User> users) {
        return users.stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }
}
