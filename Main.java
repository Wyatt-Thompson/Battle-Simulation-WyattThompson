import java.awt.Dimension;

import javax.swing.JFrame;

public class Main
{
    public static void main(String[] args) {
        JFrame frame = new JFrame("Battle Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);
        frame.setSize(new Dimension(1920, 1080));

        ContainerPanel panel = new ContainerPanel();
        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }
}