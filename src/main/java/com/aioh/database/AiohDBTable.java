package com.aioh.database;

import java.util.ArrayList;

/**
 * Represents a database table in a Structure of Arrays (SoA) format.
 * In this format, each column is represented as a separate list, where each list contains
 * the cells (data values) for that specific column. The columns are organized by their names,
 * types, and the list of cells (values) for each column.
 *
 * <p>This design allows efficient processing and manipulation of columns, making it ideal for
 * certain operations like filtering, aggregating, and modifying individual columns.</p>
 *
 * <h3>Attributes:</h3>
 * <ul>
 *   <li><b>columnsNames</b>: A list of column names represented as strings.
 *       It defines the names of each column in the table.</li>
 *   <li><b>columnsTypes</b>: A list of data types for each column (represented by the enum <code>DataType</code>).
 *       It specifies the data type of each column, ensuring that the data in the column conforms to its type.</li>
 *   <li><b>columnsCells</b>: A list of lists, where each inner list contains <code>StringBuilder</code> objects.
 *       Each inner list represents a column, and each <code>StringBuilder</code> object holds a cell's value in that column.</li>
 * </ul>
 *
 * <h3>Methods:</h3>
 * <ul>
 *   <li><b>columnsSize()</b>:
 *       <p>Returns the number of columns in the table. This is equal to the size of the <code>columnsNames</code> list.</p></li>
 *   <li><b>rowsSize()</b>:
 *       <p>Returns the number of rows in the table. This is determined by the size of the first column in <code>columnsCells</code>.</p></li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <pre>
 * AiohDBTable table = new AiohDBTable(
 *     new ArrayList<>(List.of("id", "name", "age")),
 *     new ArrayList<>(List.of(DataType.INTEGER, DataType.STRING, DataType.INTEGER)),
 *     new ArrayList<>(List.of(
 *         new ArrayList<>(List.of(new StringBuilder("1"), new StringBuilder("2"))), // id column
 *         new ArrayList<>(List.of(new StringBuilder("Alice"), new StringBuilder("Bob"))), // name column
 *         new ArrayList<>(List.of(new StringBuilder("30"), new StringBuilder("25"))) // age column
 *     ))
 * );
 *
 * int columnCount = table.columnsSize(); // Returns 3 (for id, name, age)
 * int rowCount = table.rowsSize(); // Returns 2 (for two rows)
 * </pre>
 *
 * <h3>Note:</h3>
 * <p>The <code>columnsCells</code> structure assumes all columns have the same number of rows.
 * If the number of rows differs between columns, it could lead to unexpected behavior. Ensure the
 * number of rows in each column is consistent when manipulating this structure.</p>
 *
 * @author Sherif Nasser
 * @see DataType
 */
public record AiohDBTable(
        ArrayList<String> columnsNames,
        ArrayList<DataType> columnsTypes,
        ArrayList<ArrayList<StringBuilder>> columnsCells
) {

    public int columnsSize() {
        return columnsNames.size();
    }

    public int rowsSize() {
        return columnsCells.getFirst().size();
    }
}
