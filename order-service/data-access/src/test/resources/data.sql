INSERT INTO orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
    VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '11.49', 'PAID', 'fail1,fail2');

INSERT INTO orders_items(id, order_id, price, product_id, quantity, sub_total)
    VALUES (1, '00000000-0000-0000-0000-000000000001', '2.25','00000000-0000-0000-0000-000000000001', 1, '2.25');
INSERT INTO orders_items(id, order_id, price, product_id, quantity, sub_total)
    VALUES (2, '00000000-0000-0000-0000-000000000001', '9.24','00000000-0000-0000-0000-000000000002', 2, '9.24');

INSERT INTO order_address(id, order_id, street, city, country, postal_code)
    VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '123 Test St.', 'Test city', 'Test country', '123456');

INSERT INTO restaurant_approval_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '2023-10-13 14:00:00+00:00',
            '2023-10-13 14:01:00+00:00', 'OrderProcessingSaga', 'restaurant_approval_outbox db test payload', 'SUCCEEDED', 'APPROVED', 'COMPLETED', 2);
INSERT INTO restaurant_approval_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '2023-11-23 15:00:00+00:00',
        '2023-11-23 15:01:00+00:00', 'OrderProcessingSaga', 'restaurant_approval_outbox db test payload', 'SUCCEEDED', 'APPROVED', 'COMPLETED', 3);