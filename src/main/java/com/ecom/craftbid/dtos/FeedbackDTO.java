package com.ecom.craftbid.dtos;


import com.ecom.craftbid.entities.user.Feedback;
import com.ecom.craftbid.enums.FeedbackStar;
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
public class FeedbackDTO {
    private long id;
    private ProfileDTO receiver;
    private ProfileDTO giver;
    private FeedbackStar stars;
    private String description;

    public static FeedbackDTO fromFeedback(Feedback feedback) {
        return FeedbackDTO.builder()
                .id(feedback.getId())
                .receiver(ProfileDTO.fromProfile(feedback.getReceiver()))
                .giver(ProfileDTO.fromProfile(feedback.getGiver()))
                .stars(feedback.getStars())
                .description(feedback.getDescription())
                .build();
    }

    public static List<FeedbackDTO> fromFeedbackList(List<Feedback> feedbackList) {
        return feedbackList.stream()
                .map(FeedbackDTO::fromFeedback)
                .collect(Collectors.toList());
    }

}
