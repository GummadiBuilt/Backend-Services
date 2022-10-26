package com.infra.gummadibuilt.common;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Embeddable
public class ChangeTracking {

    @NotNull
    private String createdBy;

    @NotNull
    private Instant createdDate;

    @NotNull
    private String modifiedBy;

    @NotNull
    private Instant modifiedDate;

    public ChangeTracking(String user) {
        this.createdBy = user;
        this.createdDate = Instant.now();
        this.modifiedBy = user;
        this.modifiedDate = this.createdDate;
    }

    public ChangeTracking() {
        this.createdDate = Instant.now();
        this.modifiedDate = this.createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeTracking that = (ChangeTracking) o;

        return createdDate.equals(that.createdDate) &&
                modifiedDate.equals(that.modifiedDate);

    }

    @Override
    public int hashCode() {
        int result = 31 * createdDate.hashCode();
        result = 31 * result + modifiedDate.hashCode();
        return result;
    }

    public void update(String user) {
        modifiedBy = user;
        modifiedDate = Instant.now();
    }

}