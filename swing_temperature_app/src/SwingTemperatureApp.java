import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SwingTemperatureApp extends JFrame {
    private JTextField celsiusField;
    private JTextField fahrenheitField;

    public SwingTemperatureApp() {
        setTitle("Swing Temperature Converter");
        setSize(300, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        // Add celsius label and text field
        add(new JLabel("Celsius:"));
        celsiusField = new JTextField();
        add(celsiusField);

        // Add fahrenheit label and text field
        add(new JLabel("Fahrenheit:"));
        fahrenheitField = new JTextField();
        add(fahrenheitField);

        JButton celsiusToFahr = new JButton("Convert to Fahrenheit");
        // Add action listener to the button
        celsiusToFahr.addActionListener(e -> {
            // Open Try Catch
            try {
                // Convert the text from the celsius text field into double variable
                double celsius = Double.parseDouble(celsiusField.getText());
                // Convert celsius double variable into fahrenheit double variable
                double fahrenheit = celsius * 9 / 5 + 32;
                // set the text to the converted fahrenheit variable
                fahrenheitField.setText(String.format("%.2f", fahrenheit));
                // catch the exception if there is a numeric error
                // from the celsius text field
            } catch (NumberFormatException ex) {
                // Show error message in an option pane
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid input",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        // Add the button to the layout
        add(celsiusToFahr);

        // Create button to convert Fahrenheit to Celsius
        JButton fahrToCelsius = new JButton("Convert to Celsius");
        // Add action listener to the button
        fahrToCelsius.addActionListener(e -> {
            try {
                // Convert the text from the fahrenheit text field into double variable
                double fahrenheit = Double.parseDouble(fahrenheitField.getText());
                // Convert fahrenheit double variable into celsius double variable
                double celsius = (fahrenheit - 32) * 5 / 9;
                // set the text to the converted fahrenheit variable
                celsiusField.setText(String.format("%.2f", celsius));
                // catch the exception if there is a numeric error
                // from the fahrenheit text field
            } catch (NumberFormatException ex) {
                // Show error message in an option pane
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid input",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        // Add the button to the layout
        add(fahrToCelsius);
    }

    public static void main(String[] args) {
        // Method to run the main thread of the app
        SwingUtilities.invokeLater(() -> {
            // Create the JFrame object
            SwingTemperatureApp swingTemperatureApp = new SwingTemperatureApp();
            // Set it visible (Open the JFrame)
            swingTemperatureApp.setVisible(true);
        });
    }
}
