// Auth Service
const auth = {
    currentUser: null,

    async login(username, password) {
        try {
            const response = await api.login(username, password);
            if (response.user) {
                this.currentUser = response.user;
                localStorage.setItem('token', response.token);
                localStorage.setItem('user', JSON.stringify(response.user));
                return response.user;
            }
            throw new Error('Invalid credentials');
        } catch (error) {
            console.error('Login error:', error);
            throw error;
        }
    },

    async register(userData) {
        try {
            const user = await api.register(userData);
            this.currentUser = user;
            localStorage.setItem('token', 'mock-token');
            localStorage.setItem('user', JSON.stringify(user));
            return user;
        } catch (error) {
            console.error('Registration error:', error);
            throw error;
        }
    },

    logout() {
        this.currentUser = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'index.html';
    },

    isAuthenticated() {
        return !!localStorage.getItem('token');
    },

    getCurrentUser() {
        if (!this.currentUser) {
            const userStr = localStorage.getItem('user');
            if (userStr) {
                this.currentUser = JSON.parse(userStr);
            }
        }
        return this.currentUser;
    },

    getUserType() {
        const user = this.getCurrentUser();
        return user ? user.type : null;
    },

    redirectBasedOnUserType() {
        const userType = this.getUserType();
        if (userType) {
            switch (userType) {
                case 'admin':
                    window.location.href = 'admin.html';
                    break;
                case 'seller':
                    window.location.href = 'seller.html';
                    break;
                case 'customer':
                    window.location.href = 'customer.html';
                    break;
                default:
                    window.location.href = 'index.html';
            }
        } else {
            window.location.href = 'index.html';
        }
    }
}; 