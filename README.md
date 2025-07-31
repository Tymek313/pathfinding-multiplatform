# Kotlin Multiplatform Pathfinding App

## 🎯 Features

- __Breadth first__ and __depth first__ pathfinding algorithms implementations
- Find path from the start node to the destination node using the selected algorithm and draw the path on the board (if
  such exists)
- Placing/removing obstacles with clicks or by dragging
- Moving start and destination nodes across the board
- Clearing board of the obstacles
- Restore board state on activity recreation (Android)
- Dynamic board size calulation based on available screen space
- Multilanguage support (English/Polish)

## 🔬 Technical details

- __Adaptive layout__ for the best UI experience on all platforms regardless of screen size
- Supported platforms: Desktop (Linux, Windows, MacOS), Android
- UI based on Compose multiplatform and Material 3
- Code quality verification with KtLint
- Dependency free domain module
- Clean architecture-based design
- Possibility for easy integration of other pathfinding algorithms via existing interface

## ✅ Validation

* Unit tests (Domain logic)
* Multiplatform UI tests (Screen and board component)

## 👀 Previews

![android](https://bitbucket.org/tymek313/pathfinding-multiplatform/raw/master/preview/android.gif)
![desktop](https://bitbucket.org/tymek313/pathfinding-multiplatform/raw/master/preview/desktop.gif)