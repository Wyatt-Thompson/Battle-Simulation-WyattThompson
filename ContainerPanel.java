import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

public class ContainerPanel extends JPanel
{
    TitlePanel titlePanel;
    SimulationPanel simulationPanel;

    public CardLayout cardLayout;

    public ContainerPanel()
    {
        this.setPreferredSize(new Dimension(1920, 1080));
        this.setLayout(new CardLayout());

        titlePanel = new TitlePanel(this);
        this.add(titlePanel, "Title");

        simulationPanel = new SimulationPanel(this);
        this.add(simulationPanel, "Simulation");

        cardLayout = (CardLayout) this.getLayout();
    }

    public void switchPanel(String name)
    {
        cardLayout.show(this, name);
    }

    public TitlePanel getTitlePanel()
    {
        return this.titlePanel;
    }

    public SimulationPanel getSimulationPanel()
    {
        return this.simulationPanel;
    }
}
