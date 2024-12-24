## AiohDBTable

Represents a database table in a Structure of Arrays (SoA) format. In this format, each column is represented as a
separate list, where each list contains the cells (data values) for that specific column. The columns are organized by
their names, types, and the list of cells (values) for each column.

This design allows efficient processing and manipulation of columns, making it ideal for certain operations like
filtering, aggregating, and modifying individual columns.

### Attributes

- **`columnsNames`**: A list of column names represented as strings. It defines the names of each column in the table.
- **`columnsTypes`**: A list of data types for each column (represented by the `DataType` enum). It specifies the data
  type of each column, ensuring that the data in the column conforms to its type.
- **`columnsCells`**: A list of lists, where each inner list contains `StringBuilder` objects. Each inner list
  represents a column, and each `StringBuilder` object holds a cell's value in that column.

### Methods

- **`columnsSize()`**:
  Returns the number of columns in the table. This is equal to the size of the `columnsNames` list.

- **`rowsSize()`**:
  Returns the number of rows in the table. This is determined by the size of the first column in `columnsCells`.

### Usage Example

```java
AiohDBTable table = new AiohDBTable(
        new ArrayList<>(List.of("id", "name", "age")),
        new ArrayList<>(List.of(DataType.INTEGER, DataType.STRING, DataType.INTEGER)),
        new ArrayList<>(List.of(
                new ArrayList<>(List.of(new StringBuilder("1"), new StringBuilder("2"))), // id column
                new ArrayList<>(List.of(new StringBuilder("Alice"), new StringBuilder("Bob"))), // name column
                new ArrayList<>(List.of(new StringBuilder("30"), new StringBuilder("25"))) // age column
        ))
);

int columnCount = table.columnsSize(); // Returns 3 (for id, name, age)
int rowCount = table.rowsSize(); // Returns 2 (for two rows)
```

### Implementation

src/main/java/com/aioh/database/AiohDBTable.java:

```java 
{{#include ../../../src/main/java/com/aioh/database/AiohDBTable.java}}
```