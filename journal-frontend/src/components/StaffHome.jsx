import React, { useState, useContext, useEffect } from 'react';
import { getAllItems } from '../services/ItemService';
import { isUserLoggedIn } from '../services/AuthService';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

/**
 * This component displays a list of items with their name, description, and price.
 * The "Add to Cart" button and quantity have been removed.
 * 
 * @author: Gabriel Perri
 */
const StaffHome = () => {

    const [items, setItems] = useState([]);

	const { backgroundColor } = useContext(BackgroundCustomizationContext);
	
    const loggedIn = isUserLoggedIn();

    // Get items when component is opened
    useEffect(() => {
        listItems();
    }, []);

    function listItems() {
        getAllItems().then((response) => {
            setItems(response.data);
        }).catch(error => {
            console.error(error);
        });
    }

    return (
        <div className='container'>
			<br /> <br />
			<h1 className="text-center" style={{ fontFamily: 'Georgia, serif', color: backgroundColor, marginBottom: '20px' }}>
			    WolfCafe
			</h1>
            <h2 className='text-center'>Items List</h2>
            <div>
                <table className='table table-bordered table-striped'>
                    <thead>
                        <tr>
                            <th>Item Name</th>
                            <th>Description</th>
                            <th>Price</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            items.map((item) =>
                                <tr key={item.id}>
                                    <td>{item.name}</td>
                                    <td>{item.description}</td>
                                    <td>{item.price}</td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default StaffHome;
