import components.HintTextField;

import javax.swing.*;
import java.awt.*;

public class GUI {

    public static JPanel getFirstPanel() {
        JPanel firstPanel = new JPanel();
        firstPanel.setBackground(Color.WHITE);
        firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.Y_AXIS));

        JRadioButton fRb = new JRadioButton("Tek katilimci");
        JRadioButton sRb = new JRadioButton("Coklu katilimci");
        ButtonGroup fBg = new ButtonGroup();
        fBg.add(fRb);
        fBg.add(sRb);

        JTextField textField = new HintTextField("Isim Soyisim yaziniz");
        JFileChooser fileChooser = new JFileChooser();

        firstPanel.add(fRb);
        firstPanel.add(sRb);
        firstPanel.add(textField);
//        firstPanel.add(fileChooser);

        return firstPanel;
    }

}
