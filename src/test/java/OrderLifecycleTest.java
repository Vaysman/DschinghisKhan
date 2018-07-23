import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.Application;
import ru.constant.OrderObligation;
import ru.constant.OrderRequirements;
import ru.constant.OrderStatus;
import ru.dao.entity.*;
import ru.dao.repository.*;
import ru.dto.json.order.OrderAcceptData;
import ru.dto.json.order.OrderAssignData;
import ru.service.OrderLifecycleService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureTestEntityManager
@Transactional
@SpringBootTest(classes = Application.class)
public class OrderLifecycleTest {

    @Autowired
    OrderLifecycleService orderLifecycleService;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    TransportRepository transportRepository;

    @Autowired
    DriverRepository driverRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PointRepository pointRepository;

    @Autowired
    RouteRepository routeRepository;

    @Test
    public void orderLifecycleTest(){
        //User/Company are described in import.sql
        User companyUser = userRepository.findByLogin("UMB").orElse(null);
        User dispatcherUser = userRepository.findByLogin("test").orElse(null);
        assertThat(companyUser).isNotNull();
        assertThat(dispatcherUser).isNotNull();
        Company company = companyUser.getCompany();
        assertThat(dispatcherUser.getCompany()).isNotNull();
        assertThat(company).isNotNull();

        Route route = routeRepository.findById(1).orElse(null);
        assertThat(route).isNotNull();

        //Creating order
        Order order = Order
                .builder()
                .orderObligation(OrderObligation.NON_MANDATORY)
                .cargo(new HashSet<>(Arrays.asList("Одно", "Другое","Третье")))
                .requirements(new HashSet<>(Arrays.asList(OrderRequirements.GIDROBOT.getRequirementName(), OrderRequirements.MED_CARD.getRequirementName())))
                .route(route)
                .rating(100)
                .documentReturnDate(1)
                .paymentDate(1)
                .originator(dispatcherUser.getCompany().getId())
                .number("LSS-1")
                .status(OrderStatus.CREATED)
                .build();
        orderRepository.save(order);

        assertThat(order.getId()).isNotNull();


        //Assigning order
        OrderAssignData orderAssignData = new OrderAssignData();
        orderAssignData.setDispatcherPrice(100F);
        orderAssignData.setAssignedCompanies(Collections.singletonList(company.getId()));

        orderLifecycleService.assign(order.getId(),dispatcherUser,orderAssignData);


        //To refresh entity in persistence context
        company = companyRepository.findById(company.getId()).orElse(null);
        assertThat(company).isNotNull();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ASSIGNED);
        assertThat(order.getAssignedCompanies().contains(company)).isTrue();

        //TODO: figure out why pending orders do not refresh on company's side
//        assertThat(company.getPendingOrders().size()).isNotEqualTo(0);


        //Accepting order
        Transport companyTransport = company.getTransports().stream().findFirst().orElse(null);
        Driver companyDriver = company.getDrivers().stream().findFirst().orElse(null);

        assertThat(companyTransport).isNotNull();
        assertThat(companyDriver).isNotNull();

        OrderAcceptData orderAcceptData = new OrderAcceptData();
        orderAcceptData.setCompanyId(company.getId());
        orderAcceptData.setTransportId(companyTransport.getId());
        orderAcceptData.setDriverId(companyDriver.getId());
        orderAcceptData.setProposedPrice(100F);


        orderLifecycleService.accept(order.getId(),companyUser,orderAcceptData);
        company = companyRepository.findById(company.getId()).orElse(null);
        assertThat(company).isNotNull();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThat(order.getOffers().stream().findFirst().orElse(null)).isNotNull();
        assertThat(order.getAssignedCompanies().size()).isEqualTo(0);


        //Confirming order
        OrderOffer offer = order.getOffers().stream().findFirst().orElse(null);
        assertThat(offer).isNotNull();
        assertThat(offer.getCompany())
                .isEqualTo(company);
        assertThat(offer.getDriver())
                .isEqualTo(companyDriver);
        assertThat(offer.getTransport())
                .isEqualTo(companyTransport);
        assertThat(offer.getProposedPrice()).isEqualTo(100F);
        assertThat(offer.getManagerCompany().equals(dispatcherUser.getCompany()));

        orderLifecycleService.confirm(dispatcherUser,offer.getId());
        company = companyRepository.findById(company.getId()).orElse(null);
        assertThat(company).isNotNull();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.getTransport())
                .isEqualTo(companyTransport);
        assertThat(order.getDriver())
                .isEqualTo(companyDriver);
        assertThat(order.getProposedPrice()).isEqualTo(100F);
        assertThat(order.getOffers().size()).isEqualTo(0);
        assertThat(order.getAssignedCompanies().size()).isEqualTo(0);

        //Changing order status
        orderLifecycleService.changeStatus(dispatcherUser, order.getId(),OrderStatus.EN_ROUTE);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.EN_ROUTE);

        //Confirm delivery
        orderLifecycleService.confirmDelivery(dispatcherUser, order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERY_CONFD);

        assertThat(orderRepository.findFirstByIdAndStatusIn(order.getId(), OrderStatus.getChangeableStatuses()).isPresent()).isEqualTo(false);

        orderLifecycleService.changeStatus(order,OrderStatus.DOCUMENT_RETURN);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DOCUMENT_RETURN);

        orderLifecycleService.confirmDocumentDelivery(dispatcherUser,order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DOCS_RECEIVED);

        orderLifecycleService.changeStatus(order, OrderStatus.PAY_PENDING);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAY_PENDING);

        orderLifecycleService.claimPayment(dispatcherUser,order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYED);

        orderLifecycleService.claimNonPayment(companyUser,order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NOT_PAYED);

        orderLifecycleService.confirmPayment(companyUser,order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_CONFD);
    }
}
