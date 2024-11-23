/**
 * LandingPage.jsx
 * 
 * This is the landing page for coffee maker 
 * It has the barebone wireframes of the page. The action buttons don't do anything yet
 * 
 * @author: Daniel Shea (dgshea)
 */

import React from 'react';
import {
    Container,
    Typography,
    Button,
    Grid,
    Paper,
    Box
} from '@mui/material';
import { styled } from '@mui/system';

const StyledPaper = styled(Paper)(({ theme }) => ({
    padding: theme.spacing(3),
    textAlign: 'center',
    color: theme.palette.text.secondary,
}));

const ActionButtons = () => {
    const handleMakeBeverage = () => {
        console.log('Make Beverage');
    }

    const handleAddRecipe = () => {
        console.log('Add Recipe');
    }

    const handleDeleteRecipe = () => {
        console.log('Delete Recipe');
    }

    const handleEditRecipe = () => {
        console.log('Edit Recipe');
    }

    const handleUpdateInventory = () => {
        console.log('Update Inventory');
    }

    const handleAddIngredient = () => {
        console.log('Add Ingredient');
    }

    return (
        <Grid container spacing={3} justifyContent="flex-start" alignItems="flex-start">
            <Grid item xs={12}>
                <Button variant="contained" color="secondary" sx={{ width: '100%' }}
                    onClick={handleMakeBeverage}>
                    Make Beverage
                </Button>
            </Grid>
            <Grid item xs={4}>
                <Button variant="contained" color="secondary" sx={{ width: '100%' }}
                    onClick={handleAddRecipe}>
                    Add Recipe
                </Button>
            </Grid>
            <Grid item xs={4}>
                <Button variant="contained" color="secondary" sx={{ width: '100%' }}
                    onClick={handleDeleteRecipe}>
                    Delete Recipe
                </Button>
            </Grid>
            <Grid item xs={4}>
                <Button variant="contained" color="secondary" sx={{ width: '100%' }}
                    onClick={handleEditRecipe}>
                    Edit Recipe
                </Button>
            </Grid>
            <Grid item xs={6}>
                <Button variant="contained" color="secondary" sx={{ width: '100%' }}
                    onClick={handleUpdateInventory}>
                    Update Inventory
                </Button>
            </Grid>
            <Grid item xs={6}>
                <Button variant="contained" color="secondary" sx={{ width: '100%' }}
                    onClick={handleAddIngredient}>
                    Add Ingredient
                </Button>
            </Grid>
        </Grid>
    )
}

const LandingPage = () => {
    return (
        <Container maxWidth="lg" sx={{
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '80vh'
        }}>
            <Box sx={{ textAlign: 'center' }}>
                <Typography variant="h1" component="h1" gutterBottom>
                    Welcome to Coffee Maker!
                </Typography>
                <Typography variant="h5" component="h2" gutterBottom>
                    To get started, select an option below
                </Typography>
                <ActionButtons />
            </Box>
        </Container>
    );
};

export default LandingPage;
