package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.Item;

/**
 * Tests Inventory repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE ) // Use the actual database
                                                      // for testing
class InventoryRepositoryTest {

    /** Reference to inventory repository */
    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        inventoryRepository.deleteAll();

        // Create and save items
        final Item coffee = new Item( null, "Coffee Beans", 10, "Premium roasted coffee beans", 12.99 );
        final Item sugar = new Item( null, "Sugar", 5, "Granulated sugar", 2.50 );

        // Create and save inventory
        final Inventory inventory = new Inventory();
        inventory.addItemToInventory( coffee );
        inventory.addItemToInventory( sugar );
        inventoryRepository.save( inventory );
    }

    /**
     * Tests adding an item to inventory and retrieving it by name.
     */
    @Test
    public void testAddItemToInventory () {
        // Retrieve the inventory from the repository
        final Inventory inventory = inventoryRepository.findAll().get( 0 );

        // Add a new item to inventory
        final Item milk = new Item( null, "Milk", 20, "Fresh dairy milk", 1.99 );
        inventory.addItemToInventory( milk );
        inventoryRepository.save( inventory ); // Save the updated inventory

        // Retrieve updated inventory and check if the new item is added
        final Inventory updatedInventory = inventoryRepository.findById( inventory.getId() ).get();
        assertAll( "Inventory contents", () -> assertEquals( 3, updatedInventory.getItems().size() ), // Verify
                                                                                                      // 3
                                                                                                      // items
                                                                                                      // in
                                                                                                      // inventory
                () -> assertEquals( "Milk", updatedInventory.getItemByName( "Milk" ).getName() ), // Check
                                                                                                  // new
                                                                                                  // item
                                                                                                  // by
                                                                                                  // name
                () -> assertEquals( 20, updatedInventory.getItemByName( "Milk" ).getAmount() ) // Verify
                                                                                               // amount
                                                                                               // of
                                                                                               // milk
        );
    }

    /**
     * Tests removing an item from inventory.
     */
    @Test
    public void testRemoveItemFromInventory () {
        // Retrieve the inventory from the repository
        final Inventory inventory = inventoryRepository.findAll().get( 0 );

        // Remove an item (e.g., Coffee Beans)
        final Item coffee = inventory.getItemByName( "Coffee Beans" );
        inventory.removeItemFromInventory( coffee );
        inventoryRepository.save( inventory ); // Save the updated inventory

        // Retrieve updated inventory and check if the item was removed
        final Inventory updatedInventory = inventoryRepository.findById( inventory.getId() ).get();
        assertAll( "Inventory contents", () -> assertEquals( 1, updatedInventory.getItems().size() ), // Only
                                                                                                      // 1
                                                                                                      // item
                                                                                                      // should
                                                                                                      // remain
                () -> assertTrue( updatedInventory.getItemByName( "Coffee Beans" ) == null ) // Ensure
                                                                                             // coffee
                                                                                             // was
                                                                                             // removed
        );
    }

    /**
     * Tests the InventoryRepository.findById method for an invalid ID.
     */
    @Test
    public void testGetInventoryByIdInvalid () {
        final Optional<Inventory> inventory = inventoryRepository.findById( 999L ); // Non-existing
                                                                                    // ID
        assertTrue( inventory.isEmpty() );
    }
}
