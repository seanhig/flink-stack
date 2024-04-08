CREATE CATALOG hive_catalog WITH (
    'type' = 'hive',
    'hive-conf-dir' = '/opt/flink/conf'
);
-- set the HiveCatalog as the current catalog of the session
USE CATALOG hive_catalog;