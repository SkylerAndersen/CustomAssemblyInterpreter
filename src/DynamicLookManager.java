import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class DynamicLookManager extends ComponentAdapter {
    private IDERequirements parent;
    private JFrame frame;
    private JPanel buttonPanel;
    private JPanel editorPanel;
    private JPanel terminalPanel;
    private JPanel stackPanel;
    private JPanel registerPanel;
    private JPanel screenPanel;
    private JPanel column1;
    private JPanel column2;
    private JPanel column3;
    private JPanel registerData;
    private boolean smallMode;
    private ScreenPanel screen;
    private boolean buttonsAvailable;
    private JTextArea codeArea;
    public DynamicLookManager(IDERequirements parent, JFrame frame) {
        this.frame = frame;
        this.parent = parent;
        createComponents();
        smallMode = false;
        placeGraphicalElements();
        styleComponents();
        updateGraphics();
        buttonsAvailable = true;
    }

    public String getCodeText () {
        return codeArea.getText();
    }

    public String getTerminalText () {
        JScrollPane scrollPane = (JScrollPane) terminalPanel.getComponent(0);
        JTextArea textArea = (JTextArea) scrollPane.getViewport().getComponent(0);
        return textArea.getText();
    }

    public String getStackText () {
        JScrollPane scrollPane = (JScrollPane) stackPanel.getComponent(0);
        JTextArea textArea = (JTextArea) scrollPane.getViewport().getComponent(0);
        return textArea.getText();
    }

    public int getRegisterValue (int number) {
        // get data from label
        String data = "";
        if (number < 10)
            data = ((JTextArea) registerData.getComponent(number)).getText().strip();
        else
            data = ((JLabel) registerData.getComponent(number)).getText().strip();

        // detect invalid data
        boolean containsNonDigit = false;
        for (int i = 0; i < data.length(); i++)
            if (!Character.isDigit(data.charAt(i)))
                containsNonDigit = true;

        // fix invalid data
        if (data.length() > 1 && containsNonDigit) {
            changeRegisterValue(number,0);
            return 0;
        } else if (data.length() == 1 && containsNonDigit) {
            int asciiVal = data.charAt(0);
            changeRegisterValue(number,asciiVal);
            return asciiVal;
        }

        // return valid data
        return Integer.parseInt(data);
    }

    private void styleComponents() {
        styleButtonPanel();
        styleEditorPanel();
        styleTerminalPanel();
        styleStackPanel();
        styleRegisterPanel();
        styleScreenPanel();
    }

    public void drawOnScreen (BufferedImage bufferedImage) {
        // crop image (or add black background to image)
        if (!(bufferedImage.getWidth() == screen.getScreenWidth() &&
                bufferedImage.getHeight() == screen.getScreenHeight())) {
            BufferedImage cropped = new BufferedImage(screen.getScreenWidth(),screen.getScreenHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            // copy pixels to cropped
            for (int x = 0; x < cropped.getWidth(); x++) {
                for (int y = 0; y < cropped.getHeight(); y++) {
                    if (x < bufferedImage.getWidth() && y < bufferedImage.getHeight())
                        cropped.setRGB(x,y,bufferedImage.getRGB(x,y));
                    else
                        cropped.setRGB(x,y,0);
                }
            }

            // replace buffered image
            bufferedImage = cropped;
        }

        // draw
        JLabel label = new JLabel();
        ImageIcon image = new ImageIcon(bufferedImage);
        label.setIcon(image);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setSize(new Dimension(bufferedImage.getWidth(),bufferedImage.getHeight()));
        screen.add(label);

        // update
        screen.revalidate();
        screen.repaint();
    }

    public Dimension getScreenSize () {
        return new Dimension(screen.getScreenWidth(),screen.getScreenHeight());
    }

    public void changeRegisterValue (int number, int value) {
        // change and update
        if (number < 10) {
            JTextArea textArea = (JTextArea) registerData.getComponent(number);
            SwingUtilities.invokeLater(() -> {textArea.setText(""+value); textArea.revalidate(); textArea.repaint();});
        } else {
            JLabel label = (JLabel) registerData.getComponent(number);
            SwingUtilities.invokeLater(() -> {label.setText(""+value); label.revalidate(); label.repaint();});
        }
    }

    public void changeStack (String text) {
        // get the text area
        JScrollPane scrollPane = (JScrollPane) stackPanel.getComponent(0);
        JTextArea textArea = (JTextArea) scrollPane.getViewport().getComponent(0);

        // change text
        textArea.setText(text);

        // update
        textArea.revalidate();
        textArea.repaint();
    }

    public void changeTerminal (String text) {
        // get the text area
        JScrollPane scrollPane = (JScrollPane) terminalPanel.getComponent(0);
        JTextArea textArea = (JTextArea) scrollPane.getViewport().getComponent(0);

        // change text
        textArea.setText(text);

        // update
        textArea.revalidate();
        textArea.repaint();
    }

    private void styleScreenPanel () {
        screenPanel.setBackground(new Color(136, 136, 136));
        screenPanel.setLayout(new BorderLayout());
        screen = new ScreenPanel();
        screen.setBackground(screenPanel.getBackground());
        screenPanel.add(screen,BorderLayout.CENTER);
    }

    private void styleRegisterPanel () {
        registerPanel.setBackground(new Color(119,119,119));
        registerPanel.setLayout(new BoxLayout(registerPanel,BoxLayout.Y_AXIS));
        JLabel header = new JLabel("Registers");
        header.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel registerPadding = new JPanel();
        registerPadding.setBackground(registerPanel.getBackground());
        registerPadding.setBorder(BorderFactory.createEmptyBorder(0,15,15,15));
        registerPanel.add(header);
        registerPanel.add(registerPadding);
        registerPadding.setLayout(new BorderLayout());
        JPanel registerNames = new JPanel();
        registerData = new JPanel();
        registerNames.setLayout(new BoxLayout(registerNames,BoxLayout.Y_AXIS));
        registerNames.setPreferredSize(new Dimension(20,2000));
        registerNames.setMinimumSize(new Dimension(20,2000));
        registerNames.setMaximumSize(new Dimension(20,2000));
        registerNames.setBackground(registerPanel.getBackground());
        registerData.setLayout(new BoxLayout(registerData,BoxLayout.Y_AXIS));
        registerData.setBackground(registerPanel.getBackground());
        registerPadding.add(registerNames,BorderLayout.WEST);
        registerPadding.add(registerData,BorderLayout.CENTER);
        String[] specialRegisters = {"TP","CL","ST","PS","CN","PK"};
        for (int i = 0; i < 10; i++) {
            registerNames.add(new JLabel("R" + i));
            JTextArea entry = new JTextArea();
            entry.setPreferredSize(new Dimension(2000, 16));
            entry.setMaximumSize(new Dimension(2000, 16));
            entry.setMinimumSize(new Dimension(2000, 16));
            entry.setBackground(registerPanel.getBackground());
            entry.setAlignmentX(Component.LEFT_ALIGNMENT);
            entry.setText("0");
            registerData.add(entry);
        }
        for (String name : specialRegisters) {
            registerNames.add(new JLabel(name));
            JLabel value = new JLabel("0");
            value.setHorizontalAlignment(SwingConstants.LEFT);
            value.setAlignmentX(Component.LEFT_ALIGNMENT);
            registerData.add(value);
        }

    }

    private void styleStackPanel () {
        stackPanel.setBackground(new Color(170,170,170));
        stackPanel.setLayout(new BorderLayout());

        JTextArea text = new JTextArea();
        text.setText("Stack Memory:\n\nAddress\tDecimal\n--- Frame Pointer ---");
        text.setMargin(new Insets(20,20,20,20));
        text.setBackground(stackPanel.getBackground());
        text.setFocusable(false);

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setBackground(stackPanel.getBackground());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        stackPanel.add(scrollPane,BorderLayout.CENTER);
    }

    private void styleTerminalPanel () {
        terminalPanel.setBackground(new Color(51,51,51));
        terminalPanel.setLayout(new BorderLayout());

        JTextArea text = new JTextArea();
        text.setForeground(Color.WHITE);
        text.setMargin(new Insets(20,20,20,20));
        text.setBackground(terminalPanel.getBackground());
        text.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setBackground(terminalPanel.getBackground());

        terminalPanel.add(scrollPane,BorderLayout.CENTER);
    }

    private void styleEditorPanel () {
        editorPanel.setBackground(new Color(136, 136, 136));
        editorPanel.setLayout(new BorderLayout());
        JPanel editorMargins = new JPanel();
        editorMargins.setBackground(editorPanel.getBackground());
        editorMargins.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        editorMargins.setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setMargin(new Insets(10,10,10,10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        codeArea = textArea;
        scrollPane.setPreferredSize(new Dimension(2000,2000));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        textArea.setBackground(new Color(156,156,156));
        scrollPane.setBackground(textArea.getBackground());
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK,1,true));
        editorMargins.add(scrollPane,BorderLayout.CENTER);
        editorPanel.add(editorMargins,BorderLayout.CENTER);
    }

    private void styleButtonPanel () {
        buttonPanel.setBackground(new Color(102,102,102));
        buttonPanel.setLayout(null);
        BevelPanel runButton = new BevelPanel();
        runButton.setSize(new Dimension(100,30));
        runButton.setBounds(20,15,runButton.getWidth(),runButton.getHeight());
        JLabel runLabel = new JLabel("Run");
        runLabel.setHorizontalAlignment(SwingConstants.CENTER);
        runLabel.setForeground(new Color(51,51,51));
        runButton.setBackground(new Color(102, 148, 103));
        runButton.setDecorativeBorder(new DecorativeBorder(new Color(82,128,83),2));
        runButton.setLayout(new BorderLayout());
        runButton.add(runLabel,BorderLayout.CENTER);
        runButton.setRoundTop(true);
        runButton.setRoundBottom(true);
        runButton.setRounding(10);

        BevelPanel terminateButton = new BevelPanel();
        terminateButton.setSize(new Dimension(100,30));
        terminateButton.setBounds(40+runButton.getWidth(),15,terminateButton.getWidth(),terminateButton.getHeight());
        JLabel terminateLabel = new JLabel("Terminate");
        terminateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        terminateLabel.setForeground(new Color(51,51,51));
        terminateButton.setBackground(new Color(158,59,54));
        terminateButton.setDecorativeBorder(new DecorativeBorder(new Color(138,39,34),2));
        terminateButton.setLayout(new BorderLayout());
        terminateButton.add(terminateLabel,BorderLayout.CENTER);
        terminateButton.setRoundTop(true);
        terminateButton.setRoundBottom(true);
        terminateButton.setRounding(10);

        // add functionality
        runButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent e) {
                super.mousePressed(e);
                if (buttonsAvailable) {
                    buttonPressed(runButton, runLabel, "Run");
                    buttonsAvailable = false;
                }
            }
        });
        runLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent e) {
                super.mousePressed(e);
                if (buttonsAvailable) {
                    buttonPressed(runButton, runLabel, "Run");
                    buttonsAvailable = false;
                }
            }
        });
        terminateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent e) {
                super.mousePressed(e);
                if (buttonsAvailable) {
                    buttonPressed(terminateButton, terminateLabel, "Terminate");
                    buttonsAvailable = false;
                }
            }
        });
        terminateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed (MouseEvent e) {
                super.mousePressed(e);
                if (buttonsAvailable) {
                    buttonPressed(terminateButton, terminateLabel, "Terminate");
                    buttonsAvailable = false;
                }
            }
        });

        buttonPanel.add(runButton);
        buttonPanel.add(terminateButton);
    }

    private void buttonPressed (BevelPanel panel, JLabel label, String name) {
        // gather required objects
        Color panelColor = panel.getColor();
        Color panelPressedColor = new Color(panelColor.getRed()-20,panelColor.getGreen()-20,
                panelColor.getBlue()-20);
        Color labelColor = label.getForeground();
        Color labelPressedColor = new Color(labelColor.getRed()-20,labelColor.getGreen()-20,
                labelColor.getBlue()-20);
        Timer resetColors = new Timer(250, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setBackground(panelColor);
                label.setForeground(labelColor);
                buttonsAvailable = true;
            }
        });
        resetColors.setRepeats(false);

        // change visuals
        panel.setBackground(panelPressedColor);
        label.setForeground(labelPressedColor);
        resetColors.start();

        // tell GUI
        if (name.equals("Run"))
            SwingUtilities.invokeLater(parent::runPressed);
        else
            SwingUtilities.invokeLater(parent::terminatePressed);
    }

    private void createComponents () {
        column1 = new JPanel();
        column2 = new JPanel();
        column3 = new JPanel();
        buttonPanel = new JPanel();
        editorPanel = new JPanel();
        terminalPanel = new JPanel();
        stackPanel = new JPanel();
        registerPanel = new JPanel();
        screenPanel = new JPanel();
    }

    private void updateGraphics () {
        frame.revalidate();
        frame.repaint();
        column1.revalidate();
        column1.repaint();
        column2.revalidate();
        column2.repaint();
        column3.revalidate();
        column3.repaint();
    }

    private void removeGraphicComponents () {
        frame.getContentPane().remove(column1);
        frame.getContentPane().remove(column2);
        frame.getContentPane().remove(column3);
        column1.remove(buttonPanel);
        column1.remove(editorPanel);
        column2.remove(terminalPanel);
        column2.remove(stackPanel);
        column3.remove(registerPanel);
        column3.remove(screenPanel);
        updateGraphics();
    }

    private void placeGraphicalElements () {
        removeGraphicComponents();

        if (frame.getWidth() >= 700) {
            column1.setPreferredSize(new Dimension(400, 2000));
            column1.setMinimumSize(new Dimension(400, 2000));
            column3.setPreferredSize(new Dimension(200, 2000));
            column3.setMinimumSize(new Dimension(200, 2000));
            frame.add(column1, BorderLayout.WEST);
            frame.add(column2, BorderLayout.CENTER);
            frame.add(column3, BorderLayout.EAST);
            column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
            column2.setLayout(new BorderLayout());
            column3.setLayout(new BorderLayout());
            buttonPanel.setPreferredSize(new Dimension(400, 60));
            buttonPanel.setMinimumSize(new Dimension(400, 60));
            editorPanel.setPreferredSize(new Dimension(400, 2000));
            terminalPanel.setPreferredSize(new Dimension(2000, 2000));
            stackPanel.setPreferredSize(new Dimension(2000, 200));
            stackPanel.setMinimumSize(new Dimension(2000, 200));
            registerPanel.setPreferredSize(new Dimension(200, 2000));
            screenPanel.setPreferredSize(new Dimension(200, 200));
            screenPanel.setMinimumSize(new Dimension(200, 200));
            column1.add(buttonPanel);
            column1.add(editorPanel);
            column2.add(terminalPanel, BorderLayout.CENTER);
            column2.add(stackPanel, BorderLayout.SOUTH);
            column3.add(registerPanel, BorderLayout.CENTER);
            column3.add(screenPanel, BorderLayout.SOUTH);
        } else {
            column1.setPreferredSize(new Dimension(400, 2000));
            column1.setMinimumSize(new Dimension(400, 2000));
            frame.add(column1, BorderLayout.WEST);
            frame.add(column2, BorderLayout.CENTER);
            column1.setLayout(new BoxLayout(column1, BoxLayout.Y_AXIS));
            column2.setLayout(new BorderLayout());
            buttonPanel.setPreferredSize(new Dimension(400, 60));
            buttonPanel.setMinimumSize(new Dimension(400, 60));
            editorPanel.setPreferredSize(new Dimension(400, 2000));
            stackPanel.setPreferredSize(new Dimension(2000, 200));
            stackPanel.setMinimumSize(new Dimension(2000, 200));
            registerPanel.setPreferredSize(new Dimension(200, 2000));
            column1.add(buttonPanel);
            column1.add(editorPanel);
            column2.add(registerPanel, BorderLayout.CENTER);
            column2.add(stackPanel, BorderLayout.SOUTH);
        }

        updateGraphics();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        boolean expanded = smallMode && frame.getWidth() >= 700;
        boolean condensed = !smallMode && frame.getWidth() < 700;
        super.componentResized(e);

        if (expanded || condensed)
            placeGraphicalElements();
        if (expanded)
            smallMode = false;
        if (condensed)
            smallMode = true;
    }
}