/**
 * ViewIngredientsModal.jsx
 * 
 * This is the modal for viewing the ingredients of a recipe
 * 
 * @author: Daniel Shea (dgshea)
 */

import React, { useState, useEffect } from 'react';
import { Modal, Box, Typography, Button, Card } from '@mui/material';

const ViewIngredientsModal = ({ open, handleClose, recipe }) => {
    const [ingredients, setIngredients] = useState(recipe.ingredients)

    useEffect(() => {
        setIngredients(recipe.ingredients)
    }, [recipe])

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
            }}>
                <Card sx={{ padding: 2 }}>
                    {ingredients.length > 0 ? ingredients.map((ingredient) => {
                        return (
                            <>
                                <Typography variant="h2">
                                    {ingredient.name}
                                </Typography>
                                <Typography variant="body1">
                                    {ingredient.amount} {ingredient.unit}
                                </Typography>
                            </>
                        )
                    }) : <Typography variant="body1">No ingredients</Typography>}
                </Card>

                <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                    <Button type="submit" variant="contained" color="success" onClick={handleClose} >Done</Button>
                </Box>
            </Box>
        </Modal>
    );
};

export default ViewIngredientsModal;
