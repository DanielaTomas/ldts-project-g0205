import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.AWTTerminalFrame;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Game {
    private Screen screen;
    private int fontSize;
    private Field field;
    private Menu menu;
    private TextGraphics graphics;

    Game() throws IOException, URISyntaxException, FontFormatException {
        fontSize = 25;
        field = new Field();
        Font font = loadFont();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        DefaultTerminalFactory factory = new DefaultTerminalFactory();
        Font loadedFont = font.deriveFont(Font.PLAIN, fontSize);
        AWTTerminalFontConfiguration fontConfig = AWTTerminalFontConfiguration.newInstance(loadedFont);
        factory.setTerminalEmulatorFontConfiguration(fontConfig);
        factory.setForceAWTOverSwing(true);
        factory.setInitialTerminalSize(new TerminalSize(60, 30));
        Terminal terminal = factory.createTerminal();
        ((AWTTerminalFrame)terminal).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });
        initializeScreen(terminal);
        graphics = screen.newTextGraphics();
        screen.clear();
        menu = new Menu(graphics);
        screen.refresh();
    }

    private Font loadFont() throws IOException, FontFormatException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("square.ttf");
        return Font.createFont(Font.TRUETYPE_FONT, new File(resource.toURI()));
    }

    private void initializeScreen(Terminal terminal) throws IOException {
        screen = new TerminalScreen(terminal);
        screen.setCursorPosition(null);   // we don't need a cursor
        screen.startScreen();             // screens must be started
        screen.doResizeIfNecessary();     // resize screen if necessary
        screen.refresh();
    }

    public void run() throws IOException {
        while (true) {
            KeyStroke key = screen.readInput();
            if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'q') screen.close();
            if (key.getKeyType() == KeyType.EOF) break;
            System.out.println(key);
            processKey(key);
            screen.refresh();
        }
    }

    private void processKey(KeyStroke key) throws IOException {
        if (menu.isSelected()) menu.processKey(key, graphics);
        if (!menu.isSelected()){
            processKeyMenu(key);
        }

    }

    private void processKeyMenu(KeyStroke key) throws IOException {
        if (menu.getSelected() == 1) {
            screen.clear();
            field.processKey(key, graphics);
            screen.refresh();
        }
        else if (menu.getSelected() == 2) {
            screen.close();
        }

    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
