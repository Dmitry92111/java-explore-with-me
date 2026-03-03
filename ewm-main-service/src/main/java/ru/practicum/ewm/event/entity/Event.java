package ru.practicum.ewm.event.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.proxy.HibernateProxy;
import ru.practicum.ewm.event.location.Location;
import ru.practicum.ewm.participation_request.entity.ParticipationRequest;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.category.Category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
@ToString
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
    @ToString.Exclude
    private User initiator;

    @Embedded
    @AttributeOverride(name = "lat", column = @Column(name = "lat", nullable = false))
    @AttributeOverride(name = "lon", column = @Column(name = "lon", nullable = false))
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    private Category category;

    @OneToMany(mappedBy = "event", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    @ToString.Exclude
    private List<ParticipationRequest> requests = new ArrayList<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();

        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();

        if (thisEffectiveClass != oEffectiveClass) return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : this.getClass().hashCode();
    }
}
