package ru.practicum.ewm.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.participation_request.entity.ParticipationRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 250, nullable = false)
    private String name;

    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "requester")
    private List<ParticipationRequest> participationRequests = new ArrayList<>();

    @OneToMany(mappedBy = "initiator")
    private List<Event> events = new ArrayList<>();

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addParticipationRequest(ParticipationRequest request) {
        participationRequests.add(request);
        request.setRequester(this);
    }

    public void addEvent(Event event) {
        events.add(event);
        event.setInitiator(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", deleted=" + deleted +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
