package com.aioh;

import com.aioh.database.AiohDB;
import com.aioh.database.AiohDBManager;
import com.aioh.database.AiohDBTable;
import com.aioh.database.DataType;
import com.aioh.graphics.AiohRenderer;
import glm_.vec4.Vec4;

import java.util.ArrayList;
import java.util.Comparator;

import static org.lwjgl.glfw.GLFW.*;

public class AiohDatabaseEditor extends AiohEditor {
    public static final Vec4 CELL_COLOR1 = new Vec4(0.4f);
    public static final Vec4 CELL_COLOR2 = new Vec4(0.5f);
    public static final float CELL_H_PADDING = FONT_SIZE / 2f;
    public static final float CELL_V_PADDING = FONT_SIZE / 2f;
    public static final int CELL_CHARS_THRESHOLD = 16;
    public static final int CELL_SPACING = 10;

    private AiohDB db;
    private AiohDBTable dbTable;
    private ArrayList<Float> columnsWidths;
    private int databaseCol = 0, databaseRow = 0;
    private float currentColWidth = 0, maxColWidth;
    private AiohDatabaseEditorState state = AiohDatabaseEditorState.COLUMNS_DISPLAY;
    private DataType currentCellType;

    @Override
    public void onInit() {
        super.onInit();
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        lines.clear();
        lines.add(new StringBuilder());

        db = AiohDBManager.connectToDBByName("aioh_test");
        dbTable = db.getTableByName("test_table");
        currentCellType = dbTable.columnsTypes().getFirst();
        columnsWidths = new ArrayList<>(dbTable.size());
    }

    private boolean isTextEditingState() {
        return state == AiohDatabaseEditorState.CELL_UPDATE || state == AiohDatabaseEditorState.COLUMN_RENAME;
    }

    private ArrayList<StringBuilder> getCurrentCol() {
        return dbTable.columnsCells().get(databaseCol);
    }

    private StringBuilder getCurrentCell() {
        return getCurrentCol().get(databaseRow);
    }

    private void enableTextEditing(CharSequence initialText) {
        lines.clear();
        lines.add(new StringBuilder());
        selectionStartCol = selectionEndCol = selectionStartLine = selectionEndLine = cursorCol = cursorLine = 0;
        for (int i = 0; i < initialText.length(); i++) {
            super.onTextInput(new char[]{initialText.charAt(i)});
        }
        enableTextEditing();
    }

    private void enableTextEditing() {
        renderer.getFont().setFontSpacing(0);
        this.fontSpacing = 0;
        this.fontHeight = renderer.getFont().getFontHeight();
    }

    private void disableTextEditing() {
        renderer.getFont().setFontSpacing((int) CELL_V_PADDING);
        this.fontSpacing = CELL_V_PADDING;
        this.fontHeight = renderer.getFont().getFontHeight() + fontSpacing;
    }

    @Override
    protected void onStartRendering() {
        columnsWidths.clear();
        maxColWidth = 0;
        // Compare first line lengths of each cell until certain threshold
        // FIXME: Maybe this is slow?
        for (var columnCells : dbTable.columnsCells()) {
            var len = columnCells.stream()
                    .max(Comparator.comparingInt(a -> {
                        var idx = a.indexOf("\n");
                        return (idx == -1) ? a.length() : idx;
                    }))
                    .orElse(new StringBuilder())
                    .length();

            len = Math.min(CELL_CHARS_THRESHOLD, len);
            var columnWidth = 0.5f * len * FONT_SIZE + 2 * CELL_H_PADDING;
            columnsWidths.add(columnWidth);
            if (columnWidth > maxColWidth)
                maxColWidth = columnWidth;
        }
    }

    @Override
    protected void updateCameraPos() {

        if (isTextEditingState()) {
            super.updateCameraPos();
            return;
        }

        var centerX = 0.0f;

        for (int i = 0; i < dbTable.size(); i++) {
            var columnWidth = columnsWidths.get(i);

            if (i < databaseCol)
                centerX += columnWidth + CELL_SPACING;
            else if (i == databaseCol) {
                centerX += 0.5f * columnWidth;
                break;
            }
        }
        cursorPos.setX(centerX);
        cursorPos.setY(
                -databaseRow * fontHeight
        );

        cameraCursorDiff = cursorPos.minus(cameraPos);

        cameraPos = cameraPos.plus(
                cameraCursorDiff.times((float) CAMERA_VELOCITY / FPS)
        );
    }

    @Override
    protected float getCameraScaleVelocity() {

        if (isTextEditingState())
            return super.getCameraScaleVelocity();

        var normalizedWidthRatio = currentColWidth / maxColWidth;

        var targetCameraScale = Math.max(1f - normalizedWidthRatio, 0.45f);

        return (targetCameraScale - cameraScale) / FPS;
    }


    @Override
    public void onDrawColorProgram() {
        if (isTextEditingState()) {
            super.onDrawColorProgram();
            return;
        }
        drawColumnsBackgrounds();
        drawBorderAroundCurrentCell();
    }

