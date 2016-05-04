package launch;

import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import static launch.GameRenderer.ScreenY;

public final class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    GameRenderer GameRenderPanel = null;

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
        GameRenderPanel = new GameRenderer();
        GameRenderPanel.init();
        GameRenderPanel.Levels = new ArrayList<ArrayList<RectWithProps>>() {
            private static final long serialVersionUID = 1L;

            {
                add(
                        new ArrayList<RectWithProps>() {
                    private static final long serialVersionUID = 1L;

                    {
                        add(new RectWithProps(RWP(200, ScreenY - 150, 100, 20), "w"));//rand platf
                        add(new RectWithProps(RWP(500, ScreenY - 250, 100, 20), "w"));//rand platf
                        add(new RectWithProps(RWP(800, ScreenY - 350, 100, 20), "w"));//rand platf
                        add(new RectWithProps(RWP(1100, ScreenY - 450, 100, 20), "w"));//rand platf
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
