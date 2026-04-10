import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

public class TitlePanel extends JPanel
{
    private final int SCREEN_WIDTH = 1920;
    private final int SCREEN_HEIGHT = 1080;

    ContainerPanel containerPanel;

    public TitlePanel(ContainerPanel containerPanel)
    {
        this.containerPanel = containerPanel;

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(20, 20, 20));
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        this.add(new MenuButton(containerPanel, "Play", new Color(67, 217, 50), 28), gbc);
        this.add(new MenuButton(containerPanel, "Exit", new Color(214, 50, 50), 28), gbc);
    }
}
