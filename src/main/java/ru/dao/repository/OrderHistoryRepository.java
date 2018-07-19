package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import ru.dao.entity.OrderHistory;

public interface OrderHistoryRepository extends DataTablesRepository<OrderHistory, Integer> {
}
