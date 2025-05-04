import React, { useState, useEffect } from 'react';

const Dashboard = () => {
  const [username, setUsername] = useState('');
  
  useEffect(() => {
    const storedUsername = localStorage.getItem('username');
    if (storedUsername) {
      setUsername(storedUsername);
    }
  }, []);

  return (
    <div className="card p-4 shadow">
      <h2 className="text-center mb-4">Dashboard</h2>
      <div className="alert alert-success">
        Welcome, {username}! You are successfully logged in.
      </div>
      <div className="card-body">
        <p className="lead">This is your account dashboard where you can manage your profile and account settings.</p>
        <p>Your role: Customer</p>
        <p>More features will be added soon!</p>
      </div>
    </div>
  );
};

export default Dashboard;