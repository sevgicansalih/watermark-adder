import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.jdatepicker.JDatePicker;

public class MySwingApp extends JFrame {
    private JTextField nameInput;
    private JRadioButton firstRB;
    private JRadioButton secondRB;
    private JLabel betweenPagesLabel;
    private JTextField firstPageInput;
    private JTextField secondPageInput;
    private JRadioButton firstGroupRB;
    private JRadioButton secondGroupRB;
    private JDatePicker datePicker;
    private JTextField groupNameInput;
    private JCheckBox encryptionCheckBox;

    public MySwingApp() {
        // Add first panel with radio buttons and text input
        firstRB = new JRadioButton("FirstRB");
        secondRB = new JRadioButton("SecondRB");
        nameInput = new JTextField(20);
        nameInput.setMaximumSize(nameInput.getPreferredSize());
        nameInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstRB.addActionListener(e -> nameInput.setVisible(firstRB.isSelected()));
        secondRB.addActionListener(e -> nameInput.setVisible(secondRB.isSelected()));
        ButtonGroup firstGroup = new ButtonGroup();
        firstGroup.add(firstRB);
        firstGroup.add(secondRB);
        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.PAGE_AXIS));
        firstPanel.add(firstRB);
        firstPanel.add(secondRB);
        firstPanel.add(Box.createVerticalStrut(10));
        firstPanel.add(nameInput);
        firstPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add second panel with labels and text inputs
        betweenPagesLabel = new JLabel("Between pages: ");
        firstPageInput = new JTextField(5);
        secondPageInput = new JTextField(5);
        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        secondPanel.add(betweenPagesLabel);
        secondPanel.add(firstPageInput);
        secondPanel.add(new JLabel("and"));
        secondPanel.add(secondPageInput);
        secondPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add third panel with labels and radio buttons
        firstGroupRB = new JRadioButton("FirstGroupRB");
        secondGroupRB = new JRadioButton("SecondGroupRB");
        ButtonGroup group = new ButtonGroup();
        group.add(firstGroupRB);
        group.add(secondGroupRB);
        JPanel thirdPanel = new JPanel();
        thirdPanel.setLayout(new BoxLayout(thirdPanel, BoxLayout.PAGE_AXIS));
        thirdPanel.add(new JLabel("Which Group?"));
        thirdPanel.add(firstGroupRB);
        thirdPanel.add(secondGroupRB);
        thirdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add fourth panel with label and date picker
//        datePicker = new JDatePicker();
        JPanel fourthPanel = new JPanel();
        fourthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fourthPanel.add(new JLabel("Date of event: "));
//        fourthPanel.add((Component) datePicker);
        fourthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add fifth panel with text input
        groupNameInput = new JTextField(20);
        groupNameInput.setMaximumSize(groupNameInput.getPreferredSize());
        JPanel fifthPanel = new JPanel();
        fifthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        fifthPanel.add(new JLabel("Group name: "));
        fifthPanel.add(groupNameInput);
        fifthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add sixth panel with checkbox for encryption
        encryptionCheckBox = new JCheckBox("Is encryption required?");
        JPanel sixthPanel = new JPanel();
        sixthPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sixthPanel.add(encryptionCheckBox);
        sixthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add
        // all panels to main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.add(firstPanel);
        mainPanel.add(secondPanel);
        mainPanel.add(thirdPanel);
        mainPanel.add(fourthPanel);
        mainPanel.add(fifthPanel);
        mainPanel.add(sixthPanel);

        // Add main panel to JFrame and set properties
        this.add(mainPanel);
        this.pack();
        this.setTitle("My Swing App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}