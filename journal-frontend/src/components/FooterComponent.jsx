import React from 'react';
import { Link } from 'react-router-dom';



const FooterComponent = () => {
  return (
    <div>
      <footer className="footer">
        <p className="text-center">
          WolfCafe &copy; 2024 | <Link to="/privacy">Privacy Policy</Link> | <Link to="/accessibility">Accessibility</Link>
        </p>
      </footer>
    </div>
  );
};

export default FooterComponent;
