-- PostgreSQL source
CREATE TABLE shipments (
  shipment_id SERIAL NOT NULL PRIMARY KEY,
  order_id SERIAL NOT NULL,
  origin VARCHAR(255) NOT NULL DEFAULT 'W1',
  destination VARCHAR(255) NOT NULL,
  has_arrived BOOLEAN NOT NULL
);
ALTER SEQUENCE public.shipments_shipment_id_seq RESTART WITH 1001;
ALTER TABLE public.shipments REPLICA IDENTITY FULL;
INSERT INTO shipments
VALUES (default,10001,'W2','Shanghai',false),
       (default,10002,'W3','Shanghai',false),
       (default,10003,'W1','Hangzhou',false);