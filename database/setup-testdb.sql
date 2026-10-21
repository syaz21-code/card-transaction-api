-- Run in SQL Server Management Studio as an authorized admin.
-- This script creates the local assessment DB only; create a dedicated SQL login
-- separately using a strong private password. Do NOT commit a password.
IF DB_ID(N'TESTDB') IS NULL
BEGIN
    CREATE DATABASE TESTDB;
END;
GO
USE TESTDB;
GO
SELECT DB_NAME() AS database_name;
GO
