package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import ru.dao.entity.Order;

public interface OrderRepository extends DataTablesRepository<Order, Integer> {

}
