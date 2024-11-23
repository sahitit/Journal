import React, { useState } from 'react';
import { Modal, Box, Typography, TextField, Button } from '@mui/material';
import { makeRecipe } from '../services/MakeRecipeService';

const MakeRecipeModal = ({ open, handleClose, recipeName, recipePrice }) => {
    const [amtPaid, setAmtPaid] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    const handleAmtPaidChange = (event) => {
        setAmtPaid(event.target.value);
        setErrorMessage('');  // Clear error message when user starts typing
        setSuccessMessage(''); // Clear success message when user starts typing
    };

	const handleMakeRecipe = (event) => {
	    event.preventDefault();
	    const paymentValue = parseFloat(amtPaid);

	    // Input validation
	    if (isNaN(paymentValue) || paymentValue <= 0) {
	        setErrorMessage('Invalid payment.');
	        return;
	    } else if (paymentValue < recipePrice) {
	        setErrorMessage('Not enough payment. Refunded.');
	        return;
	    }

	    // Attempt to make the recipe
	    makeRecipe(recipeName, paymentValue).then((response) => {
	        const change = paymentValue - recipePrice;
	        setSuccessMessage(`${recipeName} made! Change: ${change}`);
	        setErrorMessage(''); // Clear any previous errors
	    }).catch((error) => {
	        if (error.response && error.response.status === 400) {
	            // Check if the error is due to inventory out of stock
	            setErrorMessage('Inventory out of stock. Refunded.');
	        } else {
	            // Other unexpected errors
	            console.error(error);
	            setErrorMessage('An unexpected error occurred. Please try again.');
	        }
	    });
	};


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
                <Typography id="make-recipe-modal" variant="h6" component="h2" gutterBottom>
                    {recipeName}
                </Typography>
                
                {/* Conditionally render success or error messages */}
                {successMessage && (
                    <Typography color="success.main" gutterBottom>
                        {successMessage}
                    </Typography>
                )}
                {errorMessage && (
                    <Typography color="error.main" gutterBottom>
                        {errorMessage}
                    </Typography>
                )}

                <form onSubmit={handleMakeRecipe}>
                    <TextField
                        fullWidth
                        label="Payment"
                        type="number"
                        margin="normal"
                        required
                        value={amtPaid}
                        onChange={handleAmtPaidChange}
                        error={Boolean(errorMessage === 'Invalid payment.' || errorMessage === 'Not enough payment. Refunded.')}
                        helperText={
                            errorMessage === 'Invalid payment.' || errorMessage === 'Not enough payment. Refunded.'
                                ? errorMessage
                                : ''
                        }
                    />
                    <Box sx={{ mt: 2, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
                        <Button onClick={handleClose} color="error">Cancel</Button>
                        <Button type="submit" variant="contained" color="success">Make</Button>
                    </Box>
                </form>
            </Box>
        </Modal>
    );
};

export default MakeRecipeModal;
