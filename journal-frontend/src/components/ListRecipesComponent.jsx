/*
import React, { useEffect, useState } from 'react'
import { getAllRecipes, deleteRecipe } from '../services/RecipesService'
import { useNavigate } from 'react-router-dom'
import ViewIngredientsModal from './ViewIngredientsModal'
import { Button, Container, Typography } from '@mui/material'
import AddRecipeModal from './AddRecipeModal'
import EditRecipeModal from './EditRecipeModal'
import MakeRecipeModal from './MakeRecipeModal'

/** Lists all the recipes and provide the option to create a new recipe
 * and delete an existing recipe.
 
const ListRecipesComponent = () => {

    const [recipes, setRecipes] = useState([])

    const [addRecipeModal, setAddRecipeModal] = useState(false);
    const [openIngredientModal, setOpenIngredientModal] = useState(false);
    const [openEditRecipeModal, setOpenEditRecipeModal] = useState(false);
	const [openMakeRecipeModal, setOpenMakeRecipeModal] = useState(false);
    const navigator = useNavigate();

    useEffect(() => {
        fetchAllRecipes()
    }, [])

    async function fetchAllRecipes() {
        const allRecipes = await getAllRecipes()
        setRecipes(allRecipes)
    }

    function removeRecipe(id) {
        deleteRecipe(id).then((response) => {
            getAllRecipes()
            fetchAllRecipes()
        }).catch(error => {
            console.error(error)
        })
    }

    return (
        <Container>
            <Typography variant="h5" className="text-center">List of Recipes</Typography>
            <AddRecipeModal open={addRecipeModal} handleClose={() => { setAddRecipeModal(false); fetchAllRecipes() }} />
            <table className="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Recipe Name</th>
                        <th>Recipe Price</th>
                        <th>Ingredients</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {
                        recipes.map((recipe) => {
                            return (
                                <>
                                    <ViewIngredientsModal key={recipe.name} open={openIngredientModal} handleClose={() => setOpenIngredientModal(false)} recipe={recipe} />
                                    <EditRecipeModal key={recipe.name} open={openEditRecipeModal} handleClose={() => setOpenEditRecipeModal(false)} recipeDto={recipe} />
									<MakeRecipeModal open={openMakeRecipeModal} handleClose={() => setOpenMakeRecipeModal(false)} recipeName={recipe.name} recipePrice={recipe.price} />
                                    <tr key={recipe.name}>
                                        <td>{recipe.name}</td>
                                        <td>{recipe.price}</td>
                                        <td>
                                            <Button variant="contained" onClick={() => setOpenIngredientModal(true)}>View Ingredients</Button>
                                        </td>
                                        <td>
											<Button 
											  style={{ 
											    backgroundColor: '#28a745',
											    color: '#fff',
											    border: '1px solid #28a745',
											    marginLeft: '10px',
											    padding: '5px 16px',
											    borderRadius: '4px'
											  }} 
											  onClick={() => setOpenMakeRecipeModal(true)}
											>
											  Make
											</Button>
											<Button
											style={{ 
												backgroundColor: '#007bff',
												color: '#fff',
												border: '1px solid #007bff',
												marginLeft: '10px',
												padding: '5px 16px',
												borderRadius: '4px'
											}}
											onClick={() => setOpenEditRecipeModal(true)}>Edit</Button>
                                            <button className="btn btn-danger" onClick={() => removeRecipe(recipe.id)}
                                                style={{ marginLeft: '10px' }}
                                            >Delete</button>
                                        </td>
                                    </tr>
                                </>
                            )
                        })
                    }
                </tbody>
            </table>
            <Button variant="contained" color="success" fullWidth sx={{ m: 2 }} onClick={() => setAddRecipeModal(true)}>
                Add Recipe
            </Button>
        </Container>
    )

}
*/
export default ListRecipesComponent