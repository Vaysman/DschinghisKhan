package ru.dao.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.dao.entity.Order;
import ru.dao.entity.OrderOffer;

import java.util.List;

public interface OrderOfferRepository extends PagingAndSortingRepository<OrderOffer, Integer> {
    List<OrderOffer> findAllByOrder(Order order);
}
