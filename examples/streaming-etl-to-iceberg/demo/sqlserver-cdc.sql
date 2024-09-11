--EXEC sys.sp_cdc_enable_db
--go

EXEC sys.sp_cdc_enable_table 
@source_schema = N'employees', 
@source_name = N'employees', 
@role_name = N'Admin', 
@supports_net_changes = 1 
GO


GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA :: cdc TO cdc;
GO


EXEC sys.sp_cdc_help_change_data_capture 