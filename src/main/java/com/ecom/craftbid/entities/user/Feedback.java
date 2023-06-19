package com.ecom.craftbid.entities.user;

import com.ecom.craftbid.enums.FeedbackStar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private long id;


    // TODO: (quick fix wont check) possible bug: author and receiver not set
    @JsonIgnoreProperties("receivedFeedback")
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Profile receiver;

    @JsonIgnoreProperties("givenFeedback")
    @ManyToOne
    @JoinColumn(name = "giver_id")
    private Profile author;

    private FeedbackStar stars;
    private String comment;
}
