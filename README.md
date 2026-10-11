# Algorithm Visualizer (Java SE & Swing)

A desktop interactive application built from scratch in Java SE demonstrating **Sorting**, **Searching**, and **Graph/Pathfinding** algorithms with real-time step-by-step animations, custom `Graphics2D` rendering, and telemetry metrics.

---

## 🚀 Key Features

### 1. 📊 Sorting Algorithms
- **Algorithms**: Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort, Heap Sort.
- **Visual Features**:
  - Color-coded bar states: *Comparing (Amber)*, *Swapping (Crimson)*, *Pivot (Purple)*, *Sorted (Emerald Green)*.
  - Dynamic gradient bars with auto-sizing and numerical labels.
  - Array distribution presets: *Random*, *Reversed*, *Nearly Sorted*, *Few Unique*.
  - Configurable array size slider (10 to 120 elements).
  - Real-time time & space complexity badges.

### 2. 🔍 Searching Algorithms
- **Algorithms**: Linear Search, Binary Search.
- **Visual Features**:
  - Horizontal interactive card deck with index badges.
  - Floating indicator pointers (`[LOW]`, `[MID]`, `[HIGH]`, `[SCAN]`).
  - Search interval elimination visualizer.
  - Target value picker (*Pick Random Target* or manual integer input).

### 3. 🌐 Graph & Pathfinding Algorithms
- **Algorithms**: Dijkstra's Algorithm, Breadth-First Search (BFS), Depth-First Search (DFS).
- **Network Visual Features**:
  - Circular vertices with centered numerical identifiers (`0`, `1`, `2`, ...).
  - Straight-line edges connecting node perimeters.
  - Direction arrowheads on directed edges.
  - Weight badges rendered directly on edge lines for weighted graphs.
  - Support for **Undirected Unweighted**, **Directed Weighted**, **Undirected Weighted**, and **Directed Unweighted** graphs.
  - **Edge Traversal Animation**: Glowing amber highlight along edges as the algorithm traverses between nodes.
  - **Start & Target Node Selectors**: Specify start vertex and destination vertex for Dijkstra.
  - **Total Path Sum Display**: Calculates and displays the total shortest path cost and sequence at completion (e.g. `Shortest Path: 0 ➔ 2 ➔ 5 | Total Path Sum: 14`).
  - **Interactive Drag & Drop**: Click and drag any node circle to reposition vertices in real time.

### 4. 🎛 Unified Control & Telemetry Dock
- **Playback**: ▶ Start, ❚❚ Pause, ⏭ Step Forward, ↺ Reset.
- **Speed Slider**: Real-time animation speed control (1% to 100%).
- **Live Telemetry Dashboard**:
  - `Status`: READY, RUNNING, PAUSED, FINISHED.
  - `Comparisons`: Real-time comparison count.
  - `Operations`: Swaps, node visits, and array accesses.
  - `Elapsed Time`: Millisecond timer.

---

## 🏛 Object-Oriented Architecture & Design Patterns

- **Strategy Pattern**: Decoupled algorithm execution via interfaces:
  - [`SortingAlgorithm`](file:///c:/Users/SUMIT%20KUMAR%20SAH/OneDrive/Desktop/Java%20Development/AlgorithmVisulizerUsingAntigravity/src/com/visualizer/sorting/SortingAlgorithm.java)
  - [`SearchAlgorithm`](file:///c:/Users/SUMIT%20KUMAR%20SAH/OneDrive/Desktop/Java%20Development/AlgorithmVisulizerUsingAntigravity/src/com/visualizer/searching/SearchAlgorithm.java)
  - [`PathfindingAlgorithm`](file:///c:/Users/SUMIT%20KUMAR%20SAH/OneDrive/Desktop/Java%20Development/AlgorithmVisulizerUsingAntigravity/src/com/visualizer/graph/PathfindingAlgorithm.java)
- **Observer Pattern**: [`VisualizerListener`](file:///c:/Users/SUMIT%20KUMAR%20SAH/OneDrive/Desktop/Java%20Development/AlgorithmVisulizerUsingAntigravity/src/com/visualizer/core/VisualizerListener.java) broadcasts telemetry metrics to dashboard cards without coupling algorithms to GUI components.
- **Multithreading & Synchronization**: [`AnimationController`](file:///c:/Users/SUMIT%20KUMAR%20SAH/OneDrive/Desktop/Java%20Development/AlgorithmVisulizerUsingAntigravity/src/com/visualizer/core/AnimationController.java) manages worker threads using `wait()` and `notifyAll()` so the UI remains 100% responsive during animations.

---

## 🏃 How to Run

### Option 1: Using the PowerShell Script (Easiest)
In your terminal, simply run:
```powershell
.\run.ps1
```

### Option 2: Using the Batch File
In Windows Command Prompt or by double-clicking:
```cmd
run.bat
```

### Option 3: Direct Java Command
```powershell
& "C:\Program Files\Java\jdk-26.0.2\bin\java.exe" -cp out com.visualizer.Main
```
*(Make sure to include the `java` or `& "..."` executable at the front, not just `-cp`)*

