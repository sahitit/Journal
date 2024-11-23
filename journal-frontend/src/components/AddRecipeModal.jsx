/**
 * AddRecipeModal.jsx
 * 
 * This is the modal for adding a recipe
 * It allows users to create a recipe with a name, price, and items
 * 
 * @author: Daniel Shea (dgshea)
 

import React, { useState, useEffect } from 'react';
import { Modal, Box, Typography, TextField, Button, FormControl, InputLabel, Select, MenuItem, List, ListItem, ListItemText, IconButton } from '@mui/material';
import { createRecipe } from '../services/RecipesService';
import { RecipeDto, ItemDto } from '../services/dtos';
import { getInventory } from '../services/InventoryService';
import DeleteIcon from '@mui/icons-material/Delete';

const AddRecipeModal = ({ open, handleClose }) => {
    const [recipeName, setRecipeName] = useState('');
    const [recipePrice, setRecipePrice] = useState('');
    const [items, setItems] = useState([]);
    const [availableItems, setAvailableItems] = useState([]);
    const [selectedItem, setSelectedItem] = useState('');
    const [selectedAmount, setSelectedAmount] = useState('');

    useEffect(() => {
        fetchAvailableItems();
    }, []);

    const fetchAvailableItems = async () => {
        try {
            const inventory = await getInventory();
            setAvailableItems(inventory.items);
        } catch (error) {
            console.error('Failed to fetch items:', error);
        }
    };

    const handleRecipeNameChange = (event) => {
        setRecipeName(event.target.value);
    }

    const handleRecipePriceChange = (event) => {
        setRecipePrice(event.target.value);
    }

    const handleItemChange = (event) => {
        setSelectedItem(event.target.value);
    }

    const handleAmountChange = (event) => {
        setSelectedAmount(event.target.value);
    }

    const handleAddItem = () => {
        if (selectedItem && selectedAmount) {
            const newItem = new ItemDto(
                selectedItem.name,
                selectedAmount,
                selectedItem.unit
            );
            setItems([...items, newItem]);
            setSelectedItem('');
            setSelectedAmount('');
        }
    }

    const handleRemoveItem = (index) => {
        const updatedItems = items.filter((_, i) => i !== index);
        setItems(updatedItems);
    }

    const handleAddNewRecipe = (event) => {
        event.preventDefault();
        const recipeDto = new RecipeDto(null, recipeName, recipePrice, items);
        createRecipe(recipeDto).then((response) => {
            handleClose();
        }).catch((error) => {
            console.error(error);
        });
    }

    return (
        <Modal
            open={open}
            onClose={handleClose}
        >
            <Box sx={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                width: 400,
                bgcolor: 'background.paper',
                p: 4,
                maxHeight: '90vh',
                overflowY: 'auto',
            }}>
                <Typography id="add-recipe-modal" variant="h6" component="h2" gutterBottom>
                    Create Your Recipe
                </Typography>
                <FormControl component="form" onSubmit={handleAddNewRecipe} fullWidth>
                    <TextField
                        fullWidth
                        label="Recipe Name"
                        margin="normal"
                        required
                        value={recipeName}
                        onChange={handleRecipeNameChange}
                    />
                    <TextField
                        fullWidth
                        label="Recipe Price"
                        type="number"
                        margin="normal"
                        required
                        value={recipePrice}
                        onChange={handleRecipePriceChange}
                    />
                    <Typography variant="subtitle1" gutterBottom>
                        Add Items
                    </Typography>
                    <FormControl fullWidth margin="normal">
                        <InputLabel id="item-select-label">Item</InputLabel>
                        <Select
                            labelId="item-select-label"
                            id="item-select"
                            value={selectedItem}
                            label="Item"
                            onChange={handleItemChange}
                        >
                            {availableItems.map((item) => (
                                <MenuItem key={item.id} value={item}>{item.name}</MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <TextField
                        fullWidth
                        label="Amount"
                        type="number"
                        margin="normal"
                        value={selectedAmount}
                        onChange={handleAmountChange}
                    />
                    <Button onClick={handleAddItem} variant="outlined" fullWidth>
                        Add Item
                    </Button>
                    <List>
                        {items.map((item, index) => (
                            <ListItem key={index} secondaryAction={
                                <IconButton edge="end" aria-label="delete" onClick={() => handleRemoveItem(index)}>
                                    <DeleteIcon />
                                </IconButton>
                            }>
                                <ListItemText primary={`${item.name} - ${item.amount} ${item.unit}`} />
                            </ListItem>
                        ))}
                    </List>
                    <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                        <Button onClick={handleClose} color="error">Cancel</Button>
                        <Button type="submit" variant="contained" color="success">Add Recipe</Button>
                    </Box>
                </FormControl>
            </Box>
        </Modal>
    );
};
*/

export default AddRecipeModal;
