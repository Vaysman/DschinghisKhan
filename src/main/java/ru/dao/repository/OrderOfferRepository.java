package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import ru.dao.entity.Order;
import ru.dao.entity.OrderOffer;

import java.util.List;

public interface OrderOfferRepository extends DataTablesRepository<OrderOffer, Integer> {
    List<OrderOffer> findAllByOrder(Order order);
}
