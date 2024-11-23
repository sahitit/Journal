package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import edu.ncsu.csc326.wolfcafe.service.TaxService;

/**
 * Implementation of the OrderService interface.
 */
@Service
public class OrderServiceImpl implements OrderService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private OrderRepository  orderRepository;

    @Autowired
    private ModelMapper      modelMapper;

    @Autowired
    private TaxService       taxService;

    // @Autowired
    // private ItemRepository itemRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public OrderDto createOrder ( final OrderDto orderDto ) {
        // Map the OrderDto to Order entity
        final Order order = modelMapper.map( orderDto, Order.class );

        // Validate that items are present
        if ( orderDto.getItems() == null || orderDto.getItems().isEmpty() ) {
            throw new IllegalArgumentException( "Order must have at least one item." );
        }

        // Validate item amounts and map items
        final List<Item> items = orderDto.getItems().stream().map( itemDto -> {
            if ( itemDto.getAmount() <= 0 ) {
                throw new IllegalArgumentException( "Item amount must be a positive integer." );
            }
            return modelMapper.map( itemDto, Item.class );
        } ).collect( Collectors.toList() );

        // Set order properties
        order.setName( orderDto.getName() );
        order.setStatus( orderDto.getStatus() );
        order.setItems( items );

        // Save the Order to OrderRepository
        final Order savedOrder = orderRepository.save( order );

        // If status is "ACTIVE", we set it as the active order
        // if ( OrderStatus.ACTIVE.equals( orderDto.getStatus() ) ) {
        // setActiveOrder( savedOrder.getId() );
        // }

        return modelMapper.map( savedOrder, OrderDto.class );
    }

    @Override
    public OrderDto setActiveOrder ( final Long orderId ) {
        // Check if there's already an active order and deactivate it if found
        final Optional<Order> currentActiveOrderOpt = orderRepository.findAll().stream()
                .filter( order -> OrderStatus.ACTIVE.equals( order.getStatus() ) ).findFirst();

        currentActiveOrderOpt.ifPresent( currentActiveOrder -> {
            // Deactivate the current active order (set it to PICKED_UP or other
            // status)
            currentActiveOrder.setStatus( OrderStatus.PICKED_UP ); // Or
                                                                   // "CANCELED"
                                                                   // if needed
            orderRepository.save( currentActiveOrder ); // Save the deactivated
                                                        // order
        } );

        // Retrieve the order to be made active
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id " + orderId ) );

        // Set the status of the new order to ACTIVE
        order.setStatus( OrderStatus.ACTIVE );

        // Save and return the newly activated order
        final Order savedOrder = orderRepository.save( order );
        return modelMapper.map( savedOrder, OrderDto.class );
    }

    /**
     * Returns the order with the given id.
     *
     * @param orderId
     *            order's id
     * @return the order with the given id
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    @Override
    public OrderDto getOrderById ( final Long orderId ) {
        // Attempts to find a Order with the given id; throws error if no
        // Order is found
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );
        // Maps the Order to a OrderDto and returns
        return modelMapper.map( order, OrderDto.class );
    }

    /**
     * Returns the order with the given name
     *
     * @param orderName
     *            order's name
     * @return the order with the given name.
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    @Override
    // Attempts to find a Order with the given name; throws error if no Order
    // is found
    public OrderDto getOrderByName ( final String orderName ) {
        final Order order = orderRepository.findByName( orderName )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with name " + orderName ) );
        // Maps the Order to a OrderDto and returns
        return modelMapper.map( order, OrderDto.class );
    }

    /**
     * Returns true if the order already exists in the database.
     *
     * @param orderName
     *            order's name to check
     * @return true if already in the database
     */
    @Override
    public boolean isDuplicateName ( final String orderName ) {
        try {
            // Attempts to find a Order with the given name
            getOrderByName( orderName );
            // Returns true if a Order is found
            return true;
        }
        // ResourceNotFoundException thrown when orderName is not found in the
        // OrderRepository
        catch ( final ResourceNotFoundException e ) {
            return false;
        }
    }

    /**
     * Returns a list of all the orders
     *
     * @return all the orders
     */
    @Override
    public List<OrderDto> getAllOrders () {
        // Gets the list of all Orders from the OrderRepository
        final List<Order> orders = orderRepository.findAll();
        // Maps each Order to a OrderDto, puts them into a List, and returns
        // the List
        return orders.stream().map( ( order ) -> modelMapper.map( order, OrderDto.class ) )
                .collect( Collectors.toList() );
    }

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
    @Override
    public OrderDto updateOrder ( final Long orderId, final OrderDto orderDto ) {
        // final Order order = orderRepository.findById(orderId)
        // .orElseThrow(() -> new ResourceNotFoundException("Order does not
        // exist with id " + orderId));
        //
        // // Update Order fields from OrderDto
        // order.setName(orderDto.getName());
        // order.setPrice(orderDto.getPrice());
        //
        // // Update items if present in OrderDto
        // if (orderDto.getItems() != null) {
        // List<Item> items = orderDto.getItems().stream()
        // .map(itemDto -> itemRepository.findById(itemDto.getId())
        // .orElseThrow(() -> new ResourceNotFoundException("Item does not exist
        // with id " + itemDto.getId())))
        // .collect(Collectors.toList());
        // order.setItems(items);
        // }
        //
        // final Order savedOrder = orderRepository.save(order);
        // return modelMapper.map(savedOrder, OrderDto.class);
        //
        // // OLD
        // final Order order = orderRepository.findById( orderId ).orElseThrow(
        // () -> new ResourceNotFoundException( "Inventory does not exist with
        // id of " + orderDto.getId() ) );
        //
        // // Clear current items in inventory
        // order.getItems().clear();
        //
        // if ( orderDto.getItems() != null ) {
        // for ( final ItemDto itemDto : orderDto.getItems() ) {
        // // Check if the item already exists
        // final Optional<Item> existingItem = itemRepository.findByName(
        // itemDto.getName() );
        // Item item;
        //
        // if ( existingItem.isPresent() ) {
        // // Use existing item if found
        // item = existingItem.get();
        // // Optionally update its properties if necessary
        // item.setAmount( itemDto.getAmount() );
        // item.setDescription( itemDto.getDescription() );
        // item.setPrice( itemDto.getPrice() );
        // }
        // else {
        // // Create a new item if it does not exist
        // item = new Item();
        // item.setName( itemDto.getName() );
        // item.setAmount( itemDto.getAmount() );
        // item.setDescription( itemDto.getDescription() );
        // item.setPrice( itemDto.getPrice() );
        // }
        //
        // order.getItems().add( item );
        // }
        // }
        //
        // // Save the updated inventory
        // final Order savedOrder = orderRepository.save( order );
        //
        // // Return the updated InventoryDto
        // return modelMapper.map( savedOrder, OrderDto.class );
        // // OLD ENDS
        final List<Item> items = new ArrayList<Item>();
        ;

        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id" + orderId ) );

        // Ensure recipe has at least one ingredient and no more than two
        if ( orderDto.getItems() == null || orderDto.getItems().isEmpty() ) {
            throw new IllegalArgumentException( "Recipe must have at least one ingredient." );
        }
        // else if ( orderDto.getItems().size() > 10 ) {
        // throw new IllegalArgumentException( "Order cannot have more than 2
        // ingredients." );
        // }

        // Validate ingredient amounts
        orderDto.getItems().forEach( itemDto -> {
            if ( itemDto.getAmount() <= 0 ) {
                throw new IllegalArgumentException( "Ingredient units must be a positive integer." );
            }
            final Item item = modelMapper.map( itemDto, Item.class );
            items.add( item );
        } );

        // Update recipe details
        order.setName( orderDto.getName() );
        order.setStatus( orderDto.getStatus() );
        order.setItems( items );

        // Retrieve existing inventory
        // final InventoryDto inventoryDto = inventoryService.getInventory(); //
        // Get
        // the
        // inventory
        // DTO
        //
        // final List<Item> existingIngredients =
        // inventoryDto.getItems().stream()
        // .map(itemDto -> modelMapper.map(itemDto, Item.class))
        // .collect(Collectors.toList());

        // // Set the items in the order using the existing inventory
        // order.setItems(orderDto.getItems().stream().map(ingredientDto -> {
        // // Find the existing ingredient by name
        // return existingIngredients.stream()
        // .filter(ing -> ing.getName().equals(ingredientDto.getName()))
        // .findFirst()
        // .orElseThrow(() -> new IllegalArgumentException(
        // "Ingredient not found: " + ingredientDto.getName()));
        // }).collect(Collectors.toList()));

        // Save the updated order and return the DTO
        final Order savedOrder = orderRepository.save( order );
        return modelMapper.map( savedOrder, OrderDto.class );
    }

    /**
     * Deletes the order with the given id
     *
     * @param orderId
     *            order's id
     * @throws ResourceNotFoundException
     *             if the order doesn't exist
     */
    @Override
    public void deleteOrder ( final Long orderId ) {
        // Attempts to get a Order based on the provided orderId
        final Order order = orderRepository.findById( orderId )
                .orElseThrow( () -> new ResourceNotFoundException( "Order does not exist with id " + orderId ) );
        // Removes the found Order from the OrderRepository
        orderRepository.delete( order );
    }

    @Override
    public OrderDto getActiveOrder () {
        final List<Order> orders = orderRepository.findAll();

        // Check if the list is empty or if no active orders exist
        if ( orders.isEmpty() ) {
            throw new ResourceNotFoundException( "No active order found." );
        }

        // Use stream to filter and find the first active order
        final Order activeOrder = orders.stream().filter( order -> OrderStatus.ACTIVE.equals( order.getStatus() ) )
                .findFirst().orElseThrow( () -> new ResourceNotFoundException( "No active order found." ) );

        // Return the active order as a DTO
        return modelMapper.map( activeOrder, OrderDto.class );
    }

    @Override
    public boolean purchaseOrder ( final Double paidAmount ) {
        // Fetch the active order
        final OrderDto activeOrder = getActiveOrder(); // Retrieve the active
                                                       // order
        if ( activeOrder == null ) {
            throw new ResourceNotFoundException( "No active order found." );
        }

        // Check if payment is valid
        final double orderTotal = activeOrder.getOrderCost() * taxService.getTaxRate(); // Assuming
                                                                                        // this
                                                                                        // field
                                                                                        // exists

        if ( paidAmount < orderTotal ) {
            return false; // Insufficient payment
        }

        // Validate inventory for each item in the order
        // for ( final ItemDto item : activeOrder.getItems() ) {
        // if ( !inventoryService.isItemAvailable( item.getId(),
        // item.getAmount() ) ) {
        // return false; // Insufficient inventory for any item
        // }
        // }
        //
        // // Deduct items from inventory
        // for ( final ItemDto item : activeOrder.getItems() ) {
        // inventoryService.deductItemStock( item.getId(), item.getAmount() );
        // }

        // Update order status to "purchased"
        activeOrder.setStatus( OrderStatus.PURCHASED );

        // orderRepository.save(activeOrder); // Assuming orderRepository saves
        // the updated order
        // final Order orderEntity = modelMapper.map(OrderDtoactiveOrder);
        // final ModelMapper modelMapper = new ModelMapper();
        final Order orderEntity = modelMapper.map( activeOrder, Order.class );

        // Save the updated order entity
        orderRepository.save( orderEntity );

        return true; // Order successfully processed
    }

    /**
     * Returns a list of all orders that are no longer active.
     *
     * @return a list of non-active orders.
     */
    @Override
    public List<OrderDto> orderHistory () {
        // Fetch all orders from the repository
        final List<Order> allOrders = orderRepository.findAll();

        // Filter orders that are not ACTIVE
        final List<Order> nonActiveOrders = allOrders.stream()
                .filter( order -> !OrderStatus.ACTIVE.equals( order.getStatus() ) ).collect( Collectors.toList() );

        // Map the filtered orders to OrderDto
        return nonActiveOrders.stream().map( order -> modelMapper.map( order, OrderDto.class ) )
                .collect( Collectors.toList() );
    }

    @Override
    public OrderDto addItemToActiveOrder ( final String itemName, final int quantityToAdd ) {
        if ( quantityToAdd <= 0 ) {
            throw new IllegalArgumentException( "Quantity to add must be a positive number." );
        }

        // Retrieve the active order
        final OrderDto activeOrderDto = getActiveOrder();
        final Order activeOrder = modelMapper.map( activeOrderDto, Order.class );

        // Find the item in the active order's items
        final Optional<Item> existingItemOpt = activeOrder.getItems().stream()
                .filter( item -> item.getName().equalsIgnoreCase( itemName ) ).findFirst();

        if ( existingItemOpt.isPresent() ) {
            // Item exists, update the quantity
            final Item existingItem = existingItemOpt.get();
            existingItem.setAmount( existingItem.getAmount() + quantityToAdd );
        }
        else {
            // Item does not exist, handle as needed
            throw new IllegalArgumentException( "Item not found in the active order: " + itemName );
        }

        // Save the updated order
        final Order savedOrder = orderRepository.save( activeOrder );

        // Return the updated active order as a DTO
        return modelMapper.map( savedOrder, OrderDto.class );
    }

}
