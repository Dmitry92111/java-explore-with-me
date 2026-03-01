package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {


    @Query("SELECT DISTINCT c " +
            "FROM Compilation c " +
            "LEFT JOIN FETCH c.events " +
            "WHERE c.id = :id")
    Optional<Compilation> findByIdWithEvents(@Param("id") long id);

    @Modifying
    @Query("DELETE FROM Compilation c where c.id = :id")
    int deleteByIdReturningCount(@Param("id") long id);
}
