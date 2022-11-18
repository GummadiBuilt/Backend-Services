package com.infra.gummadibuilt.common;

import lombok.Data;

@Data
public class LoggedInUser {

    private final String firstName;

    private final String lastName;

    private final String email;

    private final String userId;

    public LoggedInUser(String firstName, String lastName, String email, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", firstName, lastName);
    }

}
