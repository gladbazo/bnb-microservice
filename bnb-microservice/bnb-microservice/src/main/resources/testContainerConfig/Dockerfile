# Use the official Microsoft SQL Server image as the base image
FROM mcr.microsoft.com/mssql/server:2022-latest

# Set the environment variables for SQL Server
ENV ACCEPT_EULA=Y
ENV SA_PASSWORD=YourStrong@Password
ENV MSSQL_COLLATION=Cyrillic_General_CI_AS

USER root
RUN mkdir -p /var/opt/mssql/data
RUN chown -R mssql:root /var/opt/mssql/
USER mssql