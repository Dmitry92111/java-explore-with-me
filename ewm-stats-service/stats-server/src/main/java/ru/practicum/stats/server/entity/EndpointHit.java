package ru.practicum.stats.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "endpoint_hits")
@ToString
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app", nullable = false, length = 64)
    private String app;

    @Column(name = "uri", nullable = false, length = 128)
    private String uri;

    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    public static EndpointHit of(String app, String uri, String ip, LocalDateTime created) {
        return new EndpointHit(null, app, uri, ip, created);
    }

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
        EndpointHit endpointHit = (EndpointHit) o;
        return getId() != null && Objects.equals(getId(), endpointHit.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : this.getClass().hashCode();
    }
}
