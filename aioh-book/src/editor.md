## The core editor

The [`AiohEditor`](.https://github.com/sherif-ibn-nasser/aioh/blob/develop/src/main/java/com/aioh/AiohEditor.java) class
implements the main functionality for the Aioh
text editor. It handles the rendering of text,
user input, and camera controls to provide an interactive editing environment. Below is a breakdown of its
functionality.

### Constants

- **Colors:** Predefined colors for the editor (`AIOH_COLOR`, `TEXT_SELECTION_COLOR`, etc.).
- **Camera and Rendering:** Camera movement velocity, FPS, font size, and scaling thresholds.
- **Text Behavior:** Cursor blink timing, line capacity, etc.

### Core Components

1. **Rendering:**
    - Uses [
      `AiohRenderer`](https://github.com/sherif-ibn-nasser/aioh/blob/develop/src/main/java/com/aioh/graphics/AiohRenderer.java)
      for text and graphics rendering.
    - Supports dual shader programs (`colorProgram` and `mainProgram`) to handle selection highlights and main text
      drawing.

2. **Text Management:**
    - Manages text using a list of `StringBuilder` objects, each representing a line.
    - Tracks the cursor and selection state using line and column indices.

3. **Camera Handling:**
    - Automatically adjusts the camera position and scale based on text length and cursor position.
    - Smooth transitions are implemented using velocity calculations.

4. **Input Handling:**
    - Handles keyboard input for text insertion (`onTextInput`), navigation (arrow keys), and editing (Enter,
      Backspace).

### Key Methods

1. **`init()` Methods:**
    - Initializes the editor with an empty file or loads lines from a file into the `lines` list.

2. **Rendering Pipeline:**
    - `loop()`: The main rendering loop that updates the camera, sets up shaders, and invokes the drawing methods.
    - `onDrawMainProgram()` and `onDrawColorProgram()`: Separate text rendering from highlights.

3. **Camera Updates:**
    - `updateCameraPos()`: Moves the camera towards the cursor position smoothly.
    - `updateCameraScale()`: Adjusts the camera zoom level based on the content size.

4. **Text Rendering:**
    - `drawText()`: Renders the visible text.
    - `drawSelectedText()`: Highlights the selected text region.
    - `drawCursor()`: Blinks the cursor at the current position.

5. **Cursor Movement:**
    - Supports moving the cursor in response to arrow keys (`onUpArrowPressed`, `onDownArrowPressed`, etc.).
    - Maintains boundaries and wraps across lines as needed.

6. **Text Editing:**
    - Inserts or deletes text at the cursor position.
    - Supports splitting lines and merging them for Enter and Backspace functionality.

### Features & Considerations

- **Scalability:**
    - Dynamically adjusts camera and scaling for large amounts of text.
    - Optimizations for rendering only visible lines are noted as a TODO.

- **User Interactions:**
    - Robust handling of cursor movement, text input, and selection.

- **Extensibility:**
    - Methods like `onInit`, `onStartRendering`, and `onFinishRendering` allow for customization.
