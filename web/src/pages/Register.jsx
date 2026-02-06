import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';

const Register = () => {
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        firstName: '',
        lastName: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            await axios.post('http://localhost:8080/api/auth/register', formData);
            setSuccess('Registration successful! You can now login.');
            setTimeout(() => {
                navigate('/login');
            }, 2000);
        } catch (err) {
            setError(err.response?.data || 'Registration failed. Please try again.');
        }
    };

    return (
        <div style={styles.container}>
            <div style={styles.card}>
                <h2 style={styles.title}>Register</h2>
                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.inputGroup}>
                        <input
                            type="text"
                            name="firstName"
                            placeholder="First Name"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.inputGroup}>
                        <input
                            type="text"
                            name="lastName"
                            placeholder="Last Name"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.inputGroup}>
                        <input
                            type="email"
                            name="email"
                            placeholder="Email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            style={styles.input}
                        />
                    </div>
                    <div style={styles.inputGroup}>
                        <input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            minLength="6"
                            style={styles.input}
                        />
                    </div>
                    <button type="submit" style={styles.button}>Register</button>
                </form>
                
                {error && <div style={styles.error}>{error}</div>}
                {success && <div style={styles.success}>{success}</div>}
                
                <p style={styles.link}>
                    Already have an account? <Link to="/login" style={styles.linkText}>Login here</Link>
                </p>
            </div>
        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        backgroundColor: '#f5f5f5',
    },
    card: {
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '8px',
        boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
        width: '100%',
        maxWidth: '400px',
    },
    title: {
        textAlign: 'center',
        marginBottom: '1.5rem',
        color: '#333',
    },
    form: {
        display: 'flex',
        flexDirection: 'column',
        gap: '1rem',
    },
    inputGroup: {
        display: 'flex',
        flexDirection: 'column',
    },
    input: {
        padding: '0.75rem',
        border: '1px solid #ddd',
        borderRadius: '4px',
        fontSize: '1rem',
    },
    button: {
        padding: '0.75rem',
        backgroundColor: '#007bff',
        color: 'white',
        border: 'none',
        borderRadius: '4px',
        fontSize: '1rem',
        cursor: 'pointer',
        marginTop: '1rem',
    },
    error: {
        color: '#dc3545',
        marginTop: '1rem',
        padding: '0.5rem',
        backgroundColor: '#f8d7da',
        borderRadius: '4px',
    },
    success: {
        color: '#28a745',
        marginTop: '1rem',
        padding: '0.5rem',
        backgroundColor: '#d4edda',
        borderRadius: '4px',
    },
    link: {
        textAlign: 'center',
        marginTop: '1rem',
        color: '#666',
    },
    linkText: {
        color: '#007bff',
        textDecoration: 'none',
    },
};

export default Register;