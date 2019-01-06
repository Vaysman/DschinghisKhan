package ru.dao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.constant.OrderStatus;
import ru.dao.entity.Order;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@PreAuthorize("isFullyAuthenticated()")
public interface OrderRepository extends DataTablesRepository<Order, Integer> {
    Optional<Order> findFirstByIdAndStatusIn(Integer orderId, OrderStatus[] orderStatuses);

    @Query(value = "SELECT * FROM orders WHERE (status='DELIVERED' OR status='DELIVERY_CONFD') AND (NOW() >= TIMESTAMPADD(DAY, document_return_date,status_change_date))", nativeQuery = true)
    List<Order> getOrdersForDocumentReturn();

    @Query(value = "SELECT * FROM orders WHERE (status='DOCS_RECEIVED') AND (NOW() >= TIMESTAMPADD(DAY, payment_date,status_change_date))", nativeQuery = true)
    List<Order> getOrdersForPaymentReception();

    Page<Order> findOrdersByOriginatorAndRouteNotNullAndStatusNot(@Param("originator") Integer originator, @Param("status") OrderStatus status,Pageable pageable);
    Page<Order> findOrdersByOriginatorAndRouteNotNullAndStatusNotAndNumberContaining(@Param("originator") Integer originator, @Param("status") OrderStatus status, @Param("number") String number,Pageable pageable);

    Optional<Order> findFirstByOriginatorAndDriverId(Integer originator, Integer driverId);

    @RestResource(exported = false)
    List<Order> findAllByOriginatorAndStatusChangeDateBetween(Integer originator, Date from, Date to);

}
