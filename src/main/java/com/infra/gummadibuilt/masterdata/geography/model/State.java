package com.infra.gummadibuilt.masterdata.geography.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class State {

    // Name of the db constraint for uniqueness of the state
    public static final String UNQ_NAME_CONSTRAINT = "state_name_unq_name";

    @Id
    @Size(min = 1, max = 3)
    private String stateIsoCode;

    @NotBlank
    @Size(min = 1, max = 150)
    private String stateName;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @OneToMany(mappedBy = "state")
    @ToString.Exclude
    private Set<City> citySet;

    @Embedded
    @NotNull
    private ChangeTracking changeTracking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        State state = (State) o;
        return stateIsoCode != null && Objects.equals(stateIsoCode, state.stateIsoCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
