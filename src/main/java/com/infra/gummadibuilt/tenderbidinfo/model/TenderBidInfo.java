package com.infra.gummadibuilt.tenderbidinfo.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderapplicationform.model.dto.ActionTaken;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "tender_bid_info_unique", columnNames = {"application_user_id", "tender_info_id"})
})
public class TenderBidInfo {

    public static final String UNQ_NAME_CONSTRAINT = "tender_bid_info_unique";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_user_id")
    private ApplicationUser applicationUser;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tender_info_id")
    private TenderInfo tenderInfo;

    @NotBlank
    @Size(max = 50)
    private String tenderDocumentName;

    // Document size in MB
    @NotNull
    private long tenderDocumentSize;

    @NotNull
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode tenderFinanceInfo;

    @Enumerated(EnumType.STRING)
    private ActionTaken actionTaken;

    private ChangeTracking changeTracking;

}
