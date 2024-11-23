package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.service.MakeOrderService;

/**
 * Implementation of the MakeOrderService interface.
 */
@Service
public class MakeOrderServiceImpl implements MakeOrderService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository     orderRepository;

    @Autowired
    private ModelMapper         modelMapper;

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
    @Override
    public boolean makeOrder ( final InventoryDto inventoryDto, final OrderDto orderDto ) {
        final Inventory inventory = modelMapper.map( inventoryDto, Inventory.class );
        final Order order = modelMapper.map( orderDto, Order.class );

        if ( enoughItems( inventory, order ) ) {

            inventoryRepository.save( inventory );
            return true;
        }

        return false;
    }

    /**
     * Returns true if there are enough items to make the beverage. This design
     * assumes the unit provided in the recipe and inventory is the same.
     *
     * @param inventory
     *            coffee maker inventory
     * @param recipe
     *            recipe to check if there are enough items
     * @return true if enough items to make the beverage
     */
    private boolean enoughItems ( final Inventory inventory, final Order order ) {
        boolean isEnough = true;
        boolean itemFound = false;
        for ( final Item orderItem : order.getItems() ) {
            itemFound = false;
            for ( final Item inventoryItem : inventory.getItems() ) {
                if ( orderItem.getName().equals( inventoryItem.getName() ) ) {
                    if ( orderItem.getAmount() <= inventoryItem.getAmount() ) {
                        inventoryItem.setAmount( inventoryItem.getAmount() - orderItem.getAmount() );
                        itemFound = true;
                    }
                    break;
                }
            }
            if ( !itemFound ) {
                isEnough = false;
                break;
            }
        }
        return isEnough;
    }

    @Override
    public boolean fulfillOrder ( final OrderDto orderDto ) {
        // Find the order in the repository
        final Order order = orderRepository.findById( orderDto.getId() )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with ID: " + orderDto.getId() ) );

        // Update order status to FULFILLED
        if ( order.getStatus() == OrderStatus.PURCHASED ) {
            order.setStatus( OrderStatus.FULFILLED );
            orderRepository.save( order );
            return true;
        }
        return false;
    }

    @Override
    public boolean pickupOrder ( final OrderDto orderDto ) {
        // Find the order and set status to pickup
    	final Order order = orderRepository.findById( orderDto.getId() )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with ID: " + orderDto.getId() ) );
    	
    	// Update order status to FULFILLED
        if ( order.getStatus() == OrderStatus.FULFILLED ) {
            order.setStatus( OrderStatus.PICKED_UP );
            orderRepository.save( order );
            return true;
        }
        return false;
    }

    @Override
    public List<OrderDto> getOrdersToFulfill () {
        final List<Order> unfulfilledOrders = orderRepository.findByStatus( OrderStatus.PURCHASED );
        return unfulfilledOrders.stream().map( order -> modelMapper.map( order, OrderDto.class ) )
                .collect( Collectors.toList() );
    }
    
    @Override
    public List<OrderDto> getFulfilledOrders() {
        List<Order> fulfilledOrders = orderRepository.findByStatus(OrderStatus.FULFILLED);
        return fulfilledOrders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }


}
