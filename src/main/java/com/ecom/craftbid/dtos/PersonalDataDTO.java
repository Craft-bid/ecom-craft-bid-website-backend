package com.ecom.craftbid.dtos;

import com.ecom.craftbid.entities.user.PersonalData;
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
public class PersonalDataDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String country;
    private String zipCode;
    private String phoneNumber;

    public static PersonalDataDTO fromPersonalData(PersonalData personalData) {
        return PersonalDataDTO.builder()
                .id(personalData.getId())
                .firstName(personalData.getFirstName())
                .lastName(personalData.getLastName())
                .address(personalData.getAddress())
                .city(personalData.getCity())
                .country(personalData.getCountry())
                .zipCode(personalData.getZipCode())
                .phoneNumber(personalData.getPhoneNumber())
                .build();
    }


    public static List<PersonalDataDTO> fromPersonalDataList(List<PersonalData> personalData) {
        return personalData.stream()
                .map(PersonalDataDTO::fromPersonalData)
                .collect(Collectors.toList());
    }
}