    @Override
    protected void onDrawMainProgram() {
        if (isTextEditingState()) {
            super.onDrawMainProgram();
            return;
        }
        drawColumnsTexts();
        drawStatusBar();
    }

    @Override
    protected void onFinishRendering() {
    }

    private void drawColumnsBackgrounds() {

        var start = -cameraPos.getX();

        for (int i = 0; i < dbTable.size(); i++) {
            var columnCells = dbTable.columnsCells().get(i);
            var end = start + columnsWidths.get(i);

            for (int j = 0; j < columnCells.size(); j++) {
                var color = (j % 2 == 0) ? CELL_COLOR1 : CELL_COLOR2;
                var bottom = -cameraPos.getY() - (j + 0.5f) * fontHeight;
                // Background
                renderer.drawSolidRect(
                        start,
                        bottom,
                        end,
                        bottom + fontHeight,
                        color
                );
            }

            start = end + CELL_SPACING;

        }
    }

    private void drawBorderAroundCurrentCell() {

        var targetColWidth = columnsWidths.get(databaseCol);
        var diff = targetColWidth - currentColWidth;
        currentColWidth += diff * (float) CAMERA_VELOCITY / FPS;
        var start = -currentColWidth / 2;
        var end = -start;

        var bottom = -0.5f * fontHeight;
        var top = 0.5f * fontHeight;

        // Start border
        renderer.drawSolidRect(
                start - CELL_SPACING,
                bottom,
                start,
                top,
                AIOH_COLOR
        );

        // End border
        renderer.drawSolidRect(
                end,
                bottom,
                end + CELL_SPACING,
                top,
                AIOH_COLOR
        );

        // Top border
        renderer.drawSolidRect(
                start,
                top - CELL_SPACING,
                end,
                top,
                AIOH_COLOR
        );

        // Bottom border
        renderer.drawSolidRect(
                start,
                bottom,
                end,
                bottom + CELL_SPACING,
                AIOH_COLOR
        );
    }

    private void drawColumnsTexts() {
        var columnStart = 0.0f;

        for (int i = 0; i < dbTable.size(); i++) {
            var columnWidth = columnsWidths.get(i);
            drawColumnText(i, columnStart + columnWidth / 2);
            columnStart += columnWidth + CELL_SPACING;
        }
    }

    private void drawColumnText(int columnIdx, float columnCenter) {
        // TODO: Optimize and render only visible cells
        var columnCells = dbTable.columnsCells().get(columnIdx);
        var text = new StringBuilder(columnCells.size());
        for (int i = 0; i < columnCells.size(); i++) {
            var cell = columnCells.get(i);

            for (int j = 0; j < cell.length() && j < CELL_CHARS_THRESHOLD; j++) {
                var ch = cell.charAt(j);
                if (ch == '\n') {
                    for (int k = j - 1; k >= CELL_CHARS_THRESHOLD - 3; k--) {
                        text.deleteCharAt(k);
                    }
                    text.append("...");
                    break;
                }
                text.append(ch);
            }

            renderer.getFont().drawText(
                    renderer,
                    text,
                    -cameraPos.getX() + columnCenter - text.length() * FONT_SIZE / 4f,
                    -cameraPos.getY() - i * fontHeight - 0.5f * fontHeight,
                    WHITE_COLOR
            );

            text.setLength(0);
        }

    }

    private void drawStatusBar() {
        renderer.end();

        AiohRenderer.colorProgram.use();
        AiohRenderer.colorProgram.setUniform("cameraScale", 1.0f);

        renderer.begin();
        renderer.drawSolidRect(
                -AiohWindow.width / 2f,
                -AiohWindow.height / 2f,
                AiohWindow.width / 2f,
                -AiohWindow.height / 2f + renderer.getDebugFont().getFontHeight(),
                AIOH_COLOR_DARK
        );
        renderer.end();

        AiohRenderer.mainProgram.use();
        AiohRenderer.mainProgram.setUniform("cameraScale", 1.0f);

        renderer.begin();
        renderer.getDebugFont().drawText(
                renderer,
                "Row No.: " + (databaseRow + 1) +
                        ", Column: \"" +
                        dbTable.columnsNames().get(databaseCol) +
                        "\" (" + dbTable.columnsTypes().get(databaseCol) + ")",
                -AiohWindow.width / 2f + 10,
                -AiohWindow.height / 2f,
                WHITE_COLOR
        );
    }

    @Override
    public void onTextInput(char[] newChars) {
        if (!isTextEditingState())
            return;

        var ch = newChars[0];
        switch (currentCellType) {
            case INT -> {
                if (ch == '-' && cursorCol > 0 || ch != '-' && !Character.isDigit(ch))
                    return;
            }
            case FLOAT -> {
                if (ch == '.' && lines.getFirst().indexOf(".") != -1 || ch != '.' && !Character.isDigit(ch))
                    return;
            }
            case VARCHAR -> {
            }
            default -> {
                return;
            }
        }

        super.onTextInput(newChars);
    }

