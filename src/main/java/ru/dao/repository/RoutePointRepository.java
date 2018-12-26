package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.RoutePoint;

@PreAuthorize("isFullyAuthenticated()")
public interface RoutePointRepository extends DataTablesRepository<RoutePoint, Integer> {
}
