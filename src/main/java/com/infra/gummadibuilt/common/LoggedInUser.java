package com.infra.gummadibuilt.common;

import lombok.Data;

@Data
public class LoggedInUser {

    private final String firstName;

    private final String lastName;

    public LoggedInUser(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", firstName, lastName);
    }

}
