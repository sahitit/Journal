/**
 *
 */
package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import jakarta.persistence.EntityManager;

/**
 * Unit test suite for InventoryController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc       mvc;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager entityManager;

    /**
     * Sets up the test case. We assume only one inventory row. Because
     * inventory is treated as a singleton (only one row), we must truncate for
     * auto increment on the id to work correctly. Or at least we would if it
     * would allow it but there's a foreign key problem. This workaround seems
     * to work.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        //entityManager.createNativeQuery( "TRUNCATE TABLE inventory_items" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();
        entityManager.createNativeQuery( "ALTER TABLE item AUTO_INCREMENT = 0" ).executeUpdate();
        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 0" ).executeUpdate();
    }

    /**
     * Tests the GET /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetInventory () throws Exception {
        // Issues a GET request and checks the returned JSON
        mvc.perform( get( "/api/inventory" ) ).andExpect( status().isOk() )
                .andExpect( content().string( "{\"id\":1,\"items\":[]}" ) );
    }

    /**
     * Tests the PUT /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testUpdateInventory() throws Exception {
        // Issues a GET request and checks the returned JSON
        mvc.perform(get("/api/inventory"))
            .andExpect(content().string("{\"id\":1,\"items\":[]}"))
            .andExpect(status().isOk());

        // Creates a new List of Items and adds new Items
        final List<ItemDto> items2 = new ArrayList<ItemDto>();
        items2.add(new ItemDto(null, "Coffee", 5, null, 1.00));
        items2.add(new ItemDto(null, "Mint", 5, null, 1.00));

        // Creates a new InventoryDto with the new List of Items
        final InventoryDto updatedInventory = new InventoryDto(1L, items2);

        // Issues a PUT request with the created InventoryDto and checks the returned JSON
        mvc.perform(put("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.asJsonString(updatedInventory))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].name").value("Coffee"))
            .andExpect(jsonPath("$.items[0].amount").value(5));
            // Removed the assertion for the unit field since it does not exist
    }


    /**
     * Tests the POST /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testAddItem () throws Exception {
        mvc.perform( get( "/api/inventory" ) ).andExpect( jsonPath( "$.items" ).isArray() )
                .andExpect( status().isOk() );

        final List<ItemDto> items2 = new ArrayList<ItemDto>();
        items2.add( new ItemDto( null, "Coffee", 5, null, 1.00 ) );
        items2.add( new ItemDto( null, "Mint", 5, null, 1.00 ) );
        final InventoryDto updatedInventory = new InventoryDto( 1L, items2 );

        mvc.perform( put( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedInventory ) ).accept( MediaType.APPLICATION_JSON ) );

        final ItemDto newItem = new ItemDto( null, "Milk", 100, null, 1.00 );
        mvc.perform( post( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newItem ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        final ItemDto newInvalidItem = new ItemDto( null, "Chocolate", -10, null, 1.00 );
        mvc.perform( post( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newInvalidItem ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );

        final ItemDto newDuplicateItem = new ItemDto( null, "Milk", 200, null, 1.00 );
        mvc.perform( post( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newDuplicateItem ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );
    }

}
