package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.OrderHistory;

@PreAuthorize("isFullyAuthenticated()")
public interface OrderHistoryRepository extends DataTablesRepository<OrderHistory, Integer> {
}
