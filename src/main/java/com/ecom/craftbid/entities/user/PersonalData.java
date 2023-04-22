package com.ecom.craftbid.entities.user;


import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "personal_data")
public class PersonalData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_data_id")
    private long id;

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String country;
    private String zipCode;
    private String phoneNumber;

    /* TODO: add payment details depending of chosen payments API
    *   and hook it up to transactions table */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
