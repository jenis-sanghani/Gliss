# ✨ Gliss – Motion UI Experience

> A refined Android application focused on fluid motion, immersive visuals, and modern interaction design — built with Jetpack Compose and GPU shaders.

<p align="center">
  <img src="https://img.shields.io/github/stars/jenis-sanghani/gliss?style=for-the-badge" />
  <img src="https://img.shields.io/github/forks/jenis-sanghani/gliss?style=for-the-badge" />
  <img src="https://img.shields.io/github/issues/jenis-sanghani/gliss?style=for-the-badge" />
  <img src="https://img.shields.io/github/license/jenis-sanghani/gliss?style=for-the-badge" />
  <img src="https://komarev.com/ghpvc/?username=jenis-sanghani&repo=gliss&style=for-the-badge" />
</p>

---

## About

**Gliss** explores the intersection of motion, interaction, and visual engineering on Android.  
The project emphasizes smooth transitions, minimal UI, and immersive background effects to create a calm yet futuristic user experience.

---

## Tech Stack

| Category | Technology |
|---------|------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| Navigation | Type-safe Compose Navigation |
| Graphics | RuntimeShader (AGSL) |
| Media | Media3 / ExoPlayer |
| Animation | Animatable, graphicsLayer |
| State | StateFlow |

---

## Core Features

- **Scientific Onboarding**  
  A swipe-based, 4-step flow with fixed alignment for visual consistency.

- **Interactive Visual Lab**  
  Cinematic video backgrounds combined with real-time shader distortion.

- **Gesture Lab**  
  Physics-based interactions with subtle haptic feedback.

- **Premium Experience**  
  A subscription screen featuring generative shader artwork.

- **Card Stack UI**  
  A 3D swipeable deck with parallax and rotation effects.

---

## Design Direction

- Minimal interface
- Calm visual tone
- Refined typography
- Soft neon accents
- Smooth fade and slide transitions

> The focus is clarity, balance, and visual comfort.

---

## Project Structure

```text
com.gliss.motionui
│
├── ui.screens        # Main composables
├── ui.components     # Reusable UI elements
├── navigation        # NavGraph & Screen definitions
├── billing           # Premium state management
├── analytics         # Event tracking
└── ui.theme          # Design system


Data Flow
UI → ViewModel → Repository  
UI ← StateFlow ← ViewModel
Visual Layering
Each screen typically follows this structure:
Background (Shader / Video)
        ↓
Dark Overlay
        ↓
UI Content
This keeps text readable while preserving visual depth.
```

## Repository Insights
## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=jenis-sanghani/gliss&type=Date)](https://star-history.com/#jenis-sanghani/gliss&Date)

## Setup

```bash
git clone https://github.com/jenis-sanghani/gliss.git
Open with Android Studio
Minimum SDK: 26+
Recommended: Android 13+ for shader support
```

License
MIT License

### Support - 
- If you find Gliss useful, consider giving it a Star(⭐)
- Your support helps the project grow, Contributions are welcome.
1. Create a feature branch
2. Submit a pull request
3. Keep changes clean and consistent
