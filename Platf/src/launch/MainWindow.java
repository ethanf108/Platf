package launch;

import com.sun.glass.events.KeyEvent;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    World GameRenderPanel = null;

    public void handleExceptionPopup(Throwable e) {
        dispose();
        Popup errorPopup = new Popup(e);
    }

    public static Rectangle RWP(int x, int y, int xs, int ys) {
        Rectangle tmp = new Rectangle(x, y, xs, ys);
        tmp.grow(-2, -2);
        return tmp;
    }

    public void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        GameRenderPanel = new World();
        GameRenderPanel.init();
        GameRenderPanel.Levels = new ArrayList<ArrayList<RectWithProps>>() {
            private static final long serialVersionUID = 1L;

            {
                add(
                        new ArrayList<RectWithProps>() {
                    private static final long serialVersionUID = 1L;

                    {
                        add(new RectWithProps(RWP(200, GameRenderPanel.ScreenY - 150, 100, 20), "w"));//rand platf
                        add(new RectWithProps(RWP(500, GameRenderPanel.ScreenY - 250, 100, 20), "w"));//rand platf
                        add(new RectWithProps(RWP(800, GameRenderPanel.ScreenY - 350, 100, 20), "w"));//rand platf
                        add(new RectWithProps(RWP(1100, GameRenderPanel.ScreenY - 450, 100, 20), "w"));//rand platf
                    }
                }
                );
                add(
                        new ArrayList<RectWithProps>() {
                    private static final long serialVersionUID = 1L;

                    {
                        add(new RectWithProps(RWP(800, 0, 100, 1000), "w"));//rand platf WallDemo
                    }
                });
            }
        };
        GameRenderPanel.Characters.add(new GameCharacter(0, 0, 40, 80, GameRenderPanel){{
            start();
        }});
        GameRenderPanel.Characters.add(new GameCharacter(0, 0, 40, 80, GameRenderPanel){{
            start();
            keyL=KeyEvent.VK_A;
            keyS=KeyEvent.VK_SPACE;
            keyR=KeyEvent.VK_D;
        }});
        getContentPane().add(GameRenderPanel);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        setVisible(true);
        pack();
        Thread.UncaughtExceptionHandler h = (Thread th, Throwable e) -> {
            handleExceptionPopup(e);
        };
        Thread.setDefaultUncaughtExceptionHandler(h);
    }

    public MainWindow() {
        super("");
        SwingUtilities.invokeLater(() -> {
            init();
            GameRenderPanel.start();
        });
    }

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
    }

}
