import React, { createContext, useState, useEffect } from 'react';

export const BackgroundCustomizationContext = createContext();

export const BackgroundCustomizationProvider = ({ children }) => {
    const [backgroundColor, setBackgroundColor] = useState(() => {
        // Ensure `localStorage` value is used during initialization
        return localStorage.getItem('backgroundColor') || '#927d61';
    });

    // Save the updated color to `localStorage` whenever it changes
    useEffect(() => {
        if (backgroundColor) {
            localStorage.setItem('backgroundColor', backgroundColor);
        }
    }, [backgroundColor]);

    return (
        <BackgroundCustomizationContext.Provider value={{ backgroundColor, setBackgroundColor }}>
            {children}
        </BackgroundCustomizationContext.Provider>
    );
};
