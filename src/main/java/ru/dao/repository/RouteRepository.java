package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import ru.dao.entity.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends DataTablesRepository<Route, Integer> {

    @Override
    Optional<Route> findById(Integer integer);

    Optional<Route> findRouteById(@Param("id") Integer integer);

    List<Route> findTop10ByNameContainingAndOriginator(@Param("name") String name, @Param("originator") Integer originator);


}
