package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import ru.dao.entity.Company;
import ru.dao.entity.Order;
import ru.dao.entity.PendingOrder;

import java.util.Optional;

public interface PendingOrderRepository extends DataTablesRepository<PendingOrder, Long> {
    Optional<PendingOrder> findFirstByCompanyAndOrder(Company company, Order order);
}
