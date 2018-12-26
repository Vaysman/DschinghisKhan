package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.Transport;

import java.util.List;

@PreAuthorize("isFullyAuthenticated()")
public interface TransportRepository extends DataTablesRepository<Transport, Integer> {
    List<Transport> findTop10ByNumberContainingAndOriginator(@Param("number") String number, @Param("originator") Integer originator);
}
