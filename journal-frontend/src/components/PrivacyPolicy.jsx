import React, { useState, useEffect } from 'react';

const PrivacyPolicy = () => {
    const [dataSharingConsent, setDataSharingConsent] = useState(false);

    const handleConsentChange = (event) => {
        setDataSharingConsent(event.target.checked);
    };

    useEffect(() => {
        // Example: Save consent preference in localStorage or send to backend
        console.log("Data Sharing Consent:", dataSharingConsent);
    }, [dataSharingConsent]);

    return (
        <div style={{ padding: '50px', fontFamily: 'Arial, sans-serif' }}>
            <h1>WolfCafe Privacy Policy</h1>
            <p><strong>Effective Date: January 2024</strong></p>
            <p>
                At WolfCafe, we value your privacy as much as we value a great cup of coffee. 
                This Privacy Policy outlines how we collect, use, and protect your information 
                when you visit our website and use our services. We believe in being transparent 
                about our practices, and we're here to make your experience enjoyable and secure.
            </p>

            <h2>1. Information We Collect</h2>
            <ul>
                <li><strong>Personal Information:</strong> Includes your name, email address, phone number, and payment information.</li>
                <li><strong>Order Information:</strong> Details about your purchases, including items ordered and order history.</li>
                <li><strong>Usage Data:</strong> Information about your website usage, such as IP address, browser type, and pages visited.</li>
            </ul>

            <h2>2. How We Use Your Information</h2>
            <ul>
                <li><strong>To Process Orders:</strong> Ensure orders are fulfilled accurately and efficiently.</li>
                <li><strong>To Communicate with You:</strong> Send updates, promotions, and account-related information.</li>
                <li><strong>To Improve Our Services:</strong> Analyze usage data to enhance user experience.</li>
                <li><strong>To Personalize Your Experience:</strong> Tailor services and marketing to your preferences.</li>
            </ul>

            <h2>3. Sharing Your Information</h2>
            <p>
                We respect your privacy and will never sell your information. However, we may share it with trusted 
                third parties like payment processors or delivery services, or for legal compliance.
            </p>
            <label>
                <input
                    type="checkbox"
                    checked={dataSharingConsent}
                    onChange={handleConsentChange}
                />
                I consent to sharing my data with trusted partners.
            </label>

            <h2>4. Data Security</h2>
            <p>
                We use encryption protocols to protect sensitive data transmitted online. 
                Your information is stored securely in our trusted database hosted on a 
                robust, industry-standard platform.
            </p>

            <h2>5. Your Rights</h2>
            <ul>
                <li><strong>Access Your Information:</strong> Request a copy of your personal data.</li>
                <li><strong>Update Your Information:</strong> Correct any inaccuracies.</li>
                <li><strong>Delete Your Account:</strong> Request deletion of your account, subject to retention policies.</li>
            </ul>

            <h2>6. Cookies and Tracking Technologies</h2>
            <p>
                Our website uses cookies to enhance your browsing experience. 
                You can choose to accept or decline cookies in your browser settings.
            </p>

            <h2>7. Changes to This Policy</h2>
            <p>
                We may update this Privacy Policy from time to time. You will be notified via email if significant changes occur.
            </p>

            <h2>8. Contact Us</h2>
            <p>
                For any questions, please reach out to:
                <br />
                <strong>Email:</strong> wolfsupport@wolfcafe.com
                <br />
                <strong>Phone:</strong> 919-xxx-9649
                <br />
                <strong>Address:</strong> Raleigh, NC
            </p>

            <h2>Responsibilities</h2>
            <p>
                At WolfCafe, we are committed to protecting your data and maintaining transparency 
                in our privacy practices.
            </p>
        </div>
    );
};

export default PrivacyPolicy;
