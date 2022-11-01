package com.infra.gummadibuilt.login;

import com.infra.gummadibuilt.login.dto.WhoAmIDto;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class WhoAmIService {

    public WhoAmIDto getUserDetails(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        String encodedString = new String(Base64.decodeBase64(authorization.getBytes()));

        WhoAmIDto whoAmIDto = new WhoAmIDto();
        whoAmIDto.setCredentialsExpired(false);
        return whoAmIDto;

    }
}
