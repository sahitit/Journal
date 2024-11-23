package edu.ncsu.csc326.wolfcafe.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.ItemService;

/**
 * Controller for CoffeeMaker's inventory. The inventory is a singleton; there's
 * only one row in the database that contains the current inventory for the
 * system.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/inventory" )
public class InventoryController {

    /** Connection to inventory service for manipulating the Inventory model. */
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ModelMapper      modelMapper;

    /**
     * Connection to item service for manipulating the Item model. Currently
     * unused.
     */
    @Autowired
    private ItemService      itemService;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @return response to the request
     */
    @GetMapping
    public ResponseEntity<InventoryDto> getInventory () {
        // Gets the current Inventory as an InventoryDto
        final InventoryDto inventoryDto = inventoryService.getInventory();
        // Returns a success with the InventoryDto
        return ResponseEntity.ok( inventoryDto );
    }

    /**
     * REST API endpoint to provide update access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @param inventoryDto
     *            an InventoryDto provided to update each relevant Item in the
     *            Inventory.
     * @return response to the request
     */
    @PutMapping
    public ResponseEntity<InventoryDto> updateInventory ( @RequestBody final InventoryDto inventoryDto ) {
        // Checks if the provided InventoryDto has Items
        if ( inventoryDto.getItems() != null ) {
            // Loops through each Item in the InventoryDto
            for ( final ItemDto i : inventoryDto.getItems() ) {
                // Checks that the current Item has a positive amount
                // field
                if ( i.getAmount() <= 0 ) {
                    // If any Item in the InventoryDto has an invalid
                    // amount field, returns a conflict
                    return new ResponseEntity<>( inventoryDto, HttpStatus.CONFLICT );
                }
            }
            final InventoryDto savedInventoryDto = inventoryService.updateInventory( inventoryDto );
            return ResponseEntity.ok( savedInventoryDto );
        }
        else {
            // If the Items list in the InventoryDto is null, returns a
            // conflict
            return new ResponseEntity<>( inventoryDto, HttpStatus.CONFLICT );
        }
    }

    /**
     * REST API endpoint to provide update access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @param itemDto
     *            a new ItemDto with data to add to the Inventory
     * @return response to the request
     */
    @PostMapping
    public ResponseEntity<ItemDto> addItem ( @RequestBody final ItemDto itemDto ) {
    	//Checks if the ItemDto has an amount less than or equal to 0
        if (itemDto.getAmount() <= 0) {
            // If the ItemDto has an invalid amount field, returns a conflict
            return new ResponseEntity<>(itemDto, HttpStatus.CONFLICT);
        }

        //Gets the current Inventory as an InventoryDto
        final InventoryDto inv = inventoryService.getInventory();

        //Gets the list of Items from the Inventory, initializing a new list if it's null
        List<ItemDto> newList = inv.getItems() != null ? new ArrayList<>(inv.getItems()) : new ArrayList<>();

        //Loops through each Item in the list of Items
        for (final ItemDto item : newList) {
            // Checks if the current Item's name is the same as the name in the ItemDto
            if (item.getName().equals(itemDto.getName())) {
                // If the name is a duplicate name, returns a conflict
                return new ResponseEntity<>(itemDto, HttpStatus.CONFLICT);
            }
        }

        //adds the ItemDto to the list of Items directly
        newList.add(itemDto);
        //Updates the Inventory's list of Items with the new list
        inv.setItems(newList);
        //Uses the InventoryService to update the stored Inventory
        inventoryService.updateInventory(inv);

        //Returns a success with the original ItemDto
        return ResponseEntity.ok(itemDto);
    }

}
