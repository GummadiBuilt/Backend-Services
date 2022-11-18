package com.infra.gummadibuilt.userandrole.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.masterdata.geography.model.City;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
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
    private String id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_role_id")
    private ApplicationRole applicationRole;

    /**
     * Company details
     */

    @NotBlank
    @Size(max = 100)
    private String companyName;

    @NotNull
    @Max(9999)
    private int yearOfEstablishment;

    @Type(type = "list-array")
    @Column(columnDefinition = "varchar[]")
    private List<String> typeOfEstablishment;

    @NotBlank
    @Size(max = 500)
    private String address;

    @OneToOne
    @NotNull
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToOne
    @NotNull
    @JoinColumn(name = "state_id")
    private State state;

    @OneToOne
    @NotNull
    @JoinColumn(name = "city_id")
    private City city;

    /**
     * Contact information
     */
    @NotBlank
    @Size(max = 75)
    private String contactFirstName;

    @NotBlank
    @Size(max = 75)
    private String contactLastName;

    @NotBlank
    @Size(max = 150)
    private String contactDesignation;

    @NotBlank
    @Size(max = 10)
    private String contactPhoneNumber;

    @NotBlank
    @Size(max = 100)
    private String contactEmailAddress;

    private boolean isActive;

    private boolean credentialsExpired;

    @OneToMany(mappedBy = "applicationUser", cascade = CascadeType.REMOVE)
    private List<TenderInfo> tenderInfo;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationUser that = (ApplicationUser) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
