package com.infra.gummadibuilt.common.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "mail")
@Component
public class MailConfiguration {

    /**
     * The base URL for building links to the application in E-Mails.
     */
    @NotNull
    private String applicationLinkBase;

    /**
     * Used as the envelope sender address for SMTP when RFR sends E-Mail.
     */
    @NotNull
    @Email
    private String fromAddress;

    /**
     * Used to fetch the path of Header Image.
     */
    @NotNull
    private String headerPath;

    /**
     * enables and disables mail feature based on this flag
     */
    private boolean enableMailFeature;

    public String getApplicationLinkBase() {
        return applicationLinkBase;
    }

    public void setApplicationLinkBase(String applicationLinkBase) {
        this.applicationLinkBase = applicationLinkBase;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public boolean isEnableMailFeature() {
        return enableMailFeature;
    }

    public void setEnableMailFeature(boolean enableMailFeature) {
        this.enableMailFeature = enableMailFeature;
    }

    public String getHeaderPath() {
        return headerPath;
    }

    public void setHeaderPath(String headerPath) {
        this.headerPath = headerPath;
    }
}