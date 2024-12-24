## The Buttons Selector

The **Buttons Selector** is a screen in the Aioh text editor designed to facilitate navigation and selection among
predefined options. It can dynamically display a list of selectable lines and provides functionality for highlighting
and navigating between these options. The component is particularly useful for scenarios like confirmation dialogs
with "Yes" and "No" buttons.

### Core Features

1. **Static Button Initialization**

    - The Buttons Selector includes predefined "Yes" and "No" options, stored as static `StringBuilder` instances.
    - These buttons are managed through a static list (`NO_YES_BUTTONS`) to allow quick and efficient reuse.

2. **Dynamic Line Management**

    - The selector can dynamically load and display any list of options:
        - `setLines(List<String>)`: Converts a `List<String>` into a list of `StringBuilder` objects.
        - `setLines(ArrayList<StringBuilder>)`: Directly assigns a list of `StringBuilder` objects to the selector and
          computes the maximum length of the options for rendering.

3. **Selection Handling**

    - The currently selected option is tracked using the `cursorLine` variable.
    - `getSelected()`: Retrieves the currently highlighted option, allowing integration with other components.

4. **User Interaction**

    - Supports navigation using keyboard input:
        - **Up Arrow (↑)**: Moves the selection upwards. Wraps to the last option if the first option is reached.
        - **Down Arrow (↓)**: Moves the selection downwards. Wraps to the first option if the last option is reached.

5. **Rendering**

    - The selector highlights the current option with a rectangular overlay for visual feedback.
    - Automatically adjusts the camera position and alignment based on the longest line.

### Full code

src/main/java/com/aioh/AiohButtonsSelector.java:

```java
{{#include ../../src/main/java/com/aioh/AiohButtonsSelector.java}}
```
