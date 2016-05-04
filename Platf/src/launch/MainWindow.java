package launch;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    GameRenderer GameRenderPanel = null;

    public void handleExceptionPopup(Throwable e) {
        dispose();
        Popup errorPopup = new Popup(e);
    }

    public void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        GameRenderPanel = new GameRenderer();
        GameRenderPanel.init();
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
