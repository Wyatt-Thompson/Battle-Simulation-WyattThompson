import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class MenuButton extends JButton
{
    private ContainerPanel containerPanel;

    public MenuButton(ContainerPanel containerPanel, String text, Color color, int fontSize)
    {
        this.containerPanel = containerPanel;

        this.setPreferredSize(new Dimension(200, 60));
        this.setBackground(color);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setFont(new Font("Comic Sans MS", Font.BOLD, fontSize));
        this.setForeground(Color.WHITE);
        this.setText(text);
        this.addActionListener(new MenuButtonActionListener());
    }

    private class MenuButtonActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            MenuButton button = (MenuButton) e.getSource();

            Window window = SwingUtilities.getWindowAncestor(button);

            switch (button.getText())
            {
                case "Play":
                    containerPanel.switchPanel("Simulation");
                    containerPanel.getSimulationPanel().start();
                    break;
                case "Exit":
                    window.dispose();
            }
        }
    }
}