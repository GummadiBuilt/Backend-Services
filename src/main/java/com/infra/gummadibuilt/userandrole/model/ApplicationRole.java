package com.infra.gummadibuilt.userandrole.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import lombok.*;
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
public class ApplicationRole {

    // Name of the db constraint for uniqueness of the role
    public static final String UNQ_NAME_CONSTRAINT = "role_name_unq_name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(max = 25)
    private String roleName;

    @NotBlank
    @Size(max = 255)
    private String roleDescription;

    private boolean displayToAll;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationRole that = (ApplicationRole) o;
        return id != 0 && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
