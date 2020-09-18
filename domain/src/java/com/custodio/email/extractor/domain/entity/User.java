package com.custodio.email.extractor.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.UUID;

@Getter
public class User {
    private final Collection<EmailAccount> accounts;
    private final String firstName;
    private final UUID id;
    private final String lastName;

    @JsonCreator
    public User(@JsonProperty("accounts") final Collection<EmailAccount> accounts,
                @JsonProperty("firstName") final String firstName,
                @JsonProperty("id") final UUID id,
                @JsonProperty("lastName") final String lastName) {
        this.accounts = accounts;
        this.firstName = firstName;
        this.id = id;
        this.lastName = lastName;
    }
}
