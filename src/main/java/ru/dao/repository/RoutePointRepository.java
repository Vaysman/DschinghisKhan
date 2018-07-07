package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import ru.dao.entity.RoutePoint;

public interface RoutePointRepository extends DataTablesRepository<RoutePoint, Integer> {
}
