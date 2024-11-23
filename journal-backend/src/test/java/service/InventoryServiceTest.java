package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryServiceImpl.
 */
@SpringBootTest
public class InventoryServiceTest {

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService inventoryService;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager    entityManager;

    /**
     * Sets up the test case. We assume only one inventory row. Because
     * inventory is treated as a singleton (only one row), we must truncate for
     * auto increment on the id to work correctly.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        //entityManager.createNativeQuery( "DELETE FROM inventory_items" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();
        entityManager.createNativeQuery( "ALTER TABLE item AUTO_INCREMENT = 0" ).executeUpdate();
        entityManager.createNativeQuery( "ALTER TABLE inventory AUTO_INCREMENT = 0" ).executeUpdate();
    }

     /**
     * Tests InventoryService.createInventory().
     */
     @Test
     @Transactional
     public void testCreateInventory () {
     final List<ItemDto> items = new ArrayList<ItemDto>();
     items.add( new ItemDto( null, "Coffee", 500, null, 1.00 ) );
     items.add( new ItemDto( null, "Salt", 200, null, 1.00 ) );
     final InventoryDto inventoryDto = new InventoryDto( 1L, items );
    
     final InventoryDto createdInventoryDto =
     inventoryService.createInventory( inventoryDto );
     // Check contents of returned InventoryDto
     assertAll( "InventoryDto contents", () -> assertEquals( 1L,
     createdInventoryDto.getId() ),
     () -> assertEquals( "Coffee", createdInventoryDto.getItems().get( 0
     ).getName() ),
     () -> assertEquals( 500, createdInventoryDto.getItems().get( 0
     ).getAmount() ),
     () -> assertEquals( "Salt", createdInventoryDto.getItems().get( 1
     ).getName() ),
     () -> assertEquals( 200, createdInventoryDto.getItems().get( 1
     ).getAmount() ) );
     }

    /**
     * Tests InventoryService.updateInventory()
     */
    @Test
    @Transactional
    public void testUpdateInventory () {

        final List<ItemDto> items = new ArrayList<ItemDto>();
        items.add( new ItemDto( 0L, "Coffee", 300, null, 1.00 ) );
        final InventoryDto inventoryDto = new InventoryDto( 1L, items );

        inventoryService.createInventory( inventoryDto );

        final InventoryDto newInventoryDto = inventoryService.getInventory();

        final List<ItemDto> items2 = new ArrayList<ItemDto>();
        items2.add( new ItemDto( null, "Coffee", 500, null, 1.00 ) );
        items2.add( new ItemDto( null, "Salt", 200, null, 1.00 ) );

        newInventoryDto.setItems( items2 );

        final InventoryDto updatedInventoryDto = inventoryService.updateInventory( newInventoryDto );
        assertAll( "InventoryDto contents", () -> assertEquals( 1L, updatedInventoryDto.getId() ),
                () -> assertEquals( "Coffee", updatedInventoryDto.getItems().get( 0 ).getName() ),
                () -> assertEquals( 500, updatedInventoryDto.getItems().get( 0 ).getAmount() ),
                () -> assertEquals( "Salt", updatedInventoryDto.getItems().get( 1 ).getName() ),
                () -> assertEquals( 200, updatedInventoryDto.getItems().get( 1 ).getAmount() ) );
    }

    @Test
    @Transactional
    public void testAddItemToInventory () {
        final InventoryDto inventoryDto = new InventoryDto();
        final ItemDto item = new ItemDto( null, "Coffee", 100, null, 1.50 );

        // Add item to inventory
        inventoryDto.addItemToInventory( item );

        assertEquals( 1, inventoryDto.getItems().size(), "Item was not added correctly" );
        assertEquals( "Coffee", inventoryDto.getItems().get( 0 ).getName(), "Incorrect item added" );
    }

    // Test for InventoryDto.removeItemFromInventory()
    @Test
    @Transactional
    public void testRemoveItemFromInventory () {
        final InventoryDto inventoryDto = new InventoryDto();
        final ItemDto item = new ItemDto( 0L, "Coffee", 100, null, 1.50 );

        // Add item to inventory
        inventoryDto.addItemToInventory( item );

        // Remove item from inventory
        inventoryDto.removeItemFromInventory( item );

        assertTrue( inventoryDto.getItems().isEmpty(), "Item was not removed correctly" );
    }

    // Test for InventoryDto.getItemByName()
    @Test
    @Transactional
    public void testGetItemByName () {
        final InventoryDto inventoryDto = new InventoryDto();
        final ItemDto item1 = new ItemDto( 0L, "Coffee", 100, null, 1.50 );
        final ItemDto item2 = new ItemDto( 1L, "Salt", 200, null, 1.00 );

        // Add items to inventory
        inventoryDto.addItemToInventory( item1 );
        inventoryDto.addItemToInventory( item2 );

        // Get item by name
        final ItemDto foundItem = inventoryDto.getItemByName( "Coffee" );

        assertEquals( "Coffee", foundItem.getName(), "The item found is incorrect" );
        assertNull( inventoryDto.getItemByName( "Tea" ), "Non-existing item should return null" );
    }

}
