package com.infra.gummadibuilt.notifications.model;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class UserNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "application_user_id")
    private ApplicationUser applicationUser;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "notification_id")
    private Notifications notifications;

    @Embedded
    private ChangeTracking changeTracking;

}
