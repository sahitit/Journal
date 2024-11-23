package edu.ncsu.csc326.wolfcafe.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to transfer Inventory data between the client and server. This class
 * will serve as the response in the REST API.
 */
public class InventoryDto {

    /** id for inventory entry */
    private Long          id;

    /** List of Items */
    private List<ItemDto> items = new ArrayList<>();

    public InventoryDto () {
        // Default constructor
    }

    /**
     * Constructs an InventoryDto object from field values.
     *
     * @param id
     *            inventory id
     * @param items
     *            The items in the Inventory
     */
    public InventoryDto ( final Long id, final List<ItemDto> items ) {
        this.id = id;
        this.items = ( items != null ) ? items : new ArrayList<ItemDto>();
    }

    /**
     * Gets the inventory id.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Inventory id to set.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the items
     *
     * @return the items
     */
    public List<ItemDto> getItems () {
        return items;
    }

    /**
     * Sets the items
     *
     * @param newItems
     *            - list
     */
    public void setItems ( final List<ItemDto> items ) {
//        if ( newItems == null ) {
//            this.items = new ArrayList<>(); // Re-initialize if null
//        }
//        else {
//            this.items.clear();
//            this.items.addAll( newItems );
//        }
    	this.items = items;
    }

    /**
     * Adds an ingredient to the inventory.
     *
     * @param ingredient
     *            the ingredient to add
     */
    public void addItemToInventory ( final ItemDto item ) {
        if ( this.items != null ) {
            this.items.add( item );
        }
    }

    /**
     * Removes an ingredient from the inventory.
     *
     * @param ingredient
     *            the ingredient to remove
     */
    public void removeItemFromInventory ( final ItemDto item ) {
        if ( this.items != null ) {
            this.items.remove( item );
        }
    }

    /**
     * Gets an ingredient from the inventory by name.
     *
     * @param name
     *            the name of the ingredient to retrieve
     * @return the ingredient if found, otherwise null
     */
    public ItemDto getItemByName ( final String name ) {
        if ( this.items != null ) {
            for ( final ItemDto item : items ) {
                if ( item.getName().equalsIgnoreCase( name ) ) {
                    return item; // Return the first matching ingredient
                }
            }
        }
        return null; // Return null if no match is found
    }
}
