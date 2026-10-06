#!/bin/bash
# ═══════════════════════════════════════════════════════
# AttendX Build Script
# ═══════════════════════════════════════════════════════
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_DIR/src"
OUT_DIR="$PROJECT_DIR/out"
LIB_DIR="$PROJECT_DIR/lib"
RESOURCES_DIR="$PROJECT_DIR/resources"
JAVAFX_MODS="/usr/share/openjfx/lib"

echo "╔═══════════════════════════════════════╗"
echo "║       AttendX Build & Run             ║"
echo "╚═══════════════════════════════════════╝"

# Create output directory
mkdir -p "$OUT_DIR"

# Copy resources (CSS, assets)
mkdir -p "$OUT_DIR/styles"
if [ -d "$SRC_DIR/styles" ]; then
    cp "$SRC_DIR/styles/"*.css "$OUT_DIR/styles/" 2>/dev/null || true
fi

# Build classpath
CP="$LIB_DIR/json-simple.jar"

# Collect all .java files
JAVA_FILES=$(find "$SRC_DIR" -name "*.java" | tr '\n' ' ')

echo "→ Compiling sources..."
javac \
    --module-path "$JAVAFX_MODS" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.swing,javafx.web \
    -cp "$CP" \
    -d "$OUT_DIR" \
    $JAVA_FILES \
    2>&1

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful"
else
    echo "✗ Compilation failed"
    exit 1
fi

echo ""
echo "→ Running AttendX..."
java \
    --module-path "$JAVAFX_MODS" \
    --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.swing,javafx.web \
    -cp "$OUT_DIR:$CP" \
    app.Main \
    2>&1
