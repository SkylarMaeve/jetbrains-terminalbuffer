import java.util.LinkedList;

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

    private LinkedList<Cell[]> screen;
    private LinkedList<Cell[]> scrollBack;

    public TerminalBuffer(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;
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

}
