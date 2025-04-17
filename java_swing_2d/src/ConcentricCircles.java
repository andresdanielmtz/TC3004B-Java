
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ConcentricCircles extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 10; i > 0; i--) {
            int diameter = i * 30;
            int x = centerX - diameter / 2;
            int y = centerY - diameter / 2;

            // Alternate colors
            if (i % 2 == 0) {
                g2d.setColor(new Color(0, 100, 200));
            } else {
                g2d.setColor(new Color(200, 100, 0));
            }
            g2d.fillOval(x, y, diameter, diameter);
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ConcentricCircles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ConcentricCircles());
        frame.setSize(400, 430);
        frame.setLocationRelativeTo((Component) null);
        frame.setVisible(true);
    }

}
