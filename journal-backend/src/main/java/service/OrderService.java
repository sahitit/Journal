package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Interface defining the order behaviors.
 */
public interface OrderService {

    /**
     * Creates a order with the given information.
     *
     * @param orderDto
     *            order to create
     * @return created order
     */
    OrderDto createOrder ( OrderDto orderDto );

    /**
     * Returns the order with the given id.
     *
     * @param orderId
     *            order's id
     * @return the order with the given id
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    OrderDto getOrderById ( Long orderId );

    /**
     * Returns the order with the given name
     *
     * @param orderName
     *            order's name
     * @return the order with the given name.
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    OrderDto getOrderByName ( String orderName );

    /**
     * Returns true if the order already exists in the database.
     *
     * @param orderName
     *            order's name to check
     * @return true if already in the database
     */
    boolean isDuplicateName ( String orderName );

    /**
     * Returns a list of all the orders
     *
     * @return all the orders
     */
    List<OrderDto> getAllOrders ();

    /**
     * Updates the order with the given id with the order information
     *
     * @param orderId
     *            id of order to update
     * @param orderDto
     *            values to update
     * @return updated order
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    OrderDto updateOrder ( Long orderId, OrderDto orderDto );

    /**
     * Deletes the order with the given id
     *
     * @param orderId
     *            order's id
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    void deleteOrder ( Long orderId );

    /**
     * Fetches the current active order
     *
     * @return active order
     */
    OrderDto getActiveOrder ();

    OrderDto setActiveOrder ( Long orderId );

    /**
     * Complete the order purchase by taking the amount paid by the customer,
     * calculating the change, and updating the order status to PURCHASED.
     *
     * @param amtPaid
     *            Amount paid by the customer
     * @return Response message with the change or an error message if the
     *         payment is insufficient
     */
    boolean purchaseOrder ( Double amtPaid );

    /**
     * Returns a list of all orders that are no longer active.
     *
     * @return a list of non-active orders.
     */
    List<OrderDto> orderHistory ();

    OrderDto addItemToActiveOrder ( String itemName, int quantityToAdd );

}
