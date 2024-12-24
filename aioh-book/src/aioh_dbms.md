### Aioh Database Management System (AiohDBMS)

The Database Manager acts as the core logic for handling databases. It provides functionalities to:

1. **Explore existing databases**
2. **Create databases**
3. **Delete database**
4. **Explore database tables**
5. **Create/Drop tables**
6. **Update entries**

`AiohDBMS` consists of four core classes:

* **AiohDBManager**: Provides a java API to create, read, delete, and connect to databases on local machine
* **AiohDB**: Represents a concrete database object
* **AiohDBTable**: Holds the column infos and entries
* **DataType**: An enum representing SQL datatypes
