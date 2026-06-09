# GameShop

A functional prototype for a game activation code store, built with **Compose Multiplatform** and the new **Material 3 Expressive** experimental features.

## Features
- **Material 3 Expressive Theme:** Uses the new `MaterialExpressiveTheme` and physics-based `MotionScheme`.
- **Game Listings:** Browse featured games from various sources (Steam, Epic, GOG).
- **Interactive UI:** Smooth transitions and expressive components like the morphing `LoadingIndicator`.
- **Responsive Grid:** Adapts to different window sizes.

## How to Run
Ensure you have Java 21+ and Gradle installed.

```bash
# From the project root
gradle run
```

## Technical Details
- **Framework:** Compose Multiplatform 1.7.1
- **Material 3:** 1.9.0-alpha04 (Experimental)
- **Kotlin:** 2.1.0
- **Motion:** Physics-based spring animations via `MotionScheme.expressive()`.
