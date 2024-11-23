package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Implementation of the InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper         modelMapper;

//    @Autowired
//	private ItemRepository itemRepository;

    /**
     * Creates the inventory.
     *
     * @param inventoryDto
     *            inventory to create
     * @return updated inventory after creation
     */
    @Override
    public InventoryDto createInventory ( final InventoryDto inventoryDto ) {
    	
        if ( inventoryDto == null ) {
            throw new IllegalArgumentException( "InventoryDto cannot be null." );
        }
        
        // Maps the provided InventoryDto to an Inventory
        final Inventory inventory = modelMapper.map( inventoryDto, Inventory.class );
        // Saves the Inventory to the InventoryRepository
        final Inventory savedInventory = inventoryRepository.save( inventory );
        // Maps the Inventory to an InventoryDto and returns
        return modelMapper.map( savedInventory, InventoryDto.class );
    }

    /**
     * Returns the single inventory.
     *
     * @return the single inventory
     */
    @Override
    public InventoryDto getInventory () {
        // Gets a list of "all" Inventories (there should only ever be one)
        final List<Inventory> inventory = inventoryRepository.findAll();
        // Checks if there is an Inventory
        if ( inventory.size() == 0 ) {
            // If there is not an Inventory, creates a new InventoryDto with no
            // Items
            final InventoryDto newInventoryDto = new InventoryDto();
            newInventoryDto.setItems( new ArrayList<ItemDto>() );
            // Saves the InventoryDto and returns it
            final InventoryDto savedInventoryDto = createInventory( newInventoryDto );
            return savedInventoryDto;
        }
        // Returns the Inventory
        return modelMapper.map( inventory.get( 0 ), InventoryDto.class );
    }

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryDto
     *            values to update
     * @return updated inventory
     */
//    @Override
//    public InventoryDto updateInventory ( final InventoryDto inventoryDto ) {
//        // Gets the Inventory from the InventoryRepository
//        final Inventory inventory = inventoryRepository.findById( 1L ).orElseThrow(
//                () -> new ResourceNotFoundException( "Inventory does not exist with id of " + inventoryDto.getId() ) );
//        // Sets the Items in the Inventory to the Items in the
//        // InventoryDto
//        final List<Item> newItems = new ArrayList<Item>();
//        newItems.addAll( inventoryDto.getItems() );
//        inventory.setItems( newItems );
//        
//        // Saves the Inventory to the InventoryRepository
//        final Inventory savedInventory = inventoryRepository.save( inventory );
//        // Maps the Inventory to an InventoryDto and returns
//        return modelMapper.map( savedInventory, InventoryDto.class );
//    }
    
    @Override
    public InventoryDto updateInventory(final InventoryDto inventoryDto) {
    	// OLD
    	//        final Inventory inventory = inventoryRepository.findById(1L).orElseThrow(
//                () -> new ResourceNotFoundException("Inventory does not exist with id of " + inventoryDto.getId()));
//
//        // Clear current items in inventory
//        inventory.getItems().clear();
//
//        if (inventoryDto.getItems() != null) {
//            for (ItemDto itemDto : inventoryDto.getItems()) {
//                // Check if the item already exists
//                Optional<Item> existingItem = itemRepository.findByName(itemDto.getName());
//                Item item;
//
//                if (existingItem.isPresent()) {
//                    // Use existing item if found
//                    item = existingItem.get();
//                    // Optionally update its properties if necessary
//                    item.setAmount(itemDto.getAmount());
//                    item.setDescription(itemDto.getDescription());
//                    item.setPrice(itemDto.getPrice());
//                } else {
//                    // Create a new item if it does not exist
//                    item = new Item();
//                    item.setName(itemDto.getName());
//                    item.setAmount(itemDto.getAmount());
//                    item.setDescription(itemDto.getDescription());
//                    item.setPrice(itemDto.getPrice());
//                }
//
//                inventory.getItems().add(item);
//            }
//        }
//
//        // Save the updated inventory
//        final Inventory savedInventory = inventoryRepository.save(inventory);
//
//        // Return the updated InventoryDto
//        return modelMapper.map(savedInventory, InventoryDto.class);
    	// OLD ENDS
        final List<Inventory> inventoryList = inventoryRepository.findAll();
        if ( inventoryList.isEmpty() ) {
            throw new RuntimeException( "Inventory not found" );
        }

        final Inventory inventory = inventoryList.get( 0 );

        // Validate the new ingredient amounts
        for ( final ItemDto newItem : inventoryDto.getItems() ) {
            if ( newItem.getAmount() < 0 ) {
                throw new IllegalArgumentException(
                        "Ingredient amount cannot be negative for: " + newItem.getName() );
            }
        }

        // Update the existing inventory amounts or add new ingredients
        for ( final ItemDto newItem : inventoryDto.getItems() ) {
            final Item existingIngredient = inventory.getItems().stream()
                    .filter( ing -> ing.getName().equals( newItem.getName() ) ).findFirst().orElse( null );

            if ( existingIngredient != null ) {
                existingIngredient.setAmount( newItem.getAmount() );
            }         
            else {
                // Only add if the new ingredient amount is valid
                if ( newItem.getAmount() > 0 ) {
                    inventory.getItems().add( modelMapper.map(newItem, Item.class) );
                }
            }
        }
        final Inventory updatedInventory = inventoryRepository.save( inventory );
        return modelMapper.map(updatedInventory, InventoryDto.class);
    }

       

}
