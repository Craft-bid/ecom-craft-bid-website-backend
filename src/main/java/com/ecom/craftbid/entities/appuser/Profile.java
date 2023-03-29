package com.ecom.craftbid.entities.appuser;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private long id;

    @Column(length = 2500)
    private String description;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<PersonalData> personalData = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Feedback> receivedFeedback = new ArrayList<>();

    @OneToMany(mappedBy = "giver", cascade = CascadeType.ALL)
    private List<Feedback> givenFeedback = new ArrayList<>();

    private Double averageRating;
}
