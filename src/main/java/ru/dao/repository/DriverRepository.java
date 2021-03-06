package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.Driver;

import java.util.List;

@PreAuthorize("isFullyAuthenticated()")
public interface DriverRepository  extends DataTablesRepository<Driver, Integer> {
    List<Driver> findTop10ByNameContainingAndOriginator(@Param("name") String name, @Param("originator") Integer originator);
}
