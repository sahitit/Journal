package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.MakeOrderService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

public class MakeOrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private OrderService orderService;

    @Mock
    private MakeOrderService makeOrderService;

    @InjectMocks
    private MakeOrderController makeOrderController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(makeOrderController).build();
    }

    @Test
    public void testDisplayOrdersToFulfill() throws Exception {
        List<OrderDto> mockOrders = Arrays.asList(
            new OrderDto(1L, "Latte"),
            new OrderDto(2L, "Espresso")
        );

        when(makeOrderService.getOrdersToFulfill()).thenReturn(mockOrders);

        mockMvc.perform(get("/api/makeorder/displayOrder"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[{'id':1,'name':'Latte'},{'id':2,'name':'Espresso'}]"));
    }
    
    @Test
    public void testFulfillOrder_Success() throws Exception {
        Long orderId = 1L;
        OrderDto mockOrder = new OrderDto(orderId, "Latte");

        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);
        when(makeOrderService.fulfillOrder(mockOrder)).thenReturn(true);

        mockMvc.perform(put("/api/makeorder/fulfillOrder")
                .param("orderId", String.valueOf(orderId)))
            .andExpect(status().isOk())
            .andExpect(content().string("Order fulfilled successfully."));
    }

    @Test
    public void testFulfillOrder_Failure() throws Exception {
        Long orderId = 1L;
        OrderDto mockOrder = new OrderDto(orderId, "Latte");

        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);
        when(makeOrderService.fulfillOrder(mockOrder)).thenReturn(false);

        mockMvc.perform(put("/api/makeorder/fulfillOrder")
                .param("orderId", String.valueOf(orderId)))
            .andExpect(status().isConflict())
            .andExpect(content().string("Order is not in PURCHASED status or could not be fulfilled."));
    }

    @Test
    public void testPickupOrder_Success() throws Exception {
        Long orderId = 1L;
        OrderDto mockOrder = new OrderDto(orderId, "Latte");

        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);
        when(makeOrderService.pickupOrder(mockOrder)).thenReturn(true);

        mockMvc.perform(put("/api/makeorder/pickupOrder")
                .param("orderId", String.valueOf(orderId)))
            .andExpect(status().isOk())
            .andExpect(content().string("Order picked up successfully."));
    }
    
    @Test
    public void testPickupOrder_Failure() throws Exception {
        Long orderId = 1L;
        OrderDto mockOrder = new OrderDto(orderId, "Latte");

        when(orderService.getOrderById(orderId)).thenReturn(mockOrder);
        when(makeOrderService.pickupOrder(mockOrder)).thenReturn(false);

        mockMvc.perform(put("/api/makeorder/pickupOrder")
                .param("orderId", String.valueOf(orderId)))
            .andExpect(status().isConflict())
            .andExpect(content().string("Order is not in FULFILLED status or could not be picked up."));
    }

    @Test
    public void testDisplayFulfilledOrders() throws Exception {
        List<OrderDto> mockOrders = Arrays.asList(
            new OrderDto(1L, "Latte"),
            new OrderDto(2L, "Espresso")
        );

        when(makeOrderService.getFulfilledOrders()).thenReturn(mockOrders);

        mockMvc.perform(get("/api/makeorder/fulfilledOrders"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[{'id':1,'name':'Latte'},{'id':2,'name':'Espresso'}]"));
    }
    
    @Test
    public void testMakeOrder_SuccessfulOrder() {
        // Arrange
        String orderName = "Latte";
        String amtPaid = "5.00";
        OrderDto mockOrder = new OrderDto();
        mockOrder.setName(orderName);

        InventoryDto mockInventory = new InventoryDto();

        when(orderService.getOrderByName(orderName)).thenReturn(mockOrder);
        when(inventoryService.getInventory()).thenReturn(mockInventory);
        when(makeOrderService.makeOrder(mockInventory, mockOrder)).thenReturn(true);

        ResponseEntity<Number> response = makeOrderController.makeOrder(orderName, amtPaid);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(orderService).getOrderByName(orderName);
        verify(makeOrderService).makeOrder(mockInventory, mockOrder);
    }
    
    @Test
    public void testMakeOrder_InvalidPaidAmount() {
        String orderName = "Latte";
        String amtPaid = "invalid";

        ResponseEntity<Number> response = makeOrderController.makeOrder(orderName, amtPaid);

        assertNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    
    @Test
    public void testMakeOrder_InsufficientInventory() {
        String orderName = "Latte";
        String amtPaid = "5.00";
        OrderDto mockOrder = new OrderDto();
        mockOrder.setName(orderName);

        InventoryDto mockInventory = new InventoryDto();

        when(orderService.getOrderByName(orderName)).thenReturn(mockOrder);
        when(inventoryService.getInventory()).thenReturn(mockInventory);
        when(makeOrderService.makeOrder(mockInventory, mockOrder)).thenReturn(false);

        ResponseEntity<Number> response = makeOrderController.makeOrder(orderName, amtPaid);

        assertEquals(5.00, response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); 
   }
    
}