package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {
    private long id;
    private String description;
    private List<PersonalDataDTO> personalData;
    private List<FeedbackDTO> receivedFeedback;
    private List<FeedbackDTO> givenFeedback;
    private Double averageRating;

    public static ProfileDTO fromProfile(Profile profile) {
        return ProfileDTO.builder()
                .id(profile.getId())
                .description(profile.getDescription())
                .personalData(PersonalDataDTO.fromPersonalDataList(profile.getPersonalData()))
                .receivedFeedback(FeedbackDTO.fromFeedbackList(profile.getReceivedFeedback()))
                .givenFeedback(FeedbackDTO.fromFeedbackList(profile.getGivenFeedback()))
                .averageRating(profile.getAverageRating())
                .build();
    }

    public static List<ProfileDTO> fromProfileList(List<Profile> profiles) {
        return profiles.stream()
                .map(ProfileDTO::fromProfile)
                .collect(Collectors.toList());
    }

}

