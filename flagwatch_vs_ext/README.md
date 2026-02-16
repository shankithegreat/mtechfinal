# FlagWatch VS Code Extension

FlagWatch is an auto-documentation system for feature flags in VS Code. It helps developers track, visualize, and validate feature flag usage across their codebase.

## Features

### 1. Auto-Documentation
*   **Real-time Monitoring**: Automatically detects feature flag definitions (starting with `FEATURE_` or `FLAG_`) in TypeScript/JavaScript files.
*   **JSON Generation**: Generates and updates a `feature_flags_doc.json` file in the workspace root whenever a flag is modified or a file is saved.

### 2. Validation & Linting
FlagWatch provides real-time diagnostics (squiggly lines) for best practices:
*   **No Magic Strings**: Warns if you use a feature flag as a string literal (e.g., `'FEATURE_NEW_UI'`). You should define constants.
*   **Error Handling**: Warns if a feature flag is used in logic without being wrapped in a `try/catch` block or error handling mechanism.

### 3. Visualization
*   **Dependency Diagrams**: View a visual graph of your feature flags and the files that depend on them.
*   **Webview Panel**: Accessible via the "FlagWatch" activity bar item.

### 4. Git Integration
*   **Commit Monitoring**: Updates documentation automatically when Git commits occur (watches `.git/HEAD`).

## setup

1.  Run `npm install` to install dependencies.
2.  Press `F5` to open a new VS Code window with the extension loaded.
3.  Open a workspace containing TypeScript/JavaScript files.

## Usage

1.  **Define a Flag**:
    ```typescript
    const FEATURE_DARK_MODE = "on";
    ```
2.  **Use a Flag**:
    ```typescript
    if (FEATURE_DARK_MODE === "on") {
        // ...
    }
    ```
3.  **Check Documentation**: Look for `feature_flags_doc.json` in your root.
4.  **View Diagram**: Click the FlagWatch icon in the Activity Bar.
