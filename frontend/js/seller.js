const seller = {
    dishes: [],
    orders: [],

    init() {
        this.loadDishes();
        this.loadOrders();
        this.updateStats();
    },

    async loadDishes() {
        try {
            this.dishes = await api.getSellerDishes();
            this.renderDishes();
        } catch (error) {
            console.error('Error loading dishes:', error);
            alert('Failed to load dishes. Please try again later.');
        }
    },

    renderDishes() {
        const container = document.getElementById('dishes-container');
        container.innerHTML = '';

        this.dishes.forEach(dish => {
            const card = document.createElement('div');
            card.className = 'dish-card';
            card.innerHTML = `
                <img src="${dish.image || 'images/placeholder.jpg'}" alt="${dish.name}" class="dish-image">
                <div class="dish-info">
                    <h3 class="dish-name">${dish.name}</h3>
                    <p class="dish-description">${dish.description}</p>
                    <div class="dish-details">
                        <span class="dish-price">$${dish.price.toFixed(2)}</span>
                        <span class="dish-quantity">Available: ${dish.quantity}</span>
                    </div>
                    <div class="dish-actions">
                        <button class="edit-btn" onclick="seller.editDish(${dish.id})">Edit</button>
                        <button class="delete-btn" onclick="seller.deleteDish(${dish.id})">Delete</button>
                    </div>
                </div>
            `;
            container.appendChild(card);
        });
    },

    filterDishes(searchTerm) {
        const filteredDishes = this.dishes.filter(dish =>
            dish.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            dish.description.toLowerCase().includes(searchTerm.toLowerCase())
        );
        this.renderFilteredDishes(filteredDishes);
    },

    filterDishesByCategory(category) {
        if (!category) {
            this.renderDishes();
            return;
        }
        const filteredDishes = this.dishes.filter(dish => dish.category === category);
        this.renderFilteredDishes(filteredDishes);
    },

    renderFilteredDishes(filteredDishes) {
        const container = document.getElementById('dishes-container');
        container.innerHTML = '';

        if (filteredDishes.length === 0) {
            container.innerHTML = '<p class="no-results">No dishes found.</p>';
            return;
        }

        filteredDishes.forEach(dish => {
            const card = document.createElement('div');
            card.className = 'dish-card';
            card.innerHTML = `
                <img src="${dish.image || 'images/placeholder.jpg'}" alt="${dish.name}" class="dish-image">
                <div class="dish-info">
                    <h3 class="dish-name">${dish.name}</h3>
                    <p class="dish-description">${dish.description}</p>
                    <div class="dish-details">
                        <span class="dish-price">$${dish.price.toFixed(2)}</span>
                        <span class="dish-quantity">Available: ${dish.quantity}</span>
                    </div>
                    <div class="dish-actions">
                        <button class="edit-btn" onclick="seller.editDish(${dish.id})">Edit</button>
                        <button class="delete-btn" onclick="seller.deleteDish(${dish.id})">Delete</button>
                    </div>
                </div>
            `;
            container.appendChild(card);
        });
    },

    editDish(dishId) {
        const dish = this.dishes.find(d => d.id === dishId);
        if (!dish) return;

        document.getElementById('dish-modal-title').textContent = 'Edit Dish';
        document.getElementById('dish-name').value = dish.name;
        document.getElementById('dish-description').value = dish.description;
        document.getElementById('dish-category').value = dish.category;
        document.getElementById('dish-price').value = dish.price;
        document.getElementById('dish-quantity').value = dish.quantity;
        document.getElementById('dish-image').value = dish.image || '';

        document.getElementById('dish-form').dataset.dishId = dishId;
        document.getElementById('dish-modal').style.display = 'block';
    },

    async deleteDish(dishId) {
        if (!confirm('Are you sure you want to delete this dish?')) return;

        try {
            await api.deleteDish(dishId);
            this.dishes = this.dishes.filter(d => d.id !== dishId);
            this.renderDishes();
            this.updateStats();
        } catch (error) {
            console.error('Error deleting dish:', error);
            alert('Failed to delete dish. Please try again later.');
        }
    },

    async saveDish() {
        const form = document.getElementById('dish-form');
        const dishId = form.dataset.dishId;
        const dishData = {
            name: document.getElementById('dish-name').value,
            description: document.getElementById('dish-description').value,
            category: document.getElementById('dish-category').value,
            price: parseFloat(document.getElementById('dish-price').value),
            quantity: parseInt(document.getElementById('dish-quantity').value),
            image: document.getElementById('dish-image').value
        };

        try {
            if (dishId) {
                await api.updateDish(dishId, dishData);
                const index = this.dishes.findIndex(d => d.id === parseInt(dishId));
                if (index !== -1) {
                    this.dishes[index] = { ...this.dishes[index], ...dishData };
                }
            } else {
                const newDish = await api.createDish(dishData);
                this.dishes.push(newDish);
            }

            this.renderDishes();
            this.updateStats();
            closeModal('dish-modal');
            form.reset();
            delete form.dataset.dishId;
        } catch (error) {
            console.error('Error saving dish:', error);
            alert('Failed to save dish. Please try again later.');
        }
    },

    async loadOrders() {
        try {
            this.orders = await api.getSellerOrders();
            this.renderOrders();
            this.updateStats();
        } catch (error) {
            console.error('Error loading orders:', error);
            alert('Failed to load orders. Please try again later.');
        }
    },

    renderOrders() {
        const container = document.getElementById('orders-container');
        container.innerHTML = '';

        if (this.orders.length === 0) {
            container.innerHTML = '<p class="no-orders">No orders found.</p>';
            return;
        }

        this.orders.forEach(order => {
            const orderCard = document.createElement('div');
            orderCard.className = 'order-card';
            orderCard.innerHTML = `
                <div class="order-header">
                    <div>
                        <span class="order-number">Order #${order.id}</span>
                        <span class="order-date">${new Date(order.date).toLocaleDateString()}</span>
                    </div>
                    <span class="order-status status-${order.status.toLowerCase()}">${order.status}</span>
                </div>
                <div class="order-items">
                    ${order.items.map(item => `
                        <div class="order-item">
                            <img src="${item.image || 'images/placeholder.jpg'}" alt="${item.name}" class="order-item-image">
                            <div class="order-item-info">
                                <div class="order-item-name">${item.name}</div>
                                <div class="order-item-quantity">Quantity: ${item.quantity}</div>
                                <div class="order-item-price">$${item.price.toFixed(2)}</div>
                            </div>
                        </div>
                    `).join('')}
                </div>
                <div class="order-actions">
                    ${order.status === 'pending' ? `
                        <button class="btn btn-primary" onclick="seller.updateOrderStatus(${order.id}, 'processing')">
                            Process Order
                        </button>
                    ` : order.status === 'processing' ? `
                        <button class="btn btn-primary" onclick="seller.updateOrderStatus(${order.id}, 'completed')">
                            Complete Order
                        </button>
                    ` : ''}
                </div>
            `;
            container.appendChild(orderCard);
        });
    },

    filterOrdersByStatus(status) {
        if (!status) {
            this.renderOrders();
            return;
        }
        const filteredOrders = this.orders.filter(order => order.status === status);
        this.renderFilteredOrders(filteredOrders);
    },

    renderFilteredOrders(filteredOrders) {
        const container = document.getElementById('orders-container');
        container.innerHTML = '';

        if (filteredOrders.length === 0) {
            container.innerHTML = '<p class="no-orders">No orders found.</p>';
            return;
        }

        filteredOrders.forEach(order => {
            const orderCard = document.createElement('div');
            orderCard.className = 'order-card';
            orderCard.innerHTML = `
                <div class="order-header">
                    <div>
                        <span class="order-number">Order #${order.id}</span>
                        <span class="order-date">${new Date(order.date).toLocaleDateString()}</span>
                    </div>
                    <span class="order-status status-${order.status.toLowerCase()}">${order.status}</span>
                </div>
                <div class="order-items">
                    ${order.items.map(item => `
                        <div class="order-item">
                            <img src="${item.image || 'images/placeholder.jpg'}" alt="${item.name}" class="order-item-image">
                            <div class="order-item-info">
                                <div class="order-item-name">${item.name}</div>
                                <div class="order-item-quantity">Quantity: ${item.quantity}</div>
                                <div class="order-item-price">$${item.price.toFixed(2)}</div>
                            </div>
                        </div>
                    `).join('')}
                </div>
                <div class="order-actions">
                    ${order.status === 'pending' ? `
                        <button class="btn btn-primary" onclick="seller.updateOrderStatus(${order.id}, 'processing')">
                            Process Order
                        </button>
                    ` : order.status === 'processing' ? `
                        <button class="btn btn-primary" onclick="seller.updateOrderStatus(${order.id}, 'completed')">
                            Complete Order
                        </button>
                    ` : ''}
                </div>
            `;
            container.appendChild(orderCard);
        });
    },

    async updateOrderStatus(orderId, newStatus) {
        try {
            await api.updateOrderStatus(orderId, newStatus);
            const order = this.orders.find(o => o.id === orderId);
            if (order) {
                order.status = newStatus;
            }
            this.renderOrders();
            this.updateStats();
        } catch (error) {
            console.error('Error updating order status:', error);
            alert('Failed to update order status. Please try again later.');
        }
    },

    updateStats() {
        const totalOrders = this.orders.length;
        const totalRevenue = this.orders.reduce((sum, order) => sum + order.total, 0);
        const activeDishes = this.dishes.length;
        const pendingOrders = this.orders.filter(order => order.status === 'pending').length;

        document.getElementById('total-orders').textContent = totalOrders;
        document.getElementById('total-revenue').textContent = `$${totalRevenue.toFixed(2)}`;
        document.getElementById('active-dishes').textContent = activeDishes;
        document.getElementById('pending-orders').textContent = pendingOrders;
    },

    async loadAccountInfo() {
        try {
            const user = await api.getUserProfile();
            document.getElementById('account-company').value = user.companyName || '';
            document.getElementById('account-email').value = user.email || '';
            document.getElementById('account-phone').value = user.phone || '';
            document.getElementById('account-description').value = user.companyDescription || '';
        } catch (error) {
            console.error('Error loading account info:', error);
            alert('Failed to load account information. Please try again later.');
        }
    },

    async updateAccountInfo() {
        try {
            const userData = {
                companyName: document.getElementById('account-company').value,
                email: document.getElementById('account-email').value,
                phone: document.getElementById('account-phone').value,
                companyDescription: document.getElementById('account-description').value
            };

            await api.updateUserProfile(userData);
            alert('Account information updated successfully!');
            closeModal('account-modal');
        } catch (error) {
            console.error('Error updating account info:', error);
            alert('Failed to update account information. Please try again later.');
        }
    }
}; 