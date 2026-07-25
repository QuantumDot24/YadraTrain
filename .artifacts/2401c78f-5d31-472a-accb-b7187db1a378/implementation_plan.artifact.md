# Translate all files to English

This plan involves translating all non-English text (mostly Spanish comments, log messages, and internal strings) into English to ensure a consistent English-only codebase.

## User Review Required

> [!NOTE]
> The UI strings are already mostly in English. The changes will primarily affect code comments, log messages, and internal documentation within the code.

## Proposed Changes

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/build.gradle.kts)
- Translate comments like `// Corutinas` to `// Coroutines`, `// NUEVO: ...` to `// NEW: ...`, etc.

### UI Screens

#### [MODIFY] [MainActivity.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/MainActivity.kt)
- Translate the shared ViewModel comment to English.

#### [MODIFY] [ResultsScreen.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/ui/screens/ResultsScreen.kt)
- Translate internal comments like `// ── Mapeos por preset ──` and `// Real solo si falló`.

#### [MODIFY] [AutoencoderResultsScreen.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/ui/screens/AutoencoderResultsScreen.kt)
- Translate comments like `// MSE promedio` and `// Mini barra de MSE relativa al peor del lote`.

#### [MODIFY] [SummaryScreen.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/ui/screens/SummaryScreen.kt)
- Translate the autoencoder accuracy comment.

### Theme & Styling

#### [MODIFY] [Color.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/ui/theme/Color.kt)
- Translate all comments describing colors and accents.

#### [MODIFY] [Theme.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/ui/theme/Theme.kt)
- Translate the dark theme justification comment.

#### [MODIFY] [Type.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/ui/theme/Type.kt)
- Translate comments about font requirements and the JetBrains Mono note.

### ViewModel & Logic

#### [MODIFY] [TrainingViewModel.kt](file:///C:/Users/angel/AndroidStudioProjects/YadraTrain/app/src/main/java/com/complexsoft/yadratrain/ui/viewmodel/TrainingViewModel.kt)
- Translate all log messages (e.g., `nativeInit devolvió 0`) and step-by-step training comments.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure the project still compiles after all comment and string changes.

### Manual Verification
- Review the modified files to ensure the English translations are accurate and natural.
