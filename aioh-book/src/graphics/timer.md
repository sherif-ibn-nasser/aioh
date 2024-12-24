## Timer

### Purpose

The **Timer** component is essential for maintaining consistent frame rates and synchronizing animations. It ensures
that graphics are rendered smoothly and efficiently.

### Implementation

The timer class in Aioh depends on the [```glfwGetTime```](https://www.glfw.org/docs/3.0/group__time.html) function:

src/main/java/com/aioh/graphics/Timer.java:

```java
{{#include ../../../src/main/java/com/aioh/graphics/Timer.java}}
```