package launch;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

public class MainWindow extends JFrame{

    public MainWindow() {
        super("");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        test panel = new test();
        getContentPane().add(panel);//setContentPane(panel);
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
        setVisible(true);
        pack();
       panel.start();
    }

    public static void main(String[] args) {
        new MainWindow();
    }

}
