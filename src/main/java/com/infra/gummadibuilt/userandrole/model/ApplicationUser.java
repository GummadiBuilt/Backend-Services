package com.infra.gummadibuilt.userandrole.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ApplicationUser {

    // Name of the db constraint for uniqueness of the country
    public static final String UNQ_NAME_CONSTRAINT = "app_user_unq_email";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(max = 75)
    private String firstName;

    @NotBlank
    @Size(max = 75)
    private String lastName;

    @NotBlank
    @Size(max = 75)
    private String email;

    @NotBlank
    @Size(max = 750)
    private String userSecret;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_role_id")
    private ApplicationRole applicationRole;

    private boolean isActive;

    private boolean credentialsExpired;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationUser that = (ApplicationUser) o;
        return id != 0 && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