    @Override
    public void onKeyPressed(int keyCode) {
        if (isTextEditingState()) {
            if (keyCode == GLFW_KEY_ESCAPE)
                disableTextEditing();
            else
                super.onKeyPressed(keyCode);
            return;
        }
        switch (keyCode) {
            case GLFW_KEY_BACKSPACE -> onBackspacePressed();
            case GLFW_KEY_DELETE -> onDeletePressed();
            case GLFW_KEY_ENTER -> onEnterPressed();
            case GLFW_KEY_UP -> onUpArrowPressed();
            case GLFW_KEY_DOWN -> onDownArrowPressed();
            case GLFW_KEY_RIGHT -> onRightArrowPressed();
            case GLFW_KEY_LEFT -> onLeftArrowPressed();
        }
    }

    private void onBackspacePressed() {
        if (dbTable.columnsTypes().get(databaseCol) == DataType.BOOL)
            return;
        getCurrentCell().setLength(0);
    }

    private void onDeletePressed() {
        if (dbTable.columnsCells().getFirst().size() == 1)
            return;

        for (var columnCells : dbTable.columnsCells()) {
            columnCells.remove(databaseRow);
        }

        if (databaseRow >= dbTable.columnsCells().getFirst().size())
            databaseRow = dbTable.columnsCells().getFirst().size() - 1;
    }

    private void onEnterPressed() {
        currentCellType = dbTable.columnsTypes().get(databaseCol);
        var cell = getCurrentCell();

        if (currentCellType == DataType.BOOL) {
            dbTable.columnsCells().get(databaseCol).set(
                    databaseRow,
                    (cell == DataType.TRUE_STRING) ? DataType.FALSE_STRING : DataType.TRUE_STRING
            );
            return;
        }
        state = AiohDatabaseEditorState.CELL_UPDATE;
        enableTextEditing(cell);
    }

    private void onUpArrowPressed() {
        if (databaseRow > 0)
            databaseRow--;
    }

    private void onDownArrowPressed() {
        if (databaseRow < getCurrentCol().size() - 1)
            databaseRow++;
    }

    private void onRightArrowPressed() {
        if (databaseCol < dbTable.size() - 1)
            databaseCol++;
    }

    private void onLeftArrowPressed() {
        if (databaseCol > 0)
            databaseCol--;
    }

    @Override
    public void onModKeysPressed(int mods, int keyCode) {
        if (isTextEditingState()) {
            if ((mods & GLFW_MOD_CONTROL) != 0) {
                switch (keyCode) {
                    case GLFW_KEY_S -> saveInputText();
                }
                return;
            }
            super.onModKeysPressed(mods, keyCode);
            return;
        }

        if ((mods & GLFW_MOD_CONTROL) != 0) {
            switch (keyCode) {
                case GLFW_KEY_UP -> addRowAbove();
                case GLFW_KEY_DOWN -> addRowBelow();
                case GLFW_KEY_BACKSPACE -> onDeletePressed();
                case GLFW_KEY_R -> renameCurrentColumn();
                case GLFW_KEY_D -> duplicateCurrentRow();
            }
        }
    }

    private void saveInputText() {
        disableTextEditing();
        var cell = new StringBuilder(lines.size());
        switch (currentCellType) {
            case INT -> {
                var numStr = lines.getFirst().toString();
                if (numStr.isEmpty())
                    cell.append(0);
                else
                    cell.append(Integer.parseInt(numStr));
            }
            case FLOAT -> {
                var numStr = lines.getFirst().toString();
                if (numStr.isEmpty())
                    cell.append(0.0f);
                else
                    cell.append(Float.parseFloat(numStr));
            }
            case VARCHAR -> {
                // FIXME: This might be very slow?
                for (int i = 0; i < lines.size() - 1; i++) {
                    cell.append(lines.get(i));
                    cell.append('\n');
                }
                cell.append(lines.getLast());
            }
            case BOOL -> {
                return;
            }
        }

        if (state == AiohDatabaseEditorState.COLUMN_RENAME) {
            dbTable.columnsNames().set(databaseCol, cell.toString());
        } else {
            dbTable.columnsCells().get(databaseCol).set(databaseRow, cell);
        }
        state = AiohDatabaseEditorState.COLUMNS_DISPLAY;
    }

    private void renameCurrentColumn() {
        enableTextEditing(dbTable.columnsNames().get(databaseCol));
        state = AiohDatabaseEditorState.COLUMN_RENAME;
        currentCellType = DataType.VARCHAR;
    }

    private void addRowAbove() {
        for (int i = 0; i < dbTable.size(); i++) {
            var columnCells = dbTable.columnsCells().get(i);
            columnCells.add(databaseRow, dbTable.columnsTypes().get(i).getDefaultCellValue());
        }
    }

    private void addRowBelow() {
        for (int i = 0; i < dbTable.size(); i++) {
            var columnCells = dbTable.columnsCells().get(i);
            columnCells.add(databaseRow + 1, dbTable.columnsTypes().get(i).getDefaultCellValue());
        }
        databaseRow++;
    }

    private void duplicateCurrentRow() {
        for (var columnCells : dbTable.columnsCells()) {
            columnCells.add(databaseRow + 1, new StringBuilder(columnCells.get(databaseRow)));
        }
        databaseRow++;
    }

}