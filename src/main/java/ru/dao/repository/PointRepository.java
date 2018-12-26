package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.Point;

import java.util.List;

@PreAuthorize("isFullyAuthenticated()")
public interface PointRepository extends DataTablesRepository<Point, Integer> {
    List<Point> findTop10ByNameContaining(@Param("name") String name);
    List<Point> findTop10ByNameContainingAndOriginator(@Param("name") String name, @Param("originator") Integer originator);
    List<Point> findTop10ByClientIdAndNameContaining(@Param("clientId") Integer clientId, @Param("name") String name);
}
