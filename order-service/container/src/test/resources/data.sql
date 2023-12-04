-- OrderServiceApplicationTest
INSERT INTO customer.order_customer_m_view(id) VALUES ('00000000-0000-0000-0000-000000000001');

INSERT INTO restaurant.order_restaurant_m_view(id, product_id, name, active, product_name, product_price, product_available)
    VALUES('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'r1', true, 'p1', '13.00', true);
INSERT INTO restaurant.order_restaurant_m_view(id, product_id, name, active, product_name, product_price, product_available)
    VALUES('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002', 'r1', true, 'p2', '12.00', true);

INSERT INTO orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
    VALUES('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000010',
           '00000000-0000-0000-0000-000000000010', '24.99', 'CANCELLED', 'fail1,fail2,fail3');

-- PaymentOutboxCleanerSchedulerTest
INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '2023-10-16 14:00:00+00:00',
           '2023-10-16 14:01:00+00:00', 'OrderProcessingSaga', '{payload: "outbox_msg1"}', 'FAILED', 'CANCELLING', 'COMPLETED', 0);
INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002', '2023-10-15 16:00:00+00:00',
           '2023-10-15 16:01:00+00:00', 'OrderProcessingSaga', '{payload: "outbox_msg2"}', 'SUCCEEDED', 'APPROVED', 'COMPLETED', 0);
INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003', '2023-10-16 21:00:00+00:00',
           '2023-10-16 21:01:00+00:00', 'OrderProcessingSaga', '{payload: "outbox_msg3"}', 'COMPENSATED', 'CANCELLED', 'COMPLETED', 0);
INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000004', '2023-10-18 17:00:00+00:00',
           '2023-10-18 17:01:00+00:00', 'OrderProcessingSaga',
           '{"orderId": "00000000-0000-0000-0000-00000000000f", "customerId": "00000000-0000-0000-0000-00000000000f", "price": 47.23, "createdAt": "2023-10-18T17:00:00.000Z", "paymentOrderStatus": "PENDING"}',
           'COMPENSATING', 'CANCELLING', 'STARTED', 0);

-- PaymentResponseKafkaListenerTest
-- Payment Completed status
INSERT INTO orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
    VALUES('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
           '00000000-0000-0000-0000-000000000011', '24.98', 'PENDING', '');
INSERT INTO orders_items(id, order_id, price, product_id, quantity, sub_total)
    VALUES (1, '00000000-0000-0000-0000-000000000011', '12.49','00000000-0000-0000-0000-000000000001', 2, '24.98');

INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000005', '2023-10-21 17:00:00+00:00',
           '2023-10-21 17:01:00+00:00', 'OrderProcessingSaga',
           '{"orderId": "00000000-0000-0000-0000-000000000011", "customerId": "00000000-0000-0000-0000-000000000001", "price": 24.98, "createdAt": "2023-10-21T17:00:00.000Z", "paymentOrderStatus": "PENDING"}',
           'STARTED', 'PENDING', 'STARTED', 0);

-- Payment Failed status
INSERT INTO orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
    VALUES('00000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
           '00000000-0000-0000-0000-000000000011', '24.98', 'PENDING', '');
INSERT INTO orders_items(id, order_id, price, product_id, quantity, sub_total)
    VALUES (1, '00000000-0000-0000-0000-000000000012', '12.49','00000000-0000-0000-0000-000000000001', 2, '24.98');

INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000006', '2023-12-03 18:00:00+00:00',
           '2023-12-03 18:01:00+00:00', 'OrderProcessingSaga',
           '{"orderId": "00000000-0000-0000-0000-000000000011", "customerId": "00000000-0000-0000-0000-000000000001", "price": 24.98, "createdAt": "2023-12-03T17:00:00.000Z", "paymentOrderStatus": "PENDING"}',
           'PROCESSING', 'PENDING', 'STARTED', 0);
INSERT INTO restaurant_approval_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000006', '2023-12-03 18:00:00+00:00',
           '2023-12-03 18:01:00+00:00', 'OrderProcessingSaga',
           '{"orderId": "00000000-0000-0000-0000-000000000011", "customerId": "00000000-0000-0000-0000-000000000001", "price": 24.98, "createdAt": "2023-12-03T17:00:00.000Z", "paymentOrderStatus": "PENDING"}',
           'COMPENSATING', 'PENDING', 'STARTED', 0);

-- RestaurantApprovalResponseKafkaListenerTest
-- Order Approved
INSERT INTO orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
    VALUES('00000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
           '00000000-0000-0000-0000-000000000011', '24.98', 'PAID', '');
INSERT INTO orders_items(id, order_id, price, product_id, quantity, sub_total)
    VALUES (1, '00000000-0000-0000-0000-000000000013', '12.49','00000000-0000-0000-0000-000000000001', 2, '24.98');

INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
VALUES('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000007', '2023-12-10 10:00:00+00:00',
       '2023-12-10 10:01:00+00:00', 'OrderProcessingSaga',
       '{"orderId": "00000000-0000-0000-0000-000000000013", "customerId": "00000000-0000-0000-0000-000000000001", "price": 24.98, "createdAt": "2023-12-10T10:00:00.000Z", "paymentStatus": "COMPLETED"}',
       'PROCESSING', 'PAID', 'STARTED', 0);
INSERT INTO restaurant_approval_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000007', '00000000-0000-0000-0000-000000000007', '2023-12-10 10:00:00+00:00',
           '2023-12-10 10:01:00+00:00', 'OrderProcessingSaga',
           '{"orderId": "00000000-0000-0000-0000-00000000013", "sagaId": "00000000-0000-0000-0000-00000000007", "restaurantId": "00000000-0000-0000-0000-000000000001", "createdAt": "2023-12-10T10:00:00.000Z", "orderApprovalStatus": "APPROVED"}',
           'PROCESSING', 'PAID', 'STARTED', 0);

-- Order Rejected
INSERT INTO orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
    VALUES('00000000-0000-0000-0000-000000000014', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
           '00000000-0000-0000-0000-000000000011', '24.98', 'PAID', '');
INSERT INTO orders_items(id, order_id, price, product_id, quantity, sub_total)
    VALUES (1, '00000000-0000-0000-0000-000000000014', '12.49','00000000-0000-0000-0000-000000000001', 2, '24.98');

INSERT INTO payment_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000008', '2023-12-10 12:00:00+00:00',
           '2023-12-10 12:01:00+00:00', 'OrderProcessingSaga',
           '{"orderId": "00000000-0000-0000-0000-000000000014", "customerId": "00000000-0000-0000-0000-000000000001", "price": 24.98, "createdAt": "2023-12-10T12:00:00.000Z", "paymentStatus": "COMPLETED"}',
           'PROCESSING', 'PAID', 'STARTED', 0);
INSERT INTO restaurant_approval_outbox(id, saga_id, created_at, processed_at, type, payload, saga_status, order_status, outbox_status, version)
    VALUES('00000000-0000-0000-0000-000000000008', '00000000-0000-0000-0000-000000000008', '2023-12-10 12:00:00+00:00',
           '2023-12-10 12:01:00+00:00', 'OrderProcessingSaga',
           '{"orderId": "00000000-0000-0000-0000-00000000014", "sagaId": "00000000-0000-0000-0000-00000000008", "restaurantId": "00000000-0000-0000-0000-000000000001", "createdAt": "2023-12-10T12:00:00.000Z", "orderApprovalStatus": "REJECTED"}',
           'PROCESSING', 'PAID', 'STARTED', 0);