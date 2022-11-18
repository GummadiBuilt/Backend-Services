package com.infra.gummadibuilt.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.infra.gummadibuilt.common.LoggedInUser;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Component
public class UserInfo {

    /**
     * Returns logged in user-id
     *
     * @return Returns logged-in-username
     */
    public static LoggedInUser loggedInUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String[] pieces = token.split("\\.");
        String b64payload = pieces[1];
        String jsonString = new String(Base64.decodeBase64(b64payload), StandardCharsets.UTF_8);
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);
        String firstName = convertedObject.get("given_name").getAsString();
        String lastName = convertedObject.get("family_name").getAsString();
        String email = convertedObject.get("email").getAsString();
        String userId = convertedObject.get("sub").getAsString();
        return new LoggedInUser(firstName, lastName, email, userId);
    }
}
