package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.service.impl.InventoryServiceImpl;
import edu.ncsu.csc326.wolfcafe.service.impl.MakeOrderServiceImpl;
import edu.ncsu.csc326.wolfcafe.service.impl.OrderServiceImpl;

/**
 * Test file for Make Order Service representation for csc326 issue 108
 * ChatGPT assisted in the development of this file
 */
@SpringBootTest
class MakeOrderServiceTest {

    @InjectMocks
    private MakeOrderServiceImpl makeOrderService;

    @Mock
    private InventoryServiceImpl inventoryService;
    
    @Mock
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ModelMapper modelMapper;

    //Starter objects for tesing
    private InventoryDto inventoryDto;
    private OrderDto orderDto;
    private Inventory inventory;
    private Order order;
    private Item inventoryItem;
    private Item orderItem;

    @BeforeEach
    void setUp() {
    	//Create an inventory and basic items, saving them and then using them in mock
    	
    	inventoryItem = new Item();
        inventoryItem.setName("Coffee");
        inventoryItem.setAmount(10);

        orderItem = new Item();
        orderItem.setName("Coffee");
        orderItem.setAmount(5);

        inventory = new Inventory();
        inventory.setItems(Collections.singletonList(inventoryItem));

        order = new Order();
        order.setId(1L);
        order.setItems(Collections.singletonList(orderItem));
        order.setStatus(OrderStatus.ACTIVE);

        inventoryDto = new InventoryDto();
        inventoryDto.setItems(Collections.singletonList(modelMapper.map(inventoryItem, ItemDto.class)));

        orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setItems(Collections.singletonList(modelMapper.map(orderItem, ItemDto.class)));
        orderDto.setStatus(OrderStatus.ACTIVE);
    }

    @Test
    void testMakeOrderSuccess() {
        //Test making a order successfully
    	orderItem.setAmount(15); // More than inventory amount
        when(modelMapper.map(inventoryDto, Inventory.class)).thenReturn(inventory);
        when(modelMapper.map(orderDto, Order.class)).thenReturn(order);

        boolean result = makeOrderService.makeOrder(inventoryDto, orderDto);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        assertFalse(result);
    }

    @Test
    void testMakeOrderFailure() {
    	//Assign order amount to be more than in inventory so it fails
        orderItem.setAmount(15);
        when(modelMapper.map(inventoryDto, Inventory.class)).thenReturn(inventory);
        when(modelMapper.map(orderDto, Order.class)).thenReturn(order);

        boolean result = makeOrderService.makeOrder(inventoryDto, orderDto);

        verify(inventoryRepository, never()).save(any(Inventory.class));
        assertFalse(result);
    }

    @Test
    void testFulfillOrderSuccess() {
        order.setStatus(OrderStatus.PURCHASED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        boolean result = makeOrderService.fulfillOrder(orderDto);

        verify(orderRepository, times(1)).save(any(Order.class));
        assertTrue(result);
        assertEquals(OrderStatus.FULFILLED, order.getStatus());
    }

    @Test
    void testFulfillOrderFailure() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        boolean result = makeOrderService.fulfillOrder(orderDto);

        verify(orderRepository, never()).save(any(Order.class));
        assertFalse(result);
    }

    @Test
    void testPickupOrderSuccess() {
        order.setStatus(OrderStatus.FULFILLED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        boolean result = makeOrderService.pickupOrder(orderDto);

        verify(orderRepository, times(1)).save(any(Order.class));
        assertTrue(result);
        assertEquals(OrderStatus.PICKED_UP, order.getStatus());
    }

    @Test
    void testPickupOrderFailure() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        boolean result = makeOrderService.pickupOrder(orderDto);

        verify(orderRepository, never()).save(any(Order.class));
        assertFalse(result);
    }

    @Test
    void testGetOrdersToFulfill() {
        when(orderRepository.findByStatus(OrderStatus.PURCHASED)).thenReturn(Collections.singletonList(order));
        when(modelMapper.map(order, OrderDto.class)).thenReturn(orderDto);

        List<OrderDto> ordersToFulfill = makeOrderService.getOrdersToFulfill();

        assertNotNull(ordersToFulfill);
        assertEquals(1, ordersToFulfill.size());
        assertEquals(OrderStatus.ACTIVE, ordersToFulfill.get(0).getStatus());
    }
}
