package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;

/**
 * Interface defining the make order behaviors.
 */
public interface MakeOrderService {

    /**
     * Reduces inventory items by the amount in the Order, and sets the order
     * status to Purchased.
     *
     * @param inventoryDto
     *            current inventory
     * @param orderDto
     *            order to purchase
     * @return if purchase was successful
     */
    boolean makeOrder ( InventoryDto inventoryDto, OrderDto orderDto );

    /**
     * Sets order status to fulfilled
     *
     * @param orderDto
     *            order to fulfill
     * @return if fulfill was successful
     */
    boolean fulfillOrder ( OrderDto orderDto );

    /**
     * Sets order status to done
     *
     * @param orderDto
     *            order to pickup
     * @return if pickup was successful
     */
    boolean pickupOrder ( OrderDto orderDto );

    /**
     * Retrieves all customer orders that need to be fulfilled by the staff.
     *
     * @return a list of orders to fulfill
     */
    public List<OrderDto> getOrdersToFulfill ();
    
    public List<OrderDto> getFulfilledOrders();
}
