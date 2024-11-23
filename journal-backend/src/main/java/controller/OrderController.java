package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

/**
 * Controller for managing Orders.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/orders" )
public class OrderController {

    @Autowired
    private OrderService     orderService;

    private static final int MAX_ORDERS = 3;

    /**
     * Fetch all orders.
     *
     * @return List of all orders.
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders () {
        final List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok( orders );
    }

    /**
     * Fetch a specific order by name.
     *
     * @param name
     *            Order name.
     * @return Order details or NOT_FOUND if not found.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER', 'ROLE_STAFF')" )
    @GetMapping ( "{name}" )
    public ResponseEntity<OrderDto> getOrder ( @PathVariable ( "name" ) final String name ) {
        final OrderDto order = orderService.getOrderByName( name );

        if ( order == null ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
        return ResponseEntity.ok( order );
    }

    /**
     * POST /api/orders to create a new order.
     */
    // @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')" )
    // @PostMapping
    // public ResponseEntity<OrderDto> createOrder ( @RequestBody final OrderDto
    // orderDto ) {
    // try {
    // // If the orderDto has "ACTIVE" status, it will be set as the active
    // // order
    // final OrderDto createdOrder = orderService.createOrder( orderDto );
    //
    // if ( "ACTIVE".equals( createdOrder.getStatus() ) ) {
    // // Set this newly created order as the active one
    // final OrderDto activatedOrder = orderService.setActiveOrder(
    // createdOrder.getId() );
    // return ResponseEntity.status( HttpStatus.CREATED ).body( activatedOrder
    // );
    // }
    //
    // return ResponseEntity.status( HttpStatus.CREATED ).body( createdOrder );
    // }
    // catch ( final IllegalArgumentException e ) {
    // return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( null );
    // }
    // catch ( final ResourceNotFoundException e ) {
    // return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
    // }
    // }

    /**
     * POST /api/orders to create a new order.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')" )
    @PostMapping
    public ResponseEntity<OrderDto> createOrder ( @RequestBody final OrderDto orderDto ) {
        System.out.println( "Received OrderDto: " + orderDto );
        try {
            // Check if the order name is a duplicate
            if ( orderService.isDuplicateName( orderDto.getName() ) ) {
                return ResponseEntity.status( HttpStatus.CONFLICT ).body( null ); // Return
                                                                                  // 409
                                                                                  // if
                                                                                  // duplicate
            }

            // Check if the number of orders exceeds the limit
            if ( orderService.getAllOrders().size() >= MAX_ORDERS ) {
                return ResponseEntity.status( HttpStatus.INSUFFICIENT_STORAGE ).body( null ); // Return
                                                                                              // 507
                                                                                              // if
                                                                                              // limit
                                                                                              // exceeded
            }

            // Create the order without an ID
            final OrderDto createdOrder = orderService.createOrder( orderDto );

            // Return the created order
            return ResponseEntity.status( HttpStatus.CREATED ).body( createdOrder );

        }
        catch ( final IllegalArgumentException e ) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( null );
        }
        catch ( final ResourceNotFoundException e ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
    }

    /**
     * GET /api/order/active to get the active order.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')" )
    @GetMapping ( "/active" )
    public ResponseEntity<OrderDto> getActiveOrder () {
        try {
            final OrderDto activeOrder = orderService.getActiveOrder();
            return ResponseEntity.ok( activeOrder );
        }
        catch ( final ResourceNotFoundException e ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
    }

    /**
     * Delete an order by ID.
     *
     * @param orderId
     *            Order ID.
     * @return Success or NOT_FOUND if the order does not exist.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')" )
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteOrder ( @PathVariable ( "id" ) final Long orderId ) {
        if ( orderService.getOrderById( orderId ) == null ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Order not found." );
        }

        orderService.deleteOrder( orderId );
        return ResponseEntity.ok( "Order deleted successfully." );
    }

    /**
     * Update an existing order by ID.
     *
     * @param id
     *            Order ID.
     * @param orderDto
     *            Updated order details.
     * @return Updated order or error response.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')" )
    @PutMapping ( "{id}" )
    public ResponseEntity<OrderDto> updateOrder ( @PathVariable ( "id" ) final Long id,
            @RequestBody final OrderDto orderDto ) {
        if ( orderDto.getItems().isEmpty() ) {
            return ResponseEntity.status( HttpStatus.CONFLICT ).body( null );
        }

        for ( final ItemDto item : orderDto.getItems() ) {
            if ( item.getAmount() <= 0 ) {
                return ResponseEntity.status( HttpStatus.CONFLICT ).body( null );
            }
        }

        final OrderDto updatedOrder = orderService.updateOrder( id, orderDto );
        return ResponseEntity.ok( updatedOrder );
    }

    /**
     * REST API method to purchase an order by completing a POST request with
     * the amount that has been paid as the body of the request.
     *
     * @param amtPaid
     *            amount paid
     * @return The response containing the status of the purchase.
     */
    @PostMapping ( "/purchase" )
    public ResponseEntity<String> purchaseOrder ( @RequestBody final String amtPaid ) {
        try {
            final Double paidAmount = Double.parseDouble( amtPaid );

            // Get the active order
            final OrderDto activeOrder = orderService.getActiveOrder();

            if ( activeOrder == null ) {
                return new ResponseEntity<>( "No active order found", HttpStatus.NOT_FOUND );
            }

            // Get the total cost of the active order
            final Double orderCost = activeOrder.getOrderCost();

            // Check if payment is sufficient
            if ( paidAmount < orderCost ) {
                return new ResponseEntity<>(
                        "Insufficient amount. Please pay at least: " + String.format( "%.2f", orderCost ),
                        HttpStatus.CONFLICT );
            }
            else if ( paidAmount.equals( orderCost ) ) {
                // Exact amount paid
                final boolean purchaseSuccess = orderService.purchaseOrder( paidAmount );

                if ( purchaseSuccess ) {
                    return ResponseEntity.ok( "Order purchased successfully. No change required." );
                }
                else {
                    return new ResponseEntity<>( "Insufficient inventory or invalid order", HttpStatus.CONFLICT );
                }
            }
            else {
                // Payment exceeds the cost, calculate change
                final boolean purchaseSuccess = orderService.purchaseOrder( paidAmount );

                if ( purchaseSuccess ) {
                    final Double change = paidAmount - orderCost;
                    return ResponseEntity
                            .ok( "Order purchased successfully. Your change is: " + String.format( "%.2f", change ) );
                }
                else {
                    return new ResponseEntity<>( "Insufficient inventory or invalid order", HttpStatus.CONFLICT );
                }
            }

        }
        catch ( final NumberFormatException e ) {
            return new ResponseEntity<>( "Invalid payment amount", HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Get all orders with a status other than "ACTIVE".
     *
     * @return List of orders with status not equal to "ACTIVE".
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')" )
    @GetMapping ( "/history" )
    public ResponseEntity<List<OrderDto>> getOrderHistory () {
        try {
            // Fetch orders with status other than "ACTIVE"
            final List<OrderDto> orderHistory = orderService.orderHistory();

            if ( orderHistory.isEmpty() ) {
                return ResponseEntity.status( HttpStatus.NO_CONTENT ).body( orderHistory );
            }

            return ResponseEntity.ok( orderHistory );
        }
        catch ( final Exception e ) {
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR ).body( null );
        }
    }

    /**
     * Updates the quantity of an existing item in the active order.
     *
     * @param itemName
     *            The name of the item to update.
     * @param quantityToAdd
     *            The quantity to add to the existing item.
     * @return The updated active order.
     */
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')" )
    @PutMapping ( "/active/items/{itemName}" )
    public ResponseEntity<OrderDto> updateItemQuantity ( @PathVariable ( "itemName" ) final String itemName,
            @RequestBody final int quantityToAdd ) {
        try {
            final OrderDto updatedOrder = orderService.addItemToActiveOrder( itemName, quantityToAdd );
            return ResponseEntity.ok( updatedOrder );
        }
        catch ( final IllegalArgumentException e ) {
            return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( null );
        }
        catch ( final ResourceNotFoundException e ) {
            return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( null );
        }
    }

}
