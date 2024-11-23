package edu.ncsu.csc326.wolfcafe.dto;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;



/**
 * Used to transfer Order data between the client and server. This class will
 * serve as the response in the REST API.
 */
public class OrderDto {

    /** Order Id */
    private Long          id;

    /** Order name */
    private String        name;

    /** Order Items */
//    @OneToMany ( cascade = CascadeType.MERGE )
//    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    
    private List<ItemDto> items;

    /** State pattern context tracker */
    private OrderStatus status;

    /**
     * Default constructor for Order.
     */
    public OrderDto() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.ACTIVE;
    }

    /**
     * Creates order from field values.
     *
     * @param id
     *            order's id
     * @param name
     *            order's name
     * @param price
     *            order's price
     */
    public OrderDto ( final Long id, final String name ) {
        super();
        this.id = id;
        this.name = name;
        this.items = new ArrayList<ItemDto>();
        this.status = OrderStatus.ACTIVE;
    }
    
    

    /**
     * Creates order from field values.
     *
     * @param id
     *            order's id
     * @param name
     *            order's name
     * @param price
     *            order's price
     * @param items
     *            order's items
     */
    public OrderDto(final Long id, final String name, final List<ItemDto> items, final OrderStatus status) {
        this.id = id;
        this.name = name;
        this.items = items != null ? items : new ArrayList<>();
        this.status = status;
    }

    /**
     * Gets the order id.
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }
    

	/**
	 * @return the status
	 */
	public OrderStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	/**
     * Order id to set.
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets order's name
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Order name to set.
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Add one item to order
     *
     * @param item
     *            item to be added
     */
    public void addItem ( final ItemDto item ) {

        if ( items == null ) {
            items = new ArrayList<ItemDto>();
        }
        items.add( item );
    }

    /**
     * Returns list of items of a order
     *
     * @return items contained in order
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
    public void setItems ( final List<ItemDto> newItems ) {
        if ( newItems == null ) {
            this.items = new ArrayList<>(); // Re-initialize if null
        }
        else {
            this.items.clear();
            this.items.addAll( newItems );
        }
    }


	/**
     * Returns the total cost of all items in the order.
     *
     * @return The total cost of all items in the order.
     */
    public Double getOrderCost () {
        Double totalCost = 0.00;
        for ( final ItemDto item : items ) {
            totalCost += item.getAmount() * item.getPrice();
        }
        return totalCost;
    }
    
    /**
     * Removes an item from the oder.
     *
     * @param item
     *            the item to remove
     */
    public void removeItem ( final ItemDto item ) {
        this.items.remove( item );
    }

}
