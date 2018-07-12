package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import ru.dao.entity.Driver;

import java.util.List;

public interface DriverRepository  extends DataTablesRepository<Driver, Integer> {
    List<Driver> findByNameContainingAndOriginator(@Param("name") String name, @Param("originator") Integer originator);
}
