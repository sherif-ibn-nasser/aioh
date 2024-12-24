## Timer

### Purpose

The **Timer** component is essential for maintaining consistent frame rates and synchronizing animations. It ensures
that graphics are rendered smoothly and efficiently.

### Implementation

A simple timer in Aioh is implemented as:

```java
long lastTime = System.nanoTime();
final double nsPerFrame = 1_000_000_000.0 / 60; // 60 FPS

while(running){
long now = System.nanoTime();
double delta = (now - lastTime) / nsPerFrame;
lastTime =now;

update(delta);

render();
}
```