package launch;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class MainWindow extends JFrame{

    public MainWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(this);
        GamePanel panel = new GamePanel();
        getContentPane().add(panel);
        setVisible(true);
        panel.start();
    }

    public static void main(String[] args) {
        new MainWindow();
    }

}
