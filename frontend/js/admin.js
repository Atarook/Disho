// Admin functionality
const admin = {
    init() {
        this.bindEvents();
        this.loadDashboardData();
        this.loadUsers();
        this.loadSellers();
        this.loadShippingCompanies();
    },

    bindEvents() {
        // Navigation
        document.querySelectorAll('.sidebar-nav a').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const targetId = e.currentTarget.getAttribute('href').substring(1);
                this.showSection(targetId);
            });
        });

        // Create user button
        document.getElementById('create-user-btn').addEventListener('click', () => {
            this.showCreateUserModal();
        });

        // Create seller button
        document.getElementById('create-seller-btn').addEventListener('click', () => {
            this.showCreateUserModal('seller');
        });

        // Create shipping company button
        document.getElementById('create-shipping-btn').addEventListener('click', () => {
            this.showCreateShippingModal();
        });

        // User type change in create user form
        document.getElementById('user-type').addEventListener('change', (e) => {
            this.toggleUserTypeFields(e.target.value);
        });

        // Form submissions
        document.getElementById('create-user-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.createUser();
        });

        document.getElementById('system-settings-form').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveSystemSettings();
        });

        // Search and filter events
        document.getElementById('user-search').addEventListener('input', (e) => {
            this.filterUsers(e.target.value);
        });

        document.getElementById('user-type-filter').addEventListener('change', (e) => {
            this.filterUsersByType(e.target.value);
        });

        document.getElementById('seller-search').addEventListener('input', (e) => {
            this.filterSellers(e.target.value);
        });

        document.getElementById('seller-status-filter').addEventListener('change', (e) => {
            this.filterSellersByStatus(e.target.value);
        });

        document.getElementById('shipping-search').addEventListener('input', (e) => {
            this.filterShippingCompanies(e.target.value);
        });

        document.getElementById('shipping-region-filter').addEventListener('change', (e) => {
            this.filterShippingCompaniesByRegion(e.target.value);
        });
    },

    showSection(sectionId) {
        // Hide all sections
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });

        // Show selected section
        document.getElementById(sectionId).classList.add('active');

        // Update sidebar active state
        document.querySelectorAll('.sidebar-nav li').forEach(li => {
            li.classList.remove('active');
        });
        document.querySelector(`.sidebar-nav a[href="#${sectionId}"]`).parentElement.classList.add('active');
    },

    async loadDashboardData() {
        try {
            // Load statistics
            const stats = await api.getAdminStats();
            this.updateDashboardStats(stats);

            // Load recent activity
            const activities = await api.getRecentActivity();
            this.updateRecentActivity(activities);
        } catch (error) {
            console.error('Error loading dashboard data:', error);
            this.showError('Failed to load dashboard data');
        }
    },

    updateDashboardStats(stats) {
        document.querySelector('.stat-card:nth-child(1) .stat-value').textContent = stats.totalUsers;
        document.querySelector('.stat-card:nth-child(2) .stat-value').textContent = stats.activeSellers;
        document.querySelector('.stat-card:nth-child(3) .stat-value').textContent = stats.totalOrders;
        document.querySelector('.stat-card:nth-child(4) .stat-value').textContent = `$${stats.platformRevenue.toFixed(2)}`;
    },

    updateRecentActivity(activities) {
        const activityList = document.getElementById('recent-activity');
        activityList.innerHTML = activities.map(activity => `
            <div class="activity-item">
                <span class="activity-time">${new Date(activity.timestamp).toLocaleString()}</span>
                <span class="activity-description">${activity.description}</span>
            </div>
        `).join('');
    },

    async loadUsers() {
        try {
            const users = await api.getAllUsers();
            this.renderUsersList(users);
        } catch (error) {
            console.error('Error loading users:', error);
            this.showError('Failed to load users');
        }
    },

    renderUsersList(users) {
        const usersList = document.getElementById('users-list');
        usersList.innerHTML = users.map(user => `
            <div class="user-item">
                <div class="user-info">
                    <h4>${user.username}</h4>
                    <p>${user.email}</p>
                    <span class="user-type">${user.type}</span>
                </div>
                <div class="user-actions">
                    <button class="btn btn-secondary" onclick="admin.editUser(${user.id})">Edit</button>
                    <button class="btn btn-danger" onclick="admin.deleteUser(${user.id})">Delete</button>
                </div>
            </div>
        `).join('');
    },

    async loadSellers() {
        try {
            const sellers = await api.getAllSellers();
            this.renderSellersList(sellers);
        } catch (error) {
            console.error('Error loading sellers:', error);
            this.showError('Failed to load sellers');
        }
    },

    renderSellersList(sellers) {
        const sellersList = document.getElementById('sellers-list');
        sellersList.innerHTML = sellers.map(seller => `
            <div class="seller-item">
                <div class="seller-info">
                    <h4>${seller.companyName}</h4>
                    <p>${seller.email}</p>
                    <span class="seller-status ${seller.status}">${seller.status}</span>
                </div>
                <div class="seller-actions">
                    <button class="btn btn-secondary" onclick="admin.editSeller(${seller.id})">Edit</button>
                    <button class="btn btn-danger" onclick="admin.deleteSeller(${seller.id})">Delete</button>
                </div>
            </div>
        `).join('');
    },

    async loadShippingCompanies() {
        try {
            const companies = await api.getAllShippingCompanies();
            this.renderShippingList(companies);
        } catch (error) {
            console.error('Error loading shipping companies:', error);
            this.showError('Failed to load shipping companies');
        }
    },

    renderShippingList(companies) {
        const shippingList = document.getElementById('shipping-list');
        shippingList.innerHTML = companies.map(company => `
            <div class="shipping-item">
                <div class="shipping-info">
                    <h4>${company.name}</h4>
                    <p>${company.region}</p>
                    <span class="shipping-status ${company.status}">${company.status}</span>
                </div>
                <div class="shipping-actions">
                    <button class="btn btn-secondary" onclick="admin.editShippingCompany(${company.id})">Edit</button>
                    <button class="btn btn-danger" onclick="admin.deleteShippingCompany(${company.id})">Delete</button>
                </div>
            </div>
        `).join('');
    },

    showCreateUserModal(userType = 'customer') {
        const modal = document.getElementById('create-user-modal');
        document.getElementById('user-type').value = userType;
        this.toggleUserTypeFields(userType);
        modal.style.display = 'block';
    },

    toggleUserTypeFields(userType) {
        const customerFields = document.getElementById('customer-fields');
        const sellerFields = document.getElementById('seller-fields');
        
        if (userType === 'customer') {
            customerFields.style.display = 'block';
            sellerFields.style.display = 'none';
        } else {
            customerFields.style.display = 'none';
            sellerFields.style.display = 'block';
        }
    },

    async createUser() {
        const form = document.getElementById('create-user-form');
        const formData = new FormData(form);
        const userData = Object.fromEntries(formData.entries());

        try {
            await api.createUser(userData);
            this.closeModal();
            this.loadUsers();
            this.showSuccess('User created successfully');
        } catch (error) {
            console.error('Error creating user:', error);
            this.showError('Failed to create user');
        }
    },

    async saveSystemSettings() {
        const form = document.getElementById('system-settings-form');
        const formData = new FormData(form);
        const settings = Object.fromEntries(formData.entries());

        try {
            await api.updateSystemSettings(settings);
            this.showSuccess('Settings saved successfully');
        } catch (error) {
            console.error('Error saving settings:', error);
            this.showError('Failed to save settings');
        }
    },

    filterUsers(searchTerm) {
        const users = document.querySelectorAll('.user-item');
        users.forEach(user => {
            const text = user.textContent.toLowerCase();
            user.style.display = text.includes(searchTerm.toLowerCase()) ? 'flex' : 'none';
        });
    },

    filterUsersByType(type) {
        const users = document.querySelectorAll('.user-item');
        users.forEach(user => {
            const userType = user.querySelector('.user-type').textContent;
            user.style.display = type === 'all' || userType === type ? 'flex' : 'none';
        });
    },

    filterSellers(searchTerm) {
        const sellers = document.querySelectorAll('.seller-item');
        sellers.forEach(seller => {
            const text = seller.textContent.toLowerCase();
            seller.style.display = text.includes(searchTerm.toLowerCase()) ? 'flex' : 'none';
        });
    },

    filterSellersByStatus(status) {
        const sellers = document.querySelectorAll('.seller-item');
        sellers.forEach(seller => {
            const sellerStatus = seller.querySelector('.seller-status').textContent;
            seller.style.display = status === 'all' || sellerStatus === status ? 'flex' : 'none';
        });
    },

    filterShippingCompanies(searchTerm) {
        const companies = document.querySelectorAll('.shipping-item');
        companies.forEach(company => {
            const text = company.textContent.toLowerCase();
            company.style.display = text.includes(searchTerm.toLowerCase()) ? 'flex' : 'none';
        });
    },

    filterShippingCompaniesByRegion(region) {
        const companies = document.querySelectorAll('.shipping-item');
        companies.forEach(company => {
            const companyRegion = company.querySelector('.shipping-info p').textContent;
            company.style.display = region === 'all' || companyRegion === region ? 'flex' : 'none';
        });
    },

    closeModal() {
        document.getElementById('create-user-modal').style.display = 'none';
        document.getElementById('create-user-form').reset();
    },

    showSuccess(message) {
        // Implement success notification
        alert(message); // Replace with a proper notification system
    },

    showError(message) {
        // Implement error notification
        alert(message); // Replace with a proper notification system
    }
};

// Initialize admin functionality when the page loads
document.addEventListener('DOMContentLoaded', () => {
    admin.init();
}); 