package com.infra.gummadibuilt.masterdata.geography.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

@Entity
@ToString
@RequiredArgsConstructor
public class Country {

    // Name of the db constraint for uniqueness of the country
    public static final String UNQ_NAME_CONSTRAINT = "country_name_unq_name";

    @Id
    @Size(min = 1, max = 3)
    private String countryIsoCode;

    @NotBlank
    @Size(max = 100)
    private String countryName;

    @OneToMany(mappedBy = "country")
    @ToString.Exclude
    private Set<State> stateSet;

    @Embedded
    @NotNull
    private ChangeTracking changeTracking;

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public ChangeTracking getChangeTracking() {
        return changeTracking;
    }

    public void setChangeTracking(ChangeTracking changeTracking) {
        this.changeTracking = changeTracking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Country country = (Country) o;
        return countryIsoCode != null && Objects.equals(countryIsoCode, country.countryIsoCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
