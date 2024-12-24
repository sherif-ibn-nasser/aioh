## DoD vs OOP

In Aioh, the architecture is primarily driven by **Data-Oriented Design (DoD)**, a programming paradigm focused on
organizing and accessing data in memory in ways that maximize performance, particularly in tasks involving large data
sets or real-time operations.

### Data-Oriented Design (DoD)

**Data-Oriented Design (DoD)** prioritizes the structure and layout of data in memory. The main idea is to optimize how
data is accessed by the CPU, ensuring that data is stored in a way that minimizes cache misses and leverages the
hardware's memory access patterns effectively. This approach is particularly beneficial for systems that need to handle
large amounts of data quickly and efficiently.

#### Key Principles of DoD:

1. **Efficient Memory Access**:
    - Data is organized in memory in a linear or contiguous format, making it easier for the CPU to fetch large chunks
      of data into the cache at once. This reduces the number of cache misses and speeds up processing.

2. **Data Locality**:
    - By organizing data to be as close together as possible in memory (for example, in arrays or structs), DoD ensures
      that when one piece of data is accessed, related data is likely to be in cache as well, improving access speed.

3. **Minimizing Abstraction**:
    - DoD minimizes high-level abstractions that could hinder performance. Rather than encapsulating behavior within
      objects, DoD focuses on manipulating raw data directly, enabling the system to operate more efficiently.

4. **Parallelism**:
    - Data layouts optimized for DoD often align well with modern processors' vectorized operations and SIMD (Single
      Instruction, Multiple Data) features, allowing for parallel processing of data in a highly efficient manner.

5. **Separation of Data and Behavior**:
    - In DoD, behavior (functions or operations) is decoupled from the data. This allows for specialized,
      performance-optimized data processing operations that can be applied to raw data without the overhead of complex
      objects.

### Benefits of DoD in Aioh:

- **Performance**: By organizing data to maximize memory access efficiency, Aioh ensures that rendering and other
  critical operations are as fast as possible, even with large amounts of data.
- **Scalability**: DoD allows Aioh to scale to handle large documents or complex UI updates efficiently without
  incurring performance penalties.
- **Real-Time Responsiveness**: For a text editor, real-time responsiveness is critical. DoD helps Aioh quickly update
  and render changes to the document and UI, minimizing lag and improving the overall user experience.

### Why Not OOP?

While Aioh does use some **Object-Oriented Programming (OOP)** principles for modularity and structuring its codebase,
the focus on **DoD** was made because of performance requirements. OOP typically introduces overhead, such as indirect
method calls, memory fragmentation, and extra object allocations, which can hinder performance when working with large
datasets or frequent real-time operations. By minimizing the use of OOP and focusing on efficient data management, Aioh
achieves the necessary performance for handling large documents and responsive UI updates without the typical
bottlenecks of an OOP-heavy design.
Here's the revised version, with **Object-Oriented Programming (OOP)** first, followed by the identified problems, and
then the explanation of how **Data-Oriented Design (DoD)** addresses these issues:

### Example: `AiohDBTable`

In this example, we define a class that represents a simple table in a database. The table holds **column names**,
**column types**, and the **cells** for each column. We will compare how this design would look from both a
**Data-Oriented Design (DoD)** and **Object-Oriented Programming (OOP)** perspective.

### Object-Oriented Design (OOP)

In the **OOP approach**, the focus is on encapsulating both data and behavior within objects. Each column of the table
is represented as an object that holds its data (name, type, cells) and the behavior (methods to manage the data).
Here’s what this might look like in Java:

```java
public class AiohDBTable {
    private ArrayList<Column> columns;

    public AiohDBTable() {
        this.columns = new ArrayList<>();
    }

    public void addColumn(String name, DataType type) {
        columns.add(new Column(name, type));
    }

    public int columnsSize() {
        return columns.size();
    }

    public int rowsSize() {
        return columns.get(0).getCells().size();
    }

    public static class Column {
        private String name;
        private DataType type;
        private ArrayList<StringBuilder> cells;

        public Column(String name, DataType type) {
            this.name = name;
            this.type = type;
            this.cells = new ArrayList<>();
        }

        public ArrayList<StringBuilder> getCells() {
            return cells;
        }
    }
}
```

#### Key Features of the OOP Approach:

- **Encapsulation**: Each column is encapsulated within its own `Column` object, which holds both data (name, type, and
  cells) and behavior (methods for adding cells).
- **Object Overhead**: Each `Column` object introduces memory overhead due to the need to store metadata (like the
  reference to the name, type, and cells) in separate objects.
- **Memory Fragmentation**: The objects in the `AiohDBTable` are scattered in memory, which may cause fragmented memory
  layouts. This can lead to inefficient cache utilization and slower performance when processing large tables.
- **More Complex Layout**: The memory layout is more complex because each column is an object with its own set of
  fields. This increases the difficulty of optimizing memory usage and improving cache locality.

### Data-Oriented Design (DoD)

In **Data-Oriented Design (DoD)**, the goal is to structure data in a way that minimizes overhead, maximizes cache
efficiency, and ensures better memory layout for high-performance computing. Instead of encapsulating behavior with
data, DoD focuses on storing raw data in memory-efficient ways. Here's how the `AiohDBTable` might look in a more
DoD-friendly design:

```java
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
```

### How DoD Solves OOP Problems

1. **Reduced Memory Overhead**: In DoD, data is stored directly in arrays or lists with minimal overhead, reducing
   memory usage. There's no need for extra objects, which means that for large datasets, memory consumption is
   significantly lower.

2. **Improved Cache Efficiency**: Since data is stored in contiguous blocks of memory (like arrays within an
   `ArrayList`), the CPU can efficiently load large chunks of data into cache. This improves memory access speeds,
   especially for large tables, by ensuring that cache lines are fully utilized.

3. **More Control Over Memory Layout**: In DoD, you have direct control over the layout of the data in memory. This
   allows for tight packing of data, improving both memory access efficiency and the ability to optimize performance for
   large datasets.
