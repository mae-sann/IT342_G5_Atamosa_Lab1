import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
    const [userData, setUserData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [showLogoutModal, setShowLogoutModal] = useState(false);
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const token = localStorage.getItem('token');
                const response = await fetch('http://localhost:8080/api/user/me', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                
                if (!response.ok) {
                    throw new Error('Failed to fetch user data');
                }
                
                const data = await response.json();
                setUserData(data);
            } catch (err) {
                console.error(err);
                navigate('/login');
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, [navigate]);

    const handleLogout = () => {
        setShowLogoutModal(true);
    };

    const confirmLogout = () => {
        logout();
        navigate('/login');
    };

    const cancelLogout = () => {
        setShowLogoutModal(false);
    };

    if (loading) {
        return <div style={styles.loading}>Loading...</div>;
    }

    return (
        <div style={styles.container}>
            <nav style={styles.navbar}>
                <div style={styles.navContent}>
                    <h1 style={styles.logo}>Dashboard</h1>
                    <button onClick={handleLogout} style={styles.logoutButton}>
                        Logout
                    </button>
                </div>
            </nav>
            
            <div style={styles.content}>
                <div style={styles.profileCard}>
                    <div style={styles.avatar}>
                        {userData?.firstName?.charAt(0)}{userData?.lastName?.charAt(0)}
                    </div>
                    <h2 style={styles.name}>
                        {userData?.firstName} {userData?.lastName}
                    </h2>
                    <p style={styles.email}>{userData?.email}</p>
                    
                    <div style={styles.infoGrid}>
                        <div style={styles.infoItem}>
                            <span style={styles.infoLabel}>User ID</span>
                            <span style={styles.infoValue}>{userData?.id}</span>
                        </div>
                        <div style={styles.infoItem}>
                            <span style={styles.infoLabel}>Joined</span>
                            <span style={styles.infoValue}>
                                {new Date(userData?.createdAt).toLocaleDateString()}
                            </span>
                        </div>
                    </div>
                    
                    <div style={styles.welcomeMessage}>
                        <p>Welcome to your dashboard! You have successfully authenticated.</p>
                        <p>Your JWT token is securely stored and used for protected API calls.</p>
                    </div>
                </div>
            </div>

            {/* Logout Confirmation Modal */}
            {showLogoutModal && (
                <div style={styles.modalOverlay}>
                    <div style={styles.modal}>
                        <h3 style={styles.modalTitle}>Confirm Logout</h3>
                        <p style={styles.modalMessage}>Are you sure you want to logout?</p>
                        <div style={styles.modalButtons}>
                            <button onClick={confirmLogout} style={styles.confirmButton}>
                                Confirm
                            </button>
                            <button onClick={cancelLogout} style={styles.cancelButton}>
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

const styles = {
    container: {
        minHeight: '100vh',
        backgroundColor: '#f5f5f5',
    },
    navbar: {
        backgroundColor: 'white',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        padding: '1rem 0',
    },
    navContent: {
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '0 1rem',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    logo: {
        margin: 0,
        color: '#007bff',
    },
    logoutButton: {
        padding: '0.5rem 1rem',
        backgroundColor: '#dc3545',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
    },
    content: {
        maxWidth: '1200px',
        margin: '2rem auto',
        padding: '0 1rem',
    },
    profileCard: {
        backgroundColor: 'white',
        borderRadius: '8px',
        padding: '2rem',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        textAlign: 'center',
    },
    avatar: {
        width: '80px',
        height: '80px',
        borderRadius: '50%',
        backgroundColor: '#007bff',
        color: 'white',
        fontSize: '2rem',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        margin: '0 auto 1rem',
    },
    name: {
        margin: '0.5rem 0',
        color: '#333',
    },
    email: {
        color: '#666',
        marginBottom: '2rem',
    },
    infoGrid: {
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '1rem',
        marginBottom: '2rem',
    },
    infoItem: {
        backgroundColor: '#f8f9fa',
        padding: '1rem',
        borderRadius: '4px',
    },
    infoLabel: {
        display: 'block',
        color: '#666',
        fontSize: '0.9rem',
        marginBottom: '0.5rem',
    },
    infoValue: {
        display: 'block',
        color: '#333',
        fontSize: '1.1rem',
        fontWeight: 'bold',
    },
    welcomeMessage: {
        marginTop: '2rem',
        padding: '1rem',
        backgroundColor: '#d4edda',
        color: '#155724',
        borderRadius: '4px',
    },
    loading: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        fontSize: '1.2rem',
    },
    modalOverlay: {
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        zIndex: 1000,
    },
    modal: {
        backgroundColor: 'white',
        borderRadius: '8px',
        padding: '2rem',
        maxWidth: '400px',
        width: '90%',
        boxShadow: '0 4px 20px rgba(0, 0, 0, 0.3)',
    },
    modalTitle: {
        margin: '0 0 1rem 0',
        color: '#333',
        fontSize: '1.5rem',
    },
    modalMessage: {
        margin: '0 0 1.5rem 0',
        color: '#666',
        fontSize: '1rem',
    },
    modalButtons: {
        display: 'flex',
        gap: '1rem',
        justifyContent: 'flex-end',
    },
    confirmButton: {
        padding: '0.6rem 1.5rem',
        backgroundColor: '#dc3545',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '1rem',
        fontWeight: '500',
        transition: 'background-color 0.2s',
    },
    cancelButton: {
        padding: '0.6rem 1.5rem',
        backgroundColor: '#6c757d',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        cursor: 'pointer',
        fontSize: '1rem',
        fontWeight: '500',
        transition: 'background-color 0.2s',
    },
};

export default Dashboard;