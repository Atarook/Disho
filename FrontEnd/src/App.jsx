import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    localStorage.getItem('isAuthenticated') === 'true'
  );

  const setAuth = (status) => {
    localStorage.setItem('isAuthenticated', status);
    setIsAuthenticated(status);
  };

  return (
    <Router>
      <div className="App">
        <Navbar isAuthenticated={isAuthenticated} setAuth={setAuth} />
        <div className="container mt-4">
          <Routes>
            <Route path="/login" element={
              !isAuthenticated ? 
                <Login setAuth={setAuth} /> : 
                <Navigate to="/dashboard" />
            } />
            <Route path="/register" element={
              !isAuthenticated ? 
                <Register setAuth={setAuth} /> : 
                <Navigate to="/dashboard" />
            } />
            <Route 
              path="/dashboard" 
              element={
                <ProtectedRoute isAuthenticated={isAuthenticated}>
                  <Dashboard />
                </ProtectedRoute>
              } 
            />
            <Route path="*" element={<Navigate to="/login" />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;