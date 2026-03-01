package ru.practicum.ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByIdAndDeletedIsFalse(long id);

    Optional<User> findByIdAndDeletedIsFalse(long id);

    @Query(value =
            "SELECT * " +
            "FROM users " +
            "WHERE deleted IS FALSE " +
            "ORDER BY id " +
            "LIMIT :size " +
            "OFFSET :from",
            nativeQuery = true)
    List<User> findAllOrderedById(@Param("from") int from,
                                  @Param("size") int size);

    @Query(value =
            "SELECT * " +
            "FROM users " +
            "WHERE deleted IS FALSE " +
            "AND id IN (:ids) " +
            "ORDER BY id " +
            "LIMIT :size " +
            "OFFSET :from",
            nativeQuery = true)
    List<User> findAllOrderedByIdAndIdsIn(@Param("from") int from,
                                          @Param("size") int size,
                                          @Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE User u SET u.deleted = true, u.deletedAt = :deletedAt WHERE u.id = :id AND u.deleted = false")
    int safeDelete(@Param("id") long id,
                   @Param("deletedAt") LocalDateTime deletedAt);
}