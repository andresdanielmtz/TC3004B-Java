import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class BasicShapes extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        this.setBackground(Color.BLACK);
        g2d.setColor(Color.RED);
        g2d.fillRect(50, 50, 100, 80);

        g2d.setColor(Color.BLUE);
        g2d.fillOval(200, 50, 100, 80);

        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(3.0F));
        g2d.drawLine(50, 200, 300, 200);

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Basic Shapes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new BasicShapes());
        frame.setSize(500, 300);
        frame.setLocationRelativeTo((Component) null);
        frame.setVisible(true);
    }
}
