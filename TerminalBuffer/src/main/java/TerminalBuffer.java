import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TerminalBuffer {
    // Cursor
    private int cursorColumn = 0;
    private int cursorRow = 0;
    // Setup
    private int width;
    private int height;
    private int maxScrollback;
    private TerminalColor foregroundColor = TerminalColor.DEFAULT;
    private TerminalColor backgroundColor = TerminalColor.DEFAULT;
    // Style flags
    private boolean styleFlagBold = false;
    private boolean styleFlagItalic = false;
    private boolean styleFlagUnderline = false;

    private Cell[][] screen;
    private LinkedList<Cell[]> scrollBack;

    public TerminalBuffer(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;


        this.screen = new Cell[height][width];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                screen[row][column] = new Cell();
            }
        }
        this.scrollBack = new LinkedList<>();
    }

    // Attributes
    public void setAllAttributes(TerminalColor bg, TerminalColor fg, boolean bold, boolean italic, boolean underline) {
        this.foregroundColor = fg;
        this.backgroundColor = bg;
        this.styleFlagBold = bold;
        this.styleFlagItalic = italic;
        this.styleFlagUnderline = underline;

    }

    public void setForegroundColor(TerminalColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setBackgroundColor(TerminalColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setStyleFlagBold(boolean styleFlagBold) {
        this.styleFlagBold = styleFlagBold;
    }

    public void setStyleFlagUnderline(boolean styleFlagUnderline) {
        this.styleFlagUnderline = styleFlagUnderline;
    }

    public void setStyleFlagItalic(boolean styleFlagItalic) {
        this.styleFlagItalic = styleFlagItalic;
    }

    // Cursor
    public int getCursorColumn() {
        return cursorColumn;
    }

    public int getCursorRow() {
        return cursorRow;
    }

    public void setCursorPosition(int column, int row) {
        cursorColumn = Math.max(0, Math.min(column, width - 1));
        cursorRow = Math.max(0, Math.min(row, height - 1));
    }

    public void moveCursorRight(int numberOfColumns) {
        setCursorPosition(cursorColumn + numberOfColumns, cursorRow);
    }

    public void moveCursorLeft(int numberOfColumns) {
        setCursorPosition(cursorColumn - numberOfColumns, cursorRow);
    }

    public void moveCursorUp(int numberOfColumns) {
        setCursorPosition(cursorColumn, cursorRow - numberOfColumns);
    }

    public void moveCursorDown(int numberOfColumns) {
        setCursorPosition(cursorColumn, cursorRow + numberOfColumns);
    }

    // Editing
    public void writeText(String text) {
        text.chars().forEach(this::writeCharacter);
    }

    public void insertText(String text) {

        List<Cell> tailingCells = new ArrayList<>();
        for (int row = cursorRow; row < height; row++) {
            int startColumn = (row == cursorRow) ? cursorColumn : 0;
            for (int column = startColumn; column < width; column++) {
                Cell original = screen[row][column];
                if (!original.active) continue;
                Cell copy = new Cell();

                copy.character = original.character;
                copy.foregroundColor = original.foregroundColor;
                copy.backgroundColor = original.backgroundColor;
                copy.styleFlagBold = original.styleFlagBold;
                copy.styleFlagItalic = original.styleFlagItalic;
                copy.styleFlagUnderline = original.styleFlagUnderline;

                tailingCells.add(copy);

            }
        }

        int tempScrollbackSize = scrollBack.size();
        text.chars().forEach(this::writeCharacter);

        int targetRow = cursorRow;
        int targetColumn = cursorColumn;

        // Saving current attributes before putting cells back
        TerminalColor tempFg = foregroundColor;
        TerminalColor tempBg = backgroundColor;
        boolean tempStyleFlagBold = styleFlagBold;
        boolean tempStyleFlagItalic = styleFlagItalic;
        boolean tempStyleFlagUnderline = styleFlagUnderline;
        for (Cell cell : tailingCells) {
            this.foregroundColor = cell.foregroundColor;
            this.backgroundColor = cell.backgroundColor;
            this.styleFlagBold = cell.styleFlagBold;
            this.styleFlagItalic = cell.styleFlagItalic;
            this.styleFlagUnderline = cell.styleFlagUnderline;
            writeCharacter(cell.character);
        }

        this.foregroundColor = tempFg;
        this.backgroundColor = tempBg;
        this.styleFlagBold = tempStyleFlagBold;
        this.styleFlagItalic = tempStyleFlagItalic;
        this.styleFlagUnderline = tempStyleFlagUnderline;

        // Setting cursor
        int scrolls = scrollBack.size() - tempScrollbackSize;
        int finalRow = Math.max(0, targetRow - scrolls);
        setCursorPosition(targetColumn, finalRow);
    }

    public void fillLine(char character) {
        for (int column = 0; column < width; column++) {
            Cell c = screen[cursorRow][column];
            c.character = character;
            c.foregroundColor = foregroundColor;
            c.backgroundColor = backgroundColor;
            c.styleFlagBold = styleFlagBold;
            c.styleFlagItalic = styleFlagItalic;
            c.styleFlagUnderline = styleFlagUnderline;
        }
    }

    public void insertEmptyLineAtBottom() {
        pushToScrollback();
        setCursorPosition(0, height - 1);
    }

    public void clearScreen() {
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                screen[row][column] = new Cell();
            }
        }
        setCursorPosition(0, 0);
    }

    public void clearAll() {
        clearScreen();
        scrollBack.clear();
    }

    // Content Access
    public Cell getCharacterFromScreen(int row, int column) {
        if (row < 0 || column < 0 || row >= height || column >= width) throw new IndexOutOfBoundsException();
        return screen[row][column]; // Returning whole cells, char + attributes
    }

    public Cell getCharacterFromScrollback(int row, int column) {
        if (row < 0 || column < 0 || row >= scrollBack.size() || column >= width) throw new IndexOutOfBoundsException();
        return scrollBack.get(row)[column]; // Returning whole cells, char + attributes
    }

    public String getLineAsStringFromScreen(int row) {
        if (row < 0 || row >= height) throw new IndexOutOfBoundsException();
        StringBuilder sb = new StringBuilder();
        for (Cell cell : screen[row]) {
            sb.appendCodePoint(cell.character);
        }
        return sb.toString().stripTrailing();
    }

    public String getLineAsStringFromScrollback(int row) {
        if (row < 0 || row >= scrollBack.size()) throw new IndexOutOfBoundsException();
        StringBuilder sb = new StringBuilder();
        for (Cell cell : scrollBack.get(row)) {
            sb.appendCodePoint(cell.character);
        }
        return sb.toString().stripTrailing();
    }

    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < height; row++) {
            sb.append(getLineAsStringFromScreen(row)).append("\n");
        }
        return sb.toString();
    }

    public String getEntireContentAsString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < scrollBack.size(); row++) {
            sb.append(getLineAsStringFromScrollback(row)).append("\n");
        }
        sb.append(getScreenAsString());
        return sb.toString();
    }

    // Providers
    private void writeCharacter(int character) {
        // EOL
        if (character == '\n') {
            cursorColumn = 0;
            cursorRow++;
            if (cursorRow >= height) {
                pushToScrollback();
            }
            return;
        }

        Cell c = screen[cursorRow][cursorColumn];
        c.character = character;
        c.foregroundColor = foregroundColor;
        c.backgroundColor = backgroundColor;
        c.styleFlagBold = styleFlagBold;
        c.styleFlagItalic = styleFlagItalic;
        c.styleFlagUnderline = styleFlagUnderline;
        c.active = true;

        cursorColumn++;
        //Due to this, actual screen is N-1 lines, last one is empty ready to be written into
        if (cursorColumn >= width) {
            cursorColumn = 0;
            cursorRow++;

            if (cursorRow >= height) {
                pushToScrollback();
            }
        }
    }

    private void pushToScrollback() {
        if (maxScrollback > 0) {
            scrollBack.addLast(screen[0]);
            if (scrollBack.size() > maxScrollback) {
                scrollBack.removeFirst();
            }
        }

        for (int i = 1; i < height; i++) {
            screen[i - 1] = screen[i];
        }

        screen[height - 1] = new Cell[width];
        for (int i = 0; i < width; i++) {
            screen[height - 1][i] = new Cell();
        }

        cursorRow = height - 1;
    }
    // Bonus
    // Resizing would need dynamic structures instead of fixed ones.
    // Wide characters would utilize width of 2, with cell next to it as width 0.
}
