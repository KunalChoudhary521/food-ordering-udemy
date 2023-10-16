INSERT INTO customer.order_customer_m_view(id) VALUES ('00000000-0000-0000-0000-000000000001');

INSERT INTO restaurant.order_restaurant_m_view(id, product_id, name, active, product_name, product_price, product_available)
    VALUES('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'r1', true, 'p1', '13.00', true);
INSERT INTO restaurant.order_restaurant_m_view(id, product_id, name, active, product_name, product_price, product_available)
    VALUES('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'r1', true, 'p2', '12.00', true);

INSERT INTO orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
    VALUES('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000010',
           '00000000-0000-0000-0000-000000000010', '24.99', 'CANCELLED', 'fail1,fail2,fail3');