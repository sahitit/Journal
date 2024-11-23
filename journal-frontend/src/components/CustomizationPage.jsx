import React, { useContext, useState } from 'react';
import { BackgroundCustomizationContext } from '../context/BackgroundCustomizationContext';

const CustomizationPage = () => {
    const { backgroundColor, setBackgroundColor } = useContext(BackgroundCustomizationContext);
    const [newColor, setNewColor] = useState(backgroundColor);

    const handleColorChange = (e) => {
        setNewColor(e.target.value);
    };

    const applyNewColor = () => {
        setBackgroundColor(newColor);
        alert('Background color updated successfully!');
    };

    const resetToDefaultColor = () => {
        const defaultColor = '#927d61'; // Default background color
        setBackgroundColor(defaultColor);
        setNewColor(defaultColor); // Update the input field to reflect the reset
        alert('Background color reset to default!');
    };

    return (
        <div className='container'>
            <br /> <br />
            <h1 style={{ fontSize: '2rem', textAlign: 'center', color: '#4a4a4a', marginBottom: '20px' }}>
                Marketer's Control Panel
            </h1>

            <h2 style={{ fontSize: '1.5rem', marginBottom: '20px', textAlign: 'center' }}>
                Customize Background Color
            </h2>

            {/* Row for Color Selector and Apply Button */}
            <div
                className="form-group"
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '10px',
                }}
            >
                <label htmlFor="backgroundColor" style={{ fontSize: '1rem', fontWeight: 'bold' }}>
                    Select Background Color:
                </label>
                <input
                    type="color"
                    id="backgroundColor"
                    value={newColor}
                    onChange={handleColorChange}
                    className="form-control"
                    style={{ width: '60px', height: '40px', border: 'none', cursor: 'pointer' }}
                />
                <button
                    className="btn btn-primary"
                    onClick={applyNewColor}
                    style={{ padding: '10px 20px', fontSize: '1rem' }}
                >
                    Apply Color
                </button>
            </div>

            {/* Reset Button */}
            <div
                style={{
                    marginTop: '20px',
                    display: 'flex',
                    justifyContent: 'center',
                }}
            >
                <button
                    className="btn btn-secondary"
                    onClick={resetToDefaultColor}
                    style={{ padding: '10px 20px', fontSize: '1rem' }}
                >
                    Reset to Default Color
                </button>
            </div>
        </div>
    );
};

export default CustomizationPage;
