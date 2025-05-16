const customer = {
    cart: [],
    dishes: [],

    init() {
        this.loadDishes();
        this.loadCart();
    },

    async loadDishes() {
        try {
            this.dishes = await api.getDishes();
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
                    <div class="dish-price">$${dish.price.toFixed(2)}</div>
                    <div class="quantity-control">
                        <button class="quantity-btn" onclick="customer.decreaseQuantity(${dish.id})">-</button>
                        <input type="number" class="quantity-input" value="0" min="0" max="${dish.quantity}"
                            onchange="customer.updateQuantity(${dish.id}, this.value)">
                        <button class="quantity-btn" onclick="customer.increaseQuantity(${dish.id})">+</button>
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
                    <div class="dish-price">$${dish.price.toFixed(2)}</div>
                    <div class="quantity-control">
                        <button class="quantity-btn" onclick="customer.decreaseQuantity(${dish.id})">-</button>
                        <input type="number" class="quantity-input" value="0" min="0" max="${dish.quantity}"
                            onchange="customer.updateQuantity(${dish.id}, this.value)">
                        <button class="quantity-btn" onclick="customer.increaseQuantity(${dish.id})">+</button>
                    </div>
                </div>
            `;
            container.appendChild(card);
        });
    },

    increaseQuantity(dishId) {
        const input = document.querySelector(`.quantity-input[data-dish-id="${dishId}"]`);
        const currentValue = parseInt(input.value);
        const maxValue = parseInt(input.max);
        if (currentValue < maxValue) {
            input.value = currentValue + 1;
            this.updateQuantity(dishId, currentValue + 1);
        }
    },

    decreaseQuantity(dishId) {
        const input = document.querySelector(`.quantity-input[data-dish-id="${dishId}"]`);
        const currentValue = parseInt(input.value);
        if (currentValue > 0) {
            input.value = currentValue - 1;
            this.updateQuantity(dishId, currentValue - 1);
        }
    },

    updateQuantity(dishId, quantity) {
        const dish = this.dishes.find(d => d.id === dishId);
        if (!dish) return;

        const cartItem = this.cart.find(item => item.dishId === dishId);
        if (cartItem) {
            if (quantity === 0) {
                this.cart = this.cart.filter(item => item.dishId !== dishId);
            } else {
                cartItem.quantity = parseInt(quantity);
            }
        } else if (quantity > 0) {
            this.cart.push({
                dishId,
                name: dish.name,
                price: dish.price,
                quantity: parseInt(quantity),
                image: dish.image
            });
        }

        this.saveCart();
        this.updateCartUI();
    },

    loadCart() {
        const savedCart = localStorage.getItem('cart');
        if (savedCart) {
            this.cart = JSON.parse(savedCart);
            this.updateCartUI();
        }
    },

    saveCart() {
        localStorage.setItem('cart', JSON.stringify(this.cart));
    },

    updateCartUI() {
        const cartItems = document.getElementById('cart-items');
        const cartCount = document.getElementById('cart-count');
        const cartSubtotal = document.getElementById('cart-subtotal');
        const cartTotal = document.getElementById('cart-total');

        cartItems.innerHTML = '';
        let subtotal = 0;

        this.cart.forEach(item => {
            const itemTotal = item.price * item.quantity;
            subtotal += itemTotal;

            const cartItem = document.createElement('div');
            cartItem.className = 'cart-item';
            cartItem.innerHTML = `
                <img src="${item.image || 'images/placeholder.jpg'}" alt="${item.name}" class="cart-item-image">
                <div class="cart-item-info">
                    <div class="cart-item-name">${item.name}</div>
                    <div class="cart-item-price">$${item.price.toFixed(2)} x ${item.quantity}</div>
                    <div class="cart-item-total">$${itemTotal.toFixed(2)}</div>
                </div>
            `;
            cartItems.appendChild(cartItem);
        });

        const deliveryFee = 5.00;
        const total = subtotal + deliveryFee;

        cartCount.textContent = this.cart.reduce((sum, item) => sum + item.quantity, 0);
        cartSubtotal.textContent = `$${subtotal.toFixed(2)}`;
        cartTotal.textContent = `$${total.toFixed(2)}`;
    },

    toggleCart() {
        const sidebar = document.getElementById('cart-sidebar');
        sidebar.classList.toggle('active');
    },

    closeCart() {
        const sidebar = document.getElementById('cart-sidebar');
        sidebar.classList.remove('active');
    },

    async checkout() {
        if (this.cart.length === 0) {
            alert('Your cart is empty!');
            return;
        }

        try {
            const order = {
                items: this.cart.map(item => ({
                    dishId: item.dishId,
                    quantity: item.quantity
                }))
            };

            await api.createOrder(order);
            this.cart = [];
            this.saveCart();
            this.updateCartUI();
            this.closeCart();
            alert('Order placed successfully!');
        } catch (error) {
            console.error('Error placing order:', error);
            alert('Failed to place order. Please try again later.');
        }
    },

    async loadOrders() {
        try {
            const orders = await api.getOrders();
            this.renderOrders(orders);
        } catch (error) {
            console.error('Error loading orders:', error);
            alert('Failed to load orders. Please try again later.');
        }
    },

    renderOrders(orders) {
        const container = document.getElementById('orders-list');
        container.innerHTML = '';

        if (orders.length === 0) {
            container.innerHTML = '<p class="no-orders">No orders found.</p>';
            return;
        }

        orders.forEach(order => {
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
                <div class="order-total">
                    Total: $${order.total.toFixed(2)}
                </div>
            `;
            container.appendChild(orderCard);
        });
    },

    async loadAccountInfo() {
        try {
            const user = await api.getUserProfile();
            document.getElementById('account-fullname').value = user.fullName || '';
            document.getElementById('account-email').value = user.email || '';
            document.getElementById('account-phone').value = user.phone || '';
            document.getElementById('account-address').value = user.address || '';
        } catch (error) {
            console.error('Error loading account info:', error);
            alert('Failed to load account information. Please try again later.');
        }
    },

    async updateAccountInfo() {
        try {
            const userData = {
                fullName: document.getElementById('account-fullname').value,
                email: document.getElementById('account-email').value,
                phone: document.getElementById('account-phone').value,
                address: document.getElementById('account-address').value
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