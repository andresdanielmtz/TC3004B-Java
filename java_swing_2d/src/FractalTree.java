import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FractalTree extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int startX = getWidth() / 2;
        int startY = getHeight() - 50;
        drawTree(g2d, startX, startY, -90, 100);
    }

    private void drawTree(Graphics2D g, int x1, int y1, double angle, double length) {
        if (length < 5)
            return;
        int x2 = x1 + (int) (Math.cos(Math.toRadians(angle)) * length);
        int y2 = y1 + (int) (Math.sin(Math.toRadians(angle)) * length);
        // Vary color based on branch length
        int colorValue = (int) (length * 2);
        colorValue = Math.min(255, Math.max(0, colorValue));
        g.setColor(new Color(0, colorValue, 0));
        // Vary thickness based on branch length
        float thickness = (float) (length / 10);
        g.setStroke(new BasicStroke(thickness));
        g.drawLine(x1, y1, x2, y2);
        // Recursively draw two branches
        drawTree(g, x2, y2, angle - 20, length * 0.75);
        drawTree(g, x2, y2, angle + 20, length * 0.75);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("FractalTree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new FractalTree());
        frame.setSize(800, 600);
        frame.setLocationRelativeTo((Component) null);
        frame.setVisible(true);
    }
}
