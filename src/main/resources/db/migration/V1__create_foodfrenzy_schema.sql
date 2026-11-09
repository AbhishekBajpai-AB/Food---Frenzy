CREATE TABLE IF NOT EXISTS `user` (
    u_id INT NOT NULL AUTO_INCREMENT,
    uname VARCHAR(255) NOT NULL,
    uemail VARCHAR(255) NOT NULL,
    upassword VARCHAR(255) NOT NULL,
    unumber BIGINT NOT NULL,
    PRIMARY KEY (u_id),
    CONSTRAINT uk_user_email UNIQUE (uemail)
);

CREATE TABLE IF NOT EXISTS admin (
    admin_id INT NOT NULL AUTO_INCREMENT,
    admin_name VARCHAR(255) NOT NULL,
    admin_email VARCHAR(255) NOT NULL,
    admin_password VARCHAR(255) NOT NULL,
    admin_number VARCHAR(255) NOT NULL,
    PRIMARY KEY (admin_id),
    CONSTRAINT uk_admin_email UNIQUE (admin_email)
);

CREATE TABLE IF NOT EXISTS product_table (
    pid INT NOT NULL AUTO_INCREMENT,
    pname VARCHAR(255) NOT NULL,
    pprice DECIMAL(10,2) NOT NULL,
    pdescription VARCHAR(255),
    category VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    stock_quantity INT NOT NULL,
    active BIT NOT NULL,
    PRIMARY KEY (pid),
    CONSTRAINT uk_product_name UNIQUE (pname)
);

CREATE TABLE IF NOT EXISTS `orders` (
    o_id INT NOT NULL AUTO_INCREMENT,
    o_name VARCHAR(255),
    o_price DECIMAL(10,2),
    o_quantity INT NOT NULL,
    order_date DATETIME(6),
    total_ammout DECIMAL(12,2),
    status VARCHAR(24),
    user_u_id INT,
    product_pid INT,
    PRIMARY KEY (o_id),
    KEY idx_orders_user (user_u_id),
    KEY idx_orders_product (product_pid),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_u_id) REFERENCES `user` (u_id),
    CONSTRAINT fk_orders_product FOREIGN KEY (product_pid) REFERENCES product_table (pid)
);
