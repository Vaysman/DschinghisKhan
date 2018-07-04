package ru.dao.repository;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.domain.Specification;
import ru.dao.entity.RoutePoint;

public interface RoutePointRepository extends DataTablesRepository<RoutePoint, Integer> {
    @Override
    DataTablesOutput<RoutePoint> findAll(DataTablesInput input, Specification<RoutePoint> additionalSpecification);
}
