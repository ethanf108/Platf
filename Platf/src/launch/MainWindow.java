package launch;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class MainWindow extends JFrame implements KeyEventDispatcher {

    private static final long serialVersionUID = 1L;
    private final int ScreenY;
    private final int ScreenX;
    private final DrawClass GraphicsPanel;
    private final Level LevelOne;
    private final Level LevelTwo;
    private final World world;
    private final GameCharacter MainCharacter;

    public void handleExceptionPopup(Throwable e) {
        dispose();
        Popup errorPopup = new Popup(e);
    }

    public void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        world.init();
        world.Characters.add(MainCharacter);
        getContentPane().add(GraphicsPanel);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        setVisible(true);
        GraphicsPanel.start();
        world.start();
        pack();
    }

    public MainWindow() {
        super("");
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        ScreenX = Toolkit.getDefaultToolkit().getScreenSize().width;
        ScreenY = Toolkit.getDefaultToolkit().getScreenSize().height;
        this.world = new World(ScreenX, ScreenY) {
            {
                init();
            }
        };
        this.LevelTwo = new Level(ScreenX, ScreenY) {
            {
                add(new Platform(new Rectangle(800, 0, 100, 1000), "w"));
            }
        };
        this.LevelOne = new Level(ScreenX * 2, ScreenY*2) {
            {
                add(new Platform(new Rectangle(200, ScreenY*2 - 150, 100, 20), "w"));
                add(new Platform(new Rectangle(500, ScreenY*2 - 250, 100, 20), "w"));
                add(new Platform(new Rectangle(800, ScreenY*2 - 350, 100, 20), "w"));
                add(new Platform(new Rectangle(1100, ScreenY*2 - 450, 100, 20), "w"));
              //  add(new Platform(new Rectangle(0, ScreenY - 450, (ScreenX*2)-10, 200), "w"));
            }
        };
        world.Levels.add(LevelOne);
        world.Levels.add(LevelTwo);
        this.MainCharacter = new GameCharacter(new Rectangle(0, ScreenY*2 - 120, 40, 80), world) {
            {
                start();
            }
        };
        GraphicsPanel = new DrawClass(ScreenX, ScreenY, world);
        SwingUtilities.invokeLater(() -> {
            init();
        });
        Thread.UncaughtExceptionHandler h = (Thread th, Throwable e) -> {
            handleExceptionPopup(e);
        };
        Thread.setDefaultUncaughtExceptionHandler(h);
    }

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyChar() == 'u') {
            world.Level = (world.Level += 1) % 2;
            MainCharacter.x = 0;
            MainCharacter.y = ScreenY - 120;
            MainCharacter.gx = 0;
            MainCharacter.gy = ScreenY - 120;
            MainCharacter.gmx = 0;
            MainCharacter.gmy = 0;
        }
        return false;
    }

}
