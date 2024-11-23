package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.MakeOrderService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

/**
 * MakeOrderController provides the endpoint for making a order.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/makeorder" )
public class MakeOrderController {

    /** Connection to InventoryService */
    @Autowired
    private InventoryService inventoryService;

    /** Connection to OrderService */
    @Autowired
    private OrderService     orderService;

    /** Connection to MakeOrderService */
    @Autowired
    private MakeOrderService makeOrderService;

    /**
     * REST API method to make coffee by completing a POST request with the ID
     * of the order as the path variable and the amount that has been paid as
     * the body of the response
     *
     * @param name
     *            order name
     * @param amtPaid
     *            amount paid
     * @return The change the customer is due if successful
     */
    @PostMapping ( "{name}" )
    public ResponseEntity<Number> makeOrder ( @PathVariable ( "name" ) final String orderName,
            @RequestBody final String amtPaid ) {
        try {
            final Double paidAmount = Double.parseDouble( amtPaid );
            final OrderDto orderDto = orderService.getOrderByName( orderName );

            final double change = makeOrder( orderDto, paidAmount );
            if ( change == paidAmount ) {
                if ( paidAmount < orderDto.getOrderCost() ) {
                    return new ResponseEntity<>( paidAmount, HttpStatus.CONFLICT );
                }
                else {
                    return new ResponseEntity<>( paidAmount, HttpStatus.BAD_REQUEST );
                }
            }
            return ResponseEntity.ok( change );
        }
        catch ( final NumberFormatException e ) {
            return new ResponseEntity<>( null, HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Helper method to make coffee
     *
     * @param toPurchase
     *            order that we want to make
     * @param amtPaid
     *            money that the user has given the machine
     * @return change if there was enough money to make the coffee, throws
     *         exceptions if not
     */
    private double makeOrder ( final OrderDto toPurchase, final double amtPaid ) {
        double change = amtPaid;
        final InventoryDto inventoryDto = inventoryService.getInventory();

        if ( toPurchase.getOrderCost() <= amtPaid ) {
            if ( makeOrderService.makeOrder( inventoryDto, toPurchase ) ) {
                change = amtPaid - toPurchase.getOrderCost();
                return change;
            }
            else {
                // not enough inventory
                return change;
            }
        }
        else {
            // not enough money
            return change;
        }

    }

    /**
     * Endpoint to retrieve orders that need to be fulfilled.
     *
     * @return List of unfulfilled OrderDto objects.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_STAFF', 'ROLE_ADMIN')" )
    @GetMapping ( "/displayOrder" )
    public ResponseEntity<List<OrderDto>> displayOrdersToFulfill () {
        final List<OrderDto> ordersToFulfill = makeOrderService.getOrdersToFulfill();
        return ResponseEntity.ok( ordersToFulfill );
    }

    /**
     * Helper method to make coffee
     *
     * @param orderDto
     *            to fulfill
     *
     * @return true if the order status is fulfilled or false if the order
     *         status is purchased or unfulfilled.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_STAFF', 'ROLE_ADMIN')" )
    @PutMapping ( "/fulfillOrder" )
    public ResponseEntity<String> fulfillOrder ( @RequestParam final Long orderId ) {
        final OrderDto orderDto = orderService.getOrderById( orderId );

        // Attempt to fulfill the order
        final boolean isFulfilled = makeOrderService.fulfillOrder( orderDto );

        if ( isFulfilled ) {
            return ResponseEntity.ok( "Order fulfilled successfully." );
        }
        else {
            return ResponseEntity.status( HttpStatus.CONFLICT )
                    .body( "Order is not in PURCHASED status or could not be fulfilled." );
        }
    }
    
    /**
     * Helper method to make coffee
     *
     * @param orderDto
     *            to fulfill
     *
     * @return true if the order status is fulfilled or false if the order
     *         status is purchased or unfulfilled.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_CUSTOMER', 'ROLE_ADMIN')" )
    @PutMapping ( "/pickupOrder" )
    public ResponseEntity<String> pickupOrder ( @RequestParam final Long orderId ) {
        final OrderDto orderDto = orderService.getOrderById( orderId );

        // Attempt to pickup the order
        final boolean isPickedUp = makeOrderService.pickupOrder(orderDto);

        if ( isPickedUp ) {
            return ResponseEntity.ok( "Order picked up successfully." );
        }
        else {
            return ResponseEntity.status( HttpStatus.CONFLICT )
                    .body( "Order is not in FULFILLED status or could not be picked up." );
        }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_STAFF', 'ROLE_ADMIN', 'ROLE_CUSTOMER')")
    @GetMapping("/fulfilledOrders")
    public ResponseEntity<List<OrderDto>> displayFulfilledOrders() {
        List<OrderDto> fulfilledOrders = makeOrderService.getFulfilledOrders();
        return ResponseEntity.ok(fulfilledOrders);
    }


}
