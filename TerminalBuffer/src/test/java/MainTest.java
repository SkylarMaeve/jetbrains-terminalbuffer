import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {

    private TerminalBuffer terminalBuffer;

    @BeforeEach
    void setup() {
        terminalBuffer = new TerminalBuffer(5, 5, 5);
        terminalBuffer.setAllAttributes(TerminalColor.GREEN, TerminalColor.BRIGHT_BLUE, false, false, false);
    }

    @Test
    void testInitialCursorPosition() {
        assertEquals(0, terminalBuffer.getCursorColumn());
        assertEquals(0, terminalBuffer.getCursorRow());
    }

    @Test
    void testCursorPositionWithinBounds() {
        terminalBuffer.moveCursorDown(4);
        terminalBuffer.moveCursorRight(4);
        assertEquals(4, terminalBuffer.getCursorColumn());
        assertEquals(4, terminalBuffer.getCursorRow());

        terminalBuffer.moveCursorUp(4);
        terminalBuffer.moveCursorLeft(4);
        assertEquals(0, terminalBuffer.getCursorColumn());
        assertEquals(0, terminalBuffer.getCursorRow());


    }

    @Test
    void testCursorPositionOutOfBounds() {
        terminalBuffer.moveCursorRight(10);
        terminalBuffer.moveCursorDown(10);
        assertEquals(4, terminalBuffer.getCursorColumn());
        assertEquals(4, terminalBuffer.getCursorRow());
    }

    @Test
    void testSetCursorPositionWithinOfBounds() {
        terminalBuffer.setCursorPosition(2, 2);
        assertEquals(2, terminalBuffer.getCursorColumn());
        assertEquals(2, terminalBuffer.getCursorRow());
    }

    @Test
    void testSetCursorPositionOutOfBounds() {
        terminalBuffer.setCursorPosition(10, 10);
        assertEquals(4, terminalBuffer.getCursorColumn());
        assertEquals(4, terminalBuffer.getCursorRow());
    }

    @Test
    void testWrite() {
        terminalBuffer.writeText("Hell");
        assertEquals(4, terminalBuffer.getCursorColumn());
        assertEquals(0, terminalBuffer.getCursorRow());

        terminalBuffer.writeText("o");
        assertEquals(0, terminalBuffer.getCursorColumn());
        assertEquals(1, terminalBuffer.getCursorRow());

        assertEquals("Hello", terminalBuffer.getLineAsStringFromScreen(0));
    }

    @Test
    void testScrollback() {
        terminalBuffer.writeText("Row 1");
        terminalBuffer.writeText("Row 2");
        terminalBuffer.writeText("Row 3");
        terminalBuffer.writeText("Row 4");
        terminalBuffer.writeText("Row 5");//This should push row 1 out, because the active line is empty

        String screen = terminalBuffer.getScreenAsString();
        assertTrue(screen.contains("Row 2"));
        assertTrue(screen.contains("Row 3"));
        assertTrue(screen.contains("Row 5"));
        assertTrue(screen.contains("Row 5"));

        assertFalse(screen.contains("Row 1"));

        assertEquals("Row 1", terminalBuffer.getLineAsStringFromScrollback(0));
    }

    @Test
    void testInsertPushRight() {
        terminalBuffer.writeText("11");
        terminalBuffer.setCursorPosition(0, 0);

        terminalBuffer.insertText("22");
        assertEquals("2211", terminalBuffer.getLineAsStringFromScreen(0));
        assertEquals(2, terminalBuffer.getCursorColumn());
        assertEquals(0, terminalBuffer.getCursorRow());
    }

    @Test
    void testInsertPushNextLine() {
        terminalBuffer.writeText("111");
        terminalBuffer.setCursorPosition(0, 0);

        terminalBuffer.insertText("22");
        assertEquals("22111", terminalBuffer.getLineAsStringFromScreen(0));
        assertEquals(2, terminalBuffer.getCursorColumn());
        assertEquals(0, terminalBuffer.getCursorRow());
    }

    @Test
    void testClearScreen() {
        terminalBuffer.writeText("Hello");
        terminalBuffer.clearScreen();

        assertEquals(0, terminalBuffer.getCursorColumn());
        assertEquals(0, terminalBuffer.getCursorRow());

        assertEquals("", terminalBuffer.getLineAsStringFromScreen(0));
    }

    @Test
    void testAllClear() {
        terminalBuffer.writeText("Row 1");
        terminalBuffer.writeText("Row 2");
        terminalBuffer.writeText("Row 3");
        terminalBuffer.writeText("Row 4");
        terminalBuffer.writeText("Row 5");
        terminalBuffer.clearAll();

        assertEquals(0, terminalBuffer.getCursorColumn());
        assertEquals(0, terminalBuffer.getCursorRow());

        assertThrows(IndexOutOfBoundsException.class, () -> terminalBuffer.getLineAsStringFromScrollback(0));
    }
}
