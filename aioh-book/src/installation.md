## Installation

To get started with **Aioh**, follow these steps to set up your environment and build the project using Gradle, Java 21,
and LWJGL.

### 1. **Clone the Repository**

First, clone the **Aioh** repository from GitHub:

   ```bash
   git clone https://github.com/sherif-ibn-nasser/aioh.git
   cd aioh
   ```

### 2. **Prerequisites**

Ensure that the following tools are installed:

- **Java Development Kit (JDK 21)**: Aioh is built using Java 21. You can download it
  from [Oracle's website](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html) or install OpenJDK 21
  using your package manager.
- **Gradle**: Aioh uses Gradle for dependency management and project building. To install Gradle, follow the
  guide [here](https://gradle.org/install/).
- **LWJGL (Lightweight Java Game Library)**: Aioh uses LWJGL for OpenGL integration. The necessary LWJGL dependencies
  are managed by Gradle and will be downloaded automatically during the build process.

### 3. **Build the Project**

Once the repository is cloned, build **Aioh** using Gradle:

   ```bash
   gradle build
   ```

This will download all required dependencies, including LWJGL, and compile the project. If you're using an IDE like
IntelliJ IDEA or Eclipse, you can import the Gradle project and build it directly within the IDE.

### 4. **Run Aioh**

After the project is built, run **Aioh** using Gradle:

   ```bash
   gradle run
   ```

Alternatively, you can run it directly from your IDE.

### 5. **IDE Setup**

- **IntelliJ IDEA**:
    - Open IntelliJ IDEA and select "Open" to open the **Aioh** project directory.
    - IntelliJ will automatically detect the Gradle project and download the necessary dependencies, including LWJGL.
    - Once setup is complete, you can run the project directly from the IDE.

- **Eclipse**:
    - Open Eclipse and select "Import" from the File menu.
    - Choose "Gradle" -> "Existing Gradle Project" and import the project.
    - Once the project is imported, you can run it from Eclipse.

### 6. **Optional: Set Up OpenGL**

**Aioh** uses LWJGL for OpenGL rendering. Make sure your system has the necessary OpenGL drivers installed:

- **Windows**: Ensure your GPU drivers (NVIDIA, AMD, or Intel) are up to date.
- **macOS**: OpenGL is pre-installed on macOS.
- **Linux**: You may need to install `mesa-utils` or other OpenGL-related libraries through your package manager.

### 7. **Troubleshooting**

- **Missing Dependencies**: Ensure that Gradle has correctly downloaded all dependencies, including LWJGL.
- **Graphics Driver Issues**: Make sure your graphics drivers are up to date for OpenGL functionality.
- **Error Logs**: If you encounter issues, check the logs in your terminal or IDE for more details.
