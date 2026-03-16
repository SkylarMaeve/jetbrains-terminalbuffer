public class Cell {
    //Basics
    public int character = ' ';
    public TerminalColor foregroundColor = TerminalColor.DEFAULT;
    public TerminalColor backgroundColor =  TerminalColor.DEFAULT;
    public int width = 1;
    //Style flags
    public boolean styleFlagBold = false;
    public boolean styleFlagItalic = false;
    public boolean styleFlagUnderline = false;
}
