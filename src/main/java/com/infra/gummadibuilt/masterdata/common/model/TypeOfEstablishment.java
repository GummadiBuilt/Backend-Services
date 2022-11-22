package com.infra.gummadibuilt.masterdata.common.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
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
public class TypeOfEstablishment {

    // Name of the db constraint for uniqueness of the role
    public static final String UNQ_NAME_CONSTRAINT = "establishment_desc_unq_name";

    @Id
    @NotBlank
    @Size(max = 255)
    private String establishmentDescription;

    private boolean isActive;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TypeOfEstablishment that = (TypeOfEstablishment) o;
        return establishmentDescription != null && Objects.equals(establishmentDescription, that.establishmentDescription);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
