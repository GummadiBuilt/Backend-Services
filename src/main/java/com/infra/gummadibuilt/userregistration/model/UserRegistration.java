package com.infra.gummadibuilt.userregistration.model;

import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.masterdata.geography.model.City;
import com.infra.gummadibuilt.masterdata.geography.model.Country;
import com.infra.gummadibuilt.masterdata.geography.model.State;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@TypeDefs({
        @TypeDef(name = "list-array", typeClass = ListArrayType.class)
})
public class UserRegistration {

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

    @Enumerated(EnumType.STRING)
    private ApproveReject approveReject;

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
    @Size(max = 150)
    private String contactName;

    @NotBlank
    @Size(max = 150)
    private String contactDesignation;

    @NotBlank
    @Size(max = 10)
    private String contactPhoneNumber;

    @NotBlank
    @Size(max = 100)
    private String contactEmailAddress;

    /**
     * Project coordinator info
     */

    @NotBlank
    @Size(max = 150)
    private String coordinatorName;

    @NotBlank
    @Size(max = 10)
    private String coordinatorMobileNumber;

    @Embedded
    @NotNull
    @Valid
    private ChangeTracking changeTracking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserRegistration that = (UserRegistration) o;
        return id != 0 && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
