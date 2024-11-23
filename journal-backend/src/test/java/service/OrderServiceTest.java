package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.TaxService;
import edu.ncsu.csc326.wolfcafe.service.impl.OrderServiceImpl;

@SpringBootTest
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TaxService taxService;

    @Mock
    private InventoryService inventoryService;

    private Order sampleOrder;
    private OrderDto sampleOrderDto;
    private List<Item> sampleItems;
    private List<ItemDto> sampleItemDtos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample Items
        sampleItems = new ArrayList<>();
        Item sampleItem1 = new Item();
        sampleItem1.setName("Item1");
        sampleItem1.setAmount(20);
        Item sampleItem2 = new Item();
        sampleItem2.setName("Item2");
        sampleItem2.setAmount(30);
        sampleItems.add(sampleItem1);
        sampleItems.add(sampleItem2);

        sampleItemDtos = new ArrayList<>();
        ItemDto sampleItemDto1 = new ItemDto();
        sampleItemDto1.setName("Item1");
        sampleItemDto1.setAmount(20);
        ItemDto sampleItemDto2 = new ItemDto();
        sampleItemDto2.setName("Item2");
        sampleItemDto2.setAmount(30);
        sampleItemDtos.add(sampleItemDto1);
        sampleItemDtos.add(sampleItemDto2);

        // Sample Order and OrderDto
        sampleOrder = new Order(1L, "Test Order", sampleItems, OrderStatus.ACTIVE);
        sampleOrderDto = new OrderDto(1L, "Test Order", sampleItemDtos, OrderStatus.ACTIVE);
    }

    @Test
    void testCreateOrder_Success() {
        when(modelMapper.map(sampleOrderDto, Order.class)).thenReturn(sampleOrder);
        when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);
        when(modelMapper.map(sampleOrder, OrderDto.class)).thenReturn(sampleOrderDto);

        OrderDto result = orderService.createOrder(sampleOrderDto);

        assertNotNull(result);
        assertEquals(sampleOrderDto.getName(), result.getName());
        verify(orderRepository, times(1)).save(sampleOrder);
    }

    // Add Test for UpdateOrder
    @Test
    void testUpdateOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);
        when(modelMapper.map(sampleOrder, OrderDto.class)).thenReturn(sampleOrderDto);

        OrderDto updatedOrderDto = new OrderDto(1L, "Updated Order", sampleItemDtos, OrderStatus.PICKED_UP);
        sampleOrder.setName("Updated Order");
        sampleOrder.setStatus(OrderStatus.PICKED_UP);

        OrderDto result = orderService.updateOrder(1L, updatedOrderDto);

        assertNotNull(result);
        assertEquals("Test Order", result.getName());
        assertEquals(OrderStatus.ACTIVE, result.getStatus());
        verify(orderRepository, times(1)).save(sampleOrder);
    }

    @Test
    void testUpdateOrder_NotFound_ThrowsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> orderService.updateOrder(1L, sampleOrderDto));

        assertEquals("Order does not exist with id1", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testGetOrderByName_Success() {
        when(orderRepository.findByName("Test Order")).thenReturn(Optional.of(sampleOrder));
        when(modelMapper.map(sampleOrder, OrderDto.class)).thenReturn(sampleOrderDto);

        OrderDto result = orderService.getOrderByName("Test Order");

        assertNotNull(result);
        assertEquals("Test Order", result.getName());
        verify(orderRepository, times(1)).findByName("Test Order");
    }

    @Test
    void testGetOrderByName_NotFound_ThrowsException() {
        when(orderRepository.findByName("Nonexistent Order")).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> orderService.getOrderByName("Nonexistent Order"));

        assertEquals("Order does not exist with name Nonexistent Order", exception.getMessage());
    }

    @Test
    void testIsDuplicateName_True() {
        when(orderRepository.findByName("Test Order")).thenReturn(Optional.of(sampleOrder));

        assertTrue(orderService.isDuplicateName("Test Order"));
        verify(orderRepository, times(1)).findByName("Test Order");
    }

    @Test
    void testIsDuplicateName_False() {
        when(orderRepository.findByName("Unique Order")).thenReturn(Optional.empty());

        assertFalse(orderService.isDuplicateName("Unique Order"));
        verify(orderRepository, times(1)).findByName("Unique Order");
    }

    @Test
    void testGetAllOrders_Success() {
        List<Order> orders = List.of(sampleOrder);
        List<OrderDto> orderDtos = List.of(sampleOrderDto);

        when(orderRepository.findAll()).thenReturn(orders);
        when(modelMapper.map(sampleOrder, OrderDto.class)).thenReturn(sampleOrderDto);

        List<OrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderDtos, result);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testDeleteOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        assertDoesNotThrow(() -> orderService.deleteOrder(1L));
        verify(orderRepository, times(1)).delete(sampleOrder);
    }

    @Test
    void testDeleteOrder_NotFound_ThrowsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> orderService.deleteOrder(1L));

        assertEquals("Order does not exist with id 1", exception.getMessage());
        verify(orderRepository, never()).delete(any(Order.class));
    }
    
    @Test
    void testGetActiveOrder_Success() {
        // Mocking repo and modelMapper
        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder));
        when(modelMapper.map(sampleOrder, OrderDto.class)).thenReturn(sampleOrderDto);

        OrderDto result = orderService.getActiveOrder();
        assertNotNull(result);
        assertEquals(sampleOrderDto, result);
        verify(orderRepository, times(1)).findAll();
    }
    
    
    @Test
    void testGetActiveOrder_NoActiveOrder() {
        // Mocking repository
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(ResourceNotFoundException.class, () -> orderService.getActiveOrder());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testOrderHistory_Success() {
        // Mocking repo and modelMapper
        Order nonActiveOrder = new Order(2L, "Past Order", sampleItems, OrderStatus.PURCHASED);
        OrderDto nonActiveOrderDto = new OrderDto(2L, "Past Order", sampleItemDtos, OrderStatus.PURCHASED);

        when(orderRepository.findAll()).thenReturn(List.of(sampleOrder, nonActiveOrder));
        when(modelMapper.map(nonActiveOrder, OrderDto.class)).thenReturn(nonActiveOrderDto);

        List<OrderDto> result = orderService.orderHistory();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PURCHASED, result.get(0).getStatus());
    }
    
    @Test
    void testSetActiveOrder_Success() {
        // Mocking the repo to return a current active order
        Order currentActiveOrder = new Order(2L, "Current Active Order", sampleItems, OrderStatus.ACTIVE);
        when(orderRepository.findAll()).thenReturn(List.of(currentActiveOrder, sampleOrder));
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        when(orderRepository.save(currentActiveOrder)).thenReturn(currentActiveOrder);

        sampleOrder.setStatus(OrderStatus.ACTIVE);
        when(orderRepository.save(sampleOrder)).thenReturn(sampleOrder);
        
        // Mocking modelMapper to map the new active ordre
        when(modelMapper.map(sampleOrder, OrderDto.class)).thenReturn(sampleOrderDto);

        OrderDto result = orderService.setActiveOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.ACTIVE, result.getStatus());
        verify(orderRepository, times(1)).save(currentActiveOrder);
        verify(orderRepository, times(1)).save(sampleOrder);
    }
    
    @Test
    void testSetActiveOrder_NoOrderFound() {
    	
        // Mocking repo to return an empty result for the order ID
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.setActiveOrder(99L));

        verify(orderRepository, never()).save(any(Order.class));
    }
}
