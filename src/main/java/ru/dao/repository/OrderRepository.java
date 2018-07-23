package ru.dao.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import ru.constant.OrderStatus;
import ru.dao.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends DataTablesRepository<Order, Integer> {
    Optional<Order> findFirstByIdAndStatusIn(Integer orderId, OrderStatus[] orderStatuses);

    @Query(value = "SELECT * FROM orders WHERE (status='DELIVERED' OR status='DELIVERY_CONFD') AND (NOW() >= (status_change_date + INTERVAL document_return_date DAY))", nativeQuery = true)
    List<Order> getOrdersForDocumentReturn();

    @Query(value = "SELECT * FROM orders WHERE (status='DOCS_RECEIVED') AND (NOW() >= (status_change_date + INTERVAL payment_date DAY))", nativeQuery = true)
    List<Order> getOrdersForPaymentReception();
}
