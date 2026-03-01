package ru.practicum.ewm.event.entity;

import jakarta.persistence.*;
import lombok.*;

import ru.practicum.ewm.event.location.Location;
import ru.practicum.ewm.participation_request.entity.ParticipationRequest;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.category.Category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "paid", nullable = false)
    private boolean paid;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus state;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Embedded
    @AttributeOverride(name = "lat", column = @Column(name = "lat", nullable = false))
    @AttributeOverride(name = "lon", column = @Column(name = "lon", nullable = false))
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private List<ParticipationRequest> requests = new ArrayList<>();
}
