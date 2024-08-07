-- Mysql khong phan biet hoa thuong (no compare lowercase with uppercase) -> using lowercase
CREATE DATABASE shopapp;
USE shopapp;
-- Khach hang muon mua hang => Phai dang ky tai khoan => Bang user (User table)
-- password la mat khau da ma hoa ()

CREATE TABLE users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '',
    create_at DATETIME,
    update_at DATETIME,
    is_active TINYINT(1) DEFAULT 1,
    data_of_birth DATE,
    facebook_account_id INT DEFAULT 0,
    google_account_id INT DEFAULT 0
);
ALTER TABLE users
ADD COLUMN role_id INT;
-- Mỗi user có một role riêng
CREATE TABLE roles(
    id INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);
ALTER TABLE users
ADD FOREIGN KEY (role_id) REFERENCES roles(id);

-- Token, dung de luu phien dang nhap o mot thoi han
CREATE TABLE tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME,
    revoked TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Hỗ trợ đăng nhập từ Facebook và Google
CREATE TABLE social_accounts(
    id INT PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(20) NOT NULL COMMENT 'Tài khoản social network'
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL COMMENT 'Email tài khoản',
    user_name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
-- Bảng danh mục sản phẩm (category)
CREATE TABLE categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Tên danh mục, ví dụ: Laptop, Điện thoại,...'
);
-- Bảng sản phẩm (product)
CREATE TABLE products(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) DEFAULT '' COMMENT 'Tên sản phẩm',
    price FLOAT NOT NULL,
    thumbnail VARCHAR(300) DEFAULT '',
    description LONGTEXT DEFAULT '',
    create_at DATETIME,
    update_at DATETIME,
    category_id int,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

ALTER TABLE product
ADD CONSTRAINT check_price CHECK (price >= 0)

-- đặt hàng
CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    fullname VARCHAR(100) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') COMMENT 'Trạng thái đơn hàng',
    total_money FLOAT CHECK(total_money >= 0),
    shipping_method VARCHAR(100),
    shipping_address VARCHAR(100),
    shipping_date DATE,
    tracking_number VARCHAR(100), -- số lần vận đơn
    payment_method VARCHAR(100)
    -- Xóa mềm đơn hàng
    active TINYINT(1) DEFAULT 1,
);
-- chi tiết đơn hàng
CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES product(id),
    price FLOAT CHECK(price >= 0),
    quantity INT CHECK (quantity > 0),
    total_money FLOAT CHECK(total_money >= 0),
    color VARCHAR(20) DEFAULT ''
);