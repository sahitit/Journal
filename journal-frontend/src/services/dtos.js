/** DTOs for the Coffee Maker API
 * 
 * These should be used to create objects that are passed to and from the api
 * @author Daniel Shea
 */

export class InventoryDto {
    constructor(items) {
        this.items = items;
    }
}

export class ItemDto {
    constructor(name, amount, price) {
        this.name = name;
        this.amount = amount;
        this.price = price;
    }
}

export class OrderDto {
    constructor(id, name, items, status) {
        this.id = id;
        this.name = name;
        this.items = items;
        this.status = status;
    }
}

export class RecipeDto {
    constructor(id, name, price, items = []) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.items = items;
    }

    addItem(item) {
        this.items.push(item);
    }
}
