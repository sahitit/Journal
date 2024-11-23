package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Inventory {

	 /** id for inventory entry */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    /** List of Items in inventory */
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    @JoinColumn ( name = "inventory_id" )
    private List<Item> items = new ArrayList<>();  // Initialize to avoid null issues

    
  /**
   * Empty constructor for Hibernate
   */
    public Inventory() {
        // Default constructor for Hibernate
    }

    
    /**
	 * Creates an Inventory with an id field
	 *
	 * @param id
	 *            inventory's id
	 */
    public Inventory(final Long id) {
        this(id, new ArrayList<Item>());
    }

    /**
     * Constructor to create an Inventory with a specified ID and items list.
     *
     * @param id Inventory's ID
     * @param items List of items in inventory
     */
    public Inventory(final Long id, final List<Item> items) {
        this.id = id;
        this.items = (items != null) ? items : new ArrayList<Item>();
    }

    
    /**
   * Returns the ID of the entry in the DB
   *
   * @return long
   */
    public Long getId() {
        return id;
    }

    
    /**
   * Set the ID of the Inventory (Used by Hibernate)
   *
   * @param id
   *            the ID
   */
    public void setId(final Long id) {
        this.id = id;
    }

    
    /**
   * Returns the List of Items stored in the inventory
   *
   * @return List<Item>
   */
    public List<Item> getItems() {
        return items;
    }
    
    
    /**
   * Set the List of Items stored in the inventory
   *
   * @param items
   *            The List of Items
   */
    public void setItems(final List<Item> items) {
//        if (newItems == null) {
//            this.items = new ArrayList<>();  // Re-initialize if null
//        } else {
//            this.items.clear();
//            this.items.addAll(newItems);
//        }
    	this.items = items;
    }
    
    /**
     * Adds an ingredient to the inventory.
     *
     * @param ingredient
     *            the ingredient to add
     */
    public void addItemToInventory ( final Item item ) {
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
    public void removeItemFromInventory ( final Item item ) {
    	if (this.items != null) {
            this.items = new ArrayList<>(this.items); // Ensure it's modifiable
            this.items.remove(item);
        }
    }
    
    /**
     * Gets an ingredient from the inventory by name.
     *
     * @param name
     *            the name of the ingredient to retrieve
     * @return the ingredient if found, otherwise null
     */
    public Item getItemByName ( final String name ) {
        if ( this.items != null ) {
            for ( final Item item : items ) {
                if ( item.getName().equalsIgnoreCase( name ) ) {
                    return item; // Return the first matching ingredient
                }
            }
        }
        return null; // Return null if no match is found
    }
}

