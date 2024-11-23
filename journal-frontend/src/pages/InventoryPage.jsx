/**
 * InventoryPage.jsx
 * 
 * This is the page for making actions to the inventory on coffee maker
 * 
 * @author: Daniel Shea (dgshea)
 */

import { Container, Box, Typography, List, ListItem, Grid, Button, FormControl, Select, MenuItem, TextField } from '@mui/material';
import { useState, useEffect } from 'react';
import AddItemModal from '../components/AddItemModal';
import { getInventory } from '../services/InventoryService';
import { units } from '../services/units';
import { updateInventory } from '../services/InventoryService';
import { ItemDto } from '../services/dtos';
import { InventoryDto } from '../services/dtos'

const InventoryPage = () => {


    const [items, setItems] = useState([]);
    const [open, setOpen] = useState(false);

    const [updateInventoryButton, setUpdateInventoryButton] = useState(false);

    useEffect(() => {
        fetchAllItems()
    }, [])

    function fetchAllItems() {
        getInventory().then((response) => {
            response.items ? setItems(response.items) : setItems([])
        }).catch(error => {
            console.error(error)
        })
    }

    const handleAddItem = () => {
        setOpen(true);
    };


    const handleCloseAddItemsModal = () => {
        fetchAllItems()
        setOpen(false);
    };

    const handleUpdateInventory = () => {
        const itemDtos = items.map((item) => {
            return new ItemDto(item.name, item.amount, item.unit)
        })
        updateInventory(new InventoryDto(itemDtos)).then((response) => {
            console.log(response)
        }).catch((error) => {
            console.error(error)
        })
        setUpdateInventoryButton(false);
    }

    const ItemsList = () => {
        const handleAmountChange = (index, value) => {
            const newItems = [...items];
            newItems[index].amount = Number(value);
            setItems(newItems);
            setUpdateInventoryButton(true);
        };

        const handleUnitChange = (index, value) => {
            const newItems = [...items];
            newItems[index].unit = value;
            setItems(newItems);
            setUpdateInventoryButton(true);
        };

        return (
            <List sx={{
                backgroundColor: '#a2d2ff',
                maxHeight: 300,
                overflow: 'auto'
            }}>
                {items.length > 0 ? items.map((item, index) => {
                    return (
                        <ListItem key={item.name}>
                            <Grid container>
                                <Grid item xs={6} container justifyContent="center" alignItems="center">
                                    <Typography variant="h6">{item.name}</Typography>
                                </Grid>
                                <Grid item xs={6} container justifyContent="center" alignItems="center">
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <TextField
                                            type="number"
                                            value={item.amount}
                                            onChange={(e) => handleAmountChange(index, e.target.value)}
                                            size="small"
                                            sx={{ width: '80px', mr: 1 }}
                                        />
                                        <FormControl size="small" sx={{ minWidth: 80 }}>
                                            <Select
                                                value={item.unit}
                                                onChange={(e) => handleUnitChange(index, e.target.value)}
                                            >
                                                {Object.entries(units).map(([key, value]) => (
                                                    <MenuItem key={key} value={value}>{value}</MenuItem>
                                                ))}
                                            </Select>
                                        </FormControl>
                                    </Box>
                                </Grid>
                            </Grid>
                        </ListItem>
                    )
                }) : <ListItem>No items found</ListItem>}
            </List>
        )
    }

    return (
        <Container maxWidth="lg">
            <AddItemModal open={open} handleClose={handleCloseAddItemsModal} />
            <Box sx={{ my: 4 }}>
                <Typography variant="h4">Inventory</Typography>
            </Box>
            <Box sx={{ mt: 2 }}>
                <ItemsList />
            </Box>
            <Box sx={{ mt: 3, display: 'flex', justifyContent: 'center', gap: 2 }}>
                <Button variant="contained" color="warning" fullWidth disabled={!updateInventoryButton} onClick={handleUpdateInventory}>
                    Update Inventory
                </Button>
                <Button variant="contained" color="success" fullWidth onClick={handleAddItem}>
                    Add Item
                </Button>
            </Box>
        </Container>
    );
}

export default InventoryPage;
