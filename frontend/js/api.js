// Dummy Data Service
const api = {
    // Dummy data
    data: {
        users: [
            { id: 1, username: 'john_doe', email: 'john@example.com', type: 'customer', fullName: 'John Doe', phone: '123-456-7890', address: '123 Main St' },
            { id: 2, username: 'jane_smith', email: 'jane@example.com', type: 'seller', companyName: 'Jane\'s Kitchen', companyDescription: 'Delicious homemade meals' }
        ],
        sellers: [
            { id: 1, companyName: 'Jane\'s Kitchen', email: 'jane@example.com', status: 'active', region: 'north' },
            { id: 2, companyName: 'Bob\'s Bistro', email: 'bob@example.com', status: 'active', region: 'south' }
        ],
        dishes: [
            { id: 1, name: 'Pasta Carbonara', description: 'Creamy pasta with bacon', price: 12.99, category: 'main', quantity: 50, imageUrl: 'https://images.unsplash.com/photo-1612874742237-6526221588e3?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80' },
            { id: 2, name: 'Chocolate Cake', description: 'Rich chocolate cake', price: 8.99, category: 'desserts', quantity: 30, imageUrl: 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80' },
            { id: 3, name: 'Caesar Salad', description: 'Fresh romaine lettuce with Caesar dressing', price: 9.99, category: 'appetizers', quantity: 40, imageUrl: 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1000&q=80' }
        ],
        orders: [
            { id: 1, customerId: 1, items: [{ dishId: 1, quantity: 2 }], status: 'pending', total: 25.98 },
            { id: 2, customerId: 1, items: [{ dishId: 2, quantity: 1 }], status: 'delivered', total: 8.99 }
        ]
    },

    // Auth functions
    login(username, password) {
        return {
            token: 'dummy-token',
            user: this.data.users.find(u => u.username === username)
        };
    },

    // Dish functions
    getAvailableDishes() {
        return this.data.dishes;
    },

    getDishById(dishId) {
        return this.data.dishes.find(d => d.id === dishId);
    },

    createDish(dishData) {
        const newDish = { id: this.data.dishes.length + 1, ...dishData };
        this.data.dishes.push(newDish);
        return newDish;
    },

    updateDish(dishId, dishData) {
        const dishIndex = this.data.dishes.findIndex(d => d.id === dishId);
        if (dishIndex !== -1) {
            this.data.dishes[dishIndex] = { ...this.data.dishes[dishIndex], ...dishData };
            return this.data.dishes[dishIndex];
        }
        return null;
    },

    deleteDish(dishId) {
        const dishIndex = this.data.dishes.findIndex(d => d.id === dishId);
        if (dishIndex !== -1) {
            this.data.dishes.splice(dishIndex, 1);
            return true;
        }
        return false;
    },

    // Order functions
    getCustomerOrders(customerId) {
        return this.data.orders.filter(o => o.customerId === customerId);
    },

    getSellerOrders(sellerId) {
        return this.data.orders;
    },

    createOrder(customerId, orderItems) {
        const newOrder = {
            id: this.data.orders.length + 1,
            customerId,
            items: orderItems,
            status: 'pending',
            total: orderItems.reduce((sum, item) => {
                const dish = this.data.dishes.find(d => d.id === item.dishId);
                return sum + (dish.price * item.quantity);
            }, 0)
        };
        this.data.orders.push(newOrder);
        return newOrder;
    },

    updateOrderStatus(orderId, status) {
        const order = this.data.orders.find(o => o.id === orderId);
        if (order) {
            order.status = status;
            return order;
        }
        return null;
    }
}; 