/**
 *
 */
package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;

/**
 * Tests Order repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class OrderRepositoryTest {

    /** Reference to order repository */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        orderRepository.deleteAll();

        final Order order1 = new Order( 1L, "Coffee", OrderStatus.ACTIVE );
        final Order order2 = new Order( 2L, "Latte", OrderStatus.ACTIVE );

        orderRepository.save( order1 );
        orderRepository.save( order2 );
    }

    /**
     * Tests the OrderRepository.findByName method.
     */
    @Test
    public void testGetOrderByName () {
        final Optional<Order> order = orderRepository.findByName( "Coffee" );
        final Order actualOrder = order.get();
        assertAll( "Order contents", () -> assertEquals( "Coffee", actualOrder.getName() ),
                () -> assertEquals( 0, actualOrder.getOrderCost() ) );
        final Item ing1 = new Item( 0L, "Matcha", 3, null, 1.00 );
        actualOrder.addItem( ing1 );
        orderRepository.save( actualOrder );
        final Optional<Order> updatedOrder = orderRepository.findByName( "Coffee" );
        final Order updatedActualOrder = updatedOrder.get();
        assertAll( "Order contents", () -> assertEquals( "Coffee", updatedActualOrder.getName() ),
                () -> assertEquals( 3.0, updatedActualOrder.getOrderCost() ),
                () -> assertEquals( "Matcha", updatedActualOrder.getItems().getFirst().getName() ),
                () -> assertEquals( 3, updatedActualOrder.getItems().getFirst().getAmount() ) );

        final Optional<Order> order2 = orderRepository.findByName( "Latte" );
        final Order actualOrder2 = order2.get();
        assertAll( "Order contents", () -> assertEquals( "Latte", actualOrder2.getName() ),
                () -> assertEquals( 0, actualOrder2.getOrderCost() ) );
    }

    /**
     * Tests the OrderRepository.findByName method, expecting failure.
     */
    @Test
    public void testGetOrderByNameInvalid () {
        final Optional<Order> order = orderRepository.findByName( "Unknown" );
        assertTrue( order.isEmpty() );
    }

}
