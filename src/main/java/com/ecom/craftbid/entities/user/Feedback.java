package com.ecom.craftbid.entities.user;

import com.ecom.craftbid.enums.FeedbackStar;
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


    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Profile receiver;

    @ManyToOne
    @JoinColumn(name = "giver_id")
    private Profile giver;

    private FeedbackStar stars;
    private String description;
}
