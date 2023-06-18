package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String avatarUri;

    public static ProfileDTO fromProfile(Profile profile) {
        return ProfileDTO.builder()
                .id(profile.getId())
                .avatarUri(profile.getAvatarUri())
                .description(profile.getDescription())
                .personalData(PersonalDataDTO.fromPersonalDataList(profile.getPersonalData()))
                .receivedFeedback(FeedbackDTO.fromFeedbackList(profile.getReceivedFeedback()))
                .givenFeedback(FeedbackDTO.fromFeedbackList(profile.getGivenFeedback()))
                .averageRating(profile.getAverageRating())
                .build();
    }

}

