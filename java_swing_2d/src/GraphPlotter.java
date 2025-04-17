
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphPlotter extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        int margin = 50;

        g2d.setColor(Color.BLACK);
        g2d.drawLine(margin, height / 2, width - margin, height / 2); // X-axis
        g2d.drawLine(width / 2, margin, width / 2, height - margin); // Y-axis
        g2d.drawString("X", width - margin + 10, height / 2);
        g2d.drawString("Y", width / 2, margin - 10);

        g2d.setColor(Color.RED);
        int prevX = margin;
        int prevY = height / 2;

        for (int x = margin; x <= width - margin; x++) {
            double xVal = (x - width / 2) / 50.0;
            double yVal = Math.sin(xVal);
            int y = (int) (height / 2 - yVal * 50);
            if (x > margin) {
                g2d.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GraphPlotter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GraphPlotter());
        frame.setSize(600, 400);
        frame.setLocationRelativeTo((Component) null);
        frame.setVisible(true);
    }
}
