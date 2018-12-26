package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.dao.entity.Order;
import ru.dao.entity.OrderOffer;

import java.util.List;

@PreAuthorize("isFullyAuthenticated()")
public interface OrderOfferRepository extends DataTablesRepository<OrderOffer, Integer> {
    List<OrderOffer> findAllByOrder(Order order);

    @Query(value = "SELECT * FROM order_offers WHERE (driver_id IS NULL OR transport_id IS NULL) AND (NOW() >= TIMESTAMPADD(HOUR, 5,offer_datetime))", nativeQuery = true)
    List<OrderOffer> getOutdatedOffers();
}
