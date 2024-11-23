package edu.ncsu.csc326.wolfcafe.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

public class OrderControllerTest {

    private MockMvc         mockMvc;

    @Mock
    private OrderService    orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    public void setUp () {
        MockitoAnnotations.openMocks( this );
        mockMvc = MockMvcBuilders.standaloneSetup( orderController ).build();
    }

    @Test
    public void testGetOrders () throws Exception {
        final OrderDto order = new OrderDto( 1L, "Order1" );
        when( orderService.getAllOrders() ).thenReturn( Collections.singletonList( order ) );

        mockMvc.perform( MockMvcRequestBuilders.get( "/api/orders" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].name" ).value( "Order1" ) );
    }

    @Test
    public void testGetOrderByName () throws Exception {
        final OrderDto order = new OrderDto( 1L, "Order1" );
        when( orderService.getOrderByName( "Order1" ) ).thenReturn( order );

        mockMvc.perform( MockMvcRequestBuilders.get( "/api/orders/Order1" ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name" ).value( "Order1" ) );
    }

    @Test
    public void testCreateOrder_Success () throws Exception {
        // Create OrderDto and ItemDto with values
        final OrderDto order = new OrderDto( null, "Order2" );
        final ItemDto item = new ItemDto( 1L, "Item1", 2, "Sample item", 5.0 );
        order.addItem( item );

        // Mock the service behavior
        when( orderService.isDuplicateName( order.getName() ) ).thenReturn( false );
        when( orderService.createOrder( any( OrderDto.class ) ) ).thenReturn( order );

        // Perform POST request and verify response
        mockMvc.perform( MockMvcRequestBuilders.post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).content(
                "{\"name\":\"Order2\",\"items\":[{\"name\":\"Item1\",\"amount\":2,\"description\":\"Sample item\",\"price\":5.0}]}" ) )
                .andExpect( status().isCreated() ) // Expecting 201 Created
                                                   // instead of 200 OK
                .andExpect( jsonPath( "$.name" ).value( "Order2" ) )
                .andExpect( jsonPath( "$.items[0].name" ).value( "Item1" ) )
                .andExpect( jsonPath( "$.items[0].price" ).value( 5.0 ) );
    }

    @Test
    public void testCreateOrder_DuplicateNameConflict () throws Exception {
        final OrderDto order = new OrderDto( null, "Order1" );
        when( orderService.isDuplicateName( order.getName() ) ).thenReturn( true );

        mockMvc.perform( MockMvcRequestBuilders.post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( "{\"name\":\"Order1\"}" ) ).andExpect( status().isConflict() );
    }

    @Test
    public void testCreateOrder_InsufficientStorage () throws Exception {
        final OrderDto order = new OrderDto( null, "Order3" );
        order.addItem( new ItemDto( 1L, "Item1", 1, "Sample item", 5.0 ) );

        when( orderService.isDuplicateName( order.getName() ) ).thenReturn( false );
        when( orderService.getAllOrders() )
                .thenReturn( Arrays.asList( new OrderDto(), new OrderDto(), new OrderDto() ) );

        mockMvc.perform( MockMvcRequestBuilders.post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( "{\"name\":\"Order3\",\"items\":[{\"name\":\"Item1\",\"amount\":1,\"price\":5.0}]}" ) )
                .andExpect( status().isInsufficientStorage() );
    }

    @Test
    public void testUpdateOrder_ConflictDueToZeroItems () throws Exception {
        final OrderDto order = new OrderDto( 1L, "Order1" );

        mockMvc.perform( MockMvcRequestBuilders.put( "/api/orders/1" ).contentType( MediaType.APPLICATION_JSON )
                .content( "{\"name\":\"Order1\",\"items\":[]}" ) ).andExpect( status().isConflict() );
    }

    @Test
    public void testUpdateOrder_ConflictDueToNegativeItemAmount () throws Exception {
        mockMvc.perform( MockMvcRequestBuilders.put( "/api/orders/1" ).contentType( MediaType.APPLICATION_JSON )
                .content( "{\"name\":\"Order1\",\"items\":[{\"name\":\"Item1\",\"amount\":-1,\"price\":5.0}]}" ) )
                .andExpect( status().isConflict() );
    }
}
