import java.util.LinkedList;

public class TerminalBuffer {
    // Cursor
    private int cursorPositionX = 0;
    private int cursorPositionY = 0;
    // Attributes basics
    private int width;
    private int height;
    private int maxScrollback;
    private TerminalColor foregroundColor = TerminalColor.DEFAULT;
    private TerminalColor backgroundColor =  TerminalColor.DEFAULT;
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


}
