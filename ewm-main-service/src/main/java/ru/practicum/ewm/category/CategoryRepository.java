package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value =
            "SELECT id, name " +
            "FROM categories " +
            "ORDER BY id " +
            "LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Category> findAllOrderedById(@Param("from") int from,
                                      @Param("size") int size);
}
