package com.houarizegai.calculator.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.houarizegai.calculator.theme.ThemeLoader;
import com.houarizegai.calculator.theme.properties.Theme;
import static com.houarizegai.calculator.util.ColorUtil.hex2Color;

public class CalculatorUI {

    private static final String FONT_NAME = "Segoe UI";
    private static final String DOUBLE_OR_NUMBER_REGEX = "([-]?\\d+[.]\\d*)|(\\d+)|(-\\d+)";
    private static final String APPLICATION_TITLE = "Calculator";
    private static final int WINDOW_WIDTH = 610;
    private static final int WINDOW_HEIGHT = 800;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 70;
    private static final int MARGIN_X = 20;
    private static final int MARGIN_Y = 60;

    private final JFrame window;
    private JComboBox<String> comboCalculatorType;
    private JComboBox<String> comboTheme;
    private JTextField inputScreen;
    private JButton btnClear;
    private JButton btnBackspace;
    private JButton btnModulus;
    private JButton btnDivide;
    private JButton btnMultiply;
    private JButton btnSubtract;
    private JButton btnAdd;
    private JButton btnZero;
    private JButton btnOne;
    private JButton btnTwo;
    private JButton btnThree;
    private JButton btnFour;
    private JButton btnFive;
    private JButton btnSix;
    private JButton btnSeven;
    private JButton btnEight;
    private JButton btnNine;
    private JButton btnPoint;
    private JButton btnEqual;
    private JButton btnPi; 
    private JButton btnRoot;
    private JButton btnPower;
    private JButton btnLog;

    private char selectedOperator = ' ';
    private boolean isReadyToCalculate = true; // For calculate with Opt != (=)
    private boolean shouldAppendToDisplay = true; // Connect numbers in display
    private double typedValue = 0;

    private final Map<String, Theme> themesMap;

    // Getter for testing
    public String getInputScreenText() {
        return inputScreen.getText();
    }

    // Method to trigger pi button for testing
    public void triggerPiButton() {
        btnPi.doClick();
    }

    public CalculatorUI() {
        themesMap = ThemeLoader.loadThemes();

        window = new JFrame(APPLICATION_TITLE);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);

        int[] columns = {MARGIN_X, MARGIN_X + 90, MARGIN_X + 90 * 2, MARGIN_X + 90 * 3, MARGIN_X + 90 * 4};
        int[] rows = {MARGIN_Y, MARGIN_Y + 100, MARGIN_Y + 100 + 80, MARGIN_Y + 100 + 80 * 2, MARGIN_Y + 100 + 80 * 3, MARGIN_Y + 100 + 80 * 4};

        initInputScreen(columns, rows);
        initButtons(columns, rows);
        initCalculatorTypeSelector();

        initThemeSelector();

        window.setLayout(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public double calculate(double firstNumber, double secondNumber, char operator) {
        switch (operator) {
            case '+':
                return firstNumber + secondNumber;
            case '-':
                return firstNumber - secondNumber;
            case '*':
                return firstNumber * secondNumber;
            case '/':
                return firstNumber / secondNumber;
            case '%':
                return firstNumber % secondNumber;
            case '^':
                return Math.pow(firstNumber, secondNumber);
            default:
                return secondNumber;
        }
    }

    private void initThemeSelector() {
        comboTheme = createComboBox(themesMap.keySet().toArray(new String[0]), 230, 30, "Theme");
        comboTheme.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;

            String selectedTheme = (String) event.getItem();
            applyTheme(themesMap.get(selectedTheme));
        });

        if (themesMap.entrySet().iterator().hasNext()) {
            applyTheme(themesMap.entrySet().iterator().next().getValue());
        }
    }

    private void initInputScreen(int[] columns, int[] rows) {
        inputScreen = new JTextField("0");
        inputScreen.setBounds(columns[0], rows[0], 350, 70);
        inputScreen.setEditable(false);
        inputScreen.setBackground(Color.WHITE);
        inputScreen.setFont(new Font(FONT_NAME, Font.BOLD, 36));
        window.add(inputScreen);
    }

    private void initCalculatorTypeSelector() {
        comboCalculatorType = createComboBox(new String[]{"Standard", "Scientific"}, 20, 30, "Calculator type");
        comboCalculatorType.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;

            String selectedItem = (String) event.getItem();
            switch (selectedItem) {
                case "Standard":
                    window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                    btnRoot.setVisible(false);
                    btnPower.setVisible(false);
                    btnLog.setVisible(false);
                    break;
                case "Scientific":
                    window.setSize(WINDOW_WIDTH + 80, WINDOW_HEIGHT);
                    btnRoot.setVisible(true);
                    btnPower.setVisible(true);
                    btnLog.setVisible(true);
                    btnPi.setVisible(true); //BDN
                    break;
            }
        });
    }

    private void initButtons(int[] columns, int[] rows) {
        btnClear = createButton("C", columns[0], rows[1]);
        btnClear.addActionListener(event -> {
            inputScreen.setText("0");
            selectedOperator = ' ';
            typedValue = 0;
        });

        btnBackspace = createButton("<-", columns[1], rows[1]);
        btnBackspace.addActionListener(event -> {
            String str = inputScreen.getText();
            StringBuilder str2 = new StringBuilder();
            for (int i = 0; i < (str.length() - 1); i++) {
                str2.append(str.charAt(i));
            }
            if (str2.toString().equals("")) {
                inputScreen.setText("0");
            } else {
                inputScreen.setText(str2.toString());
            }
        });

        btnModulus = createButton("%", columns[2], rows[1]);
        btnModulus.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()) || !isReadyToCalculate)
                return;

            typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
            if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                inputScreen.setText(String.valueOf((int) typedValue));
            } else {
                inputScreen.setText(String.valueOf(typedValue));
            }
            selectedOperator = '%';
            isReadyToCalculate = false;
            shouldAppendToDisplay = false;
        });

        btnDivide = createButton("/", columns[3], rows[1]);
        btnDivide.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '/';
                isReadyToCalculate = false;
                shouldAppendToDisplay = false;
            } else {
                selectedOperator = '/';
            }
        });

        btnSeven = createButton("7", columns[0], rows[2]);
        btnSeven.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("7");
                } else {
                    inputScreen.setText(inputScreen.getText() + "7");
                }
            } else {
                inputScreen.setText("7");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnEight = createButton("8", columns[1], rows[2]);
        btnEight.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("8");
                } else {
                    inputScreen.setText(inputScreen.getText() + "8");
                }
            } else {
                inputScreen.setText("8");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnNine = createButton("9", columns[2], rows[2]);
        btnNine.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("9");
                } else {
                    inputScreen.setText(inputScreen.getText() + "9");
                }
            } else {
                inputScreen.setText("9");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnMultiply = createButton("*", columns[3], rows[2]);
        btnMultiply.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '*';
                isReadyToCalculate = false;
                shouldAppendToDisplay = false;
            } else {
                selectedOperator = '*';
            }
        });

        btnFour = createButton("4", columns[0], rows[3]);
        btnFour.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("4");
                } else {
                    inputScreen.setText(inputScreen.getText() + "4");
                }
            } else {
                inputScreen.setText("4");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnFive = createButton("5", columns[1], rows[3]);
        btnFive.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("5");
                } else {
                    inputScreen.setText(inputScreen.getText() + "5");
                }
            } else {
                inputScreen.setText("5");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnSix = createButton("6", columns[2], rows[3]);
        btnSix.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("6");
                } else {
                    inputScreen.setText(inputScreen.getText() + "6");
                }
            } else {
                inputScreen.setText("6");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnSubtract = createButton("-", columns[3], rows[3]);
        btnSubtract.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }

                selectedOperator = '-';
                isReadyToCalculate = false;
                shouldAppendToDisplay = false;
            } else {
                selectedOperator = '-';
            }
        });

        btnOne = createButton("1", columns[0], rows[4]);
        btnOne.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("1");
                } else {
                    inputScreen.setText(inputScreen.getText() + "1");
                }
            } else {
                inputScreen.setText("1");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnTwo = createButton("2", columns[1], rows[4]);
        btnTwo.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("2");
                } else {
                    inputScreen.setText(inputScreen.getText() + "2");
                }
            } else {
                inputScreen.setText("2");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnThree = createButton("3", columns[2], rows[4]);
        btnThree.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("3");
                } else {
                    inputScreen.setText(inputScreen.getText() + "3");
                }
            } else {
                inputScreen.setText("3");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnAdd = createButton("+", columns[3], rows[4]);
        btnAdd.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '+';
                isReadyToCalculate = false;
                shouldAppendToDisplay = false;
            } else {
                selectedOperator = '+';
            }
        });

        btnPoint = createButton(".", columns[0], rows[5]);
        btnPoint.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (!inputScreen.getText().contains(".")) {
                    inputScreen.setText(inputScreen.getText() + ".");
                }
            } else {
                inputScreen.setText("0.");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnZero = createButton("0", columns[1], rows[5]);
        btnZero.addActionListener(event -> {
            if (shouldAppendToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("0");
                } else {
                    inputScreen.setText(inputScreen.getText() + "0");
                }
            } else {
                inputScreen.setText("0");
                shouldAppendToDisplay = true;
            }
            isReadyToCalculate = true;
        });

        btnEqual = createButton("=", columns[2], rows[5]);
        btnEqual.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '=';
                shouldAppendToDisplay = false;
            }
        });
        btnEqual.setSize(2 * BUTTON_WIDTH + 10, BUTTON_HEIGHT);

        
        btnPi = createButton("π", columns[4], rows[5]);
        btnPi.addActionListener(event -> {
            inputScreen.setText(String.valueOf(Math.PI));
            shouldAppendToDisplay = false;
        });

        btnRoot = createButton("√", columns[4], rows[1]);
        btnRoot.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = Math.sqrt(Double.parseDouble(inputScreen.getText()));
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '√';
                shouldAppendToDisplay = false;
            }
        });
        btnRoot.setVisible(false);

        btnPower = createButton("pow", columns[4], rows[2]);
        btnPower.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = calculate(typedValue, Double.parseDouble(inputScreen.getText()), selectedOperator);
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = '^';
                isReadyToCalculate = false;
                shouldAppendToDisplay = false;
            } else {
                selectedOperator = '^';
            }
        });
        btnPower.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        btnPower.setVisible(false);

        btnLog = createButton("ln", columns[4], rows[3]);
        btnLog.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (isReadyToCalculate) {
                typedValue = Math.log(Double.parseDouble(inputScreen.getText()));
                if (Pattern.matches("[-]?[\\d]+[.][0]*", String.valueOf(typedValue))) {
                    inputScreen.setText(String.valueOf((int) typedValue));
                } else {
                    inputScreen.setText(String.valueOf(typedValue));
                }
                selectedOperator = 'l';
                shouldAppendToDisplay = false;
            }
        });
        btnLog.setVisible(false);

        btnPi.setVisible(false); 
    }

    private JComboBox<String> createComboBox(String[] items, int x, int y, String toolTip) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBounds(x, y, 140, 25);
        combo.setToolTipText(toolTip);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        window.add(combo);

        return combo;
    }

    private JButton createButton(String label, int x, int y) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        btn.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        window.add(btn);

        return btn;
    }

    private void applyTheme(Theme theme) {
        window.getContentPane().setBackground(hex2Color(theme.getApplicationBackground()));

        comboCalculatorType.setForeground(hex2Color(theme.getTextColor()));
        comboTheme.setForeground(hex2Color(theme.getTextColor()));
        inputScreen.setForeground(hex2Color(theme.getTextColor()));
        btnZero.setForeground(hex2Color(theme.getTextColor()));
        btnOne.setForeground(hex2Color(theme.getTextColor()));
        btnTwo.setForeground(hex2Color(theme.getTextColor()));
        btnThree.setForeground(hex2Color(theme.getTextColor()));
        btnFour.setForeground(hex2Color(theme.getTextColor()));
        btnFive.setForeground(hex2Color(theme.getTextColor()));
        btnSix.setForeground(hex2Color(theme.getTextColor()));
        btnSeven.setForeground(hex2Color(theme.getTextColor()));
        btnEight.setForeground(hex2Color(theme.getTextColor()));
        btnNine.setForeground(hex2Color(theme.getTextColor()));
        btnPoint.setForeground(hex2Color(theme.getTextColor()));        
        btnPi.setForeground(hex2Color(theme.getTextColor()));           
        btnClear.setForeground(hex2Color(theme.getTextColor()));
        btnBackspace.setForeground(hex2Color(theme.getTextColor()));
        btnModulus.setForeground(hex2Color(theme.getTextColor()));
        btnDivide.setForeground(hex2Color(theme.getTextColor()));
        btnMultiply.setForeground(hex2Color(theme.getTextColor()));
        btnSubtract.setForeground(hex2Color(theme.getTextColor()));
        btnAdd.setForeground(hex2Color(theme.getTextColor()));
        btnRoot.setForeground(hex2Color(theme.getTextColor()));
        btnLog.setForeground(hex2Color(theme.getTextColor()));
        btnPower.setForeground(hex2Color(theme.getTextColor()));
        btnEqual.setForeground(hex2Color(theme.getBtnEqualTextColor()));

        comboCalculatorType.setBackground(hex2Color(theme.getApplicationBackground()));
        comboTheme.setBackground(hex2Color(theme.getApplicationBackground()));
        inputScreen.setBackground(hex2Color(theme.getApplicationBackground()));
        btnZero.setBackground(hex2Color(theme.getNumbersBackground()));
        btnOne.setBackground(hex2Color(theme.getNumbersBackground()));
        btnTwo.setBackground(hex2Color(theme.getNumbersBackground()));
        btnThree.setBackground(hex2Color(theme.getNumbersBackground()));
        btnFour.setBackground(hex2Color(theme.getNumbersBackground()));
        btnFive.setBackground(hex2Color(theme.getNumbersBackground()));
        btnSix.setBackground(hex2Color(theme.getNumbersBackground()));
        btnSeven.setBackground(hex2Color(theme.getNumbersBackground()));
        btnEight.setBackground(hex2Color(theme.getNumbersBackground()));
        btnNine.setBackground(hex2Color(theme.getNumbersBackground()));
        btnPoint.setBackground(hex2Color(theme.getNumbersBackground()));
        btnClear.setBackground(hex2Color(theme.getOperatorBackground()));
        btnBackspace.setBackground(hex2Color(theme.getOperatorBackground()));
        btnModulus.setBackground(hex2Color(theme.getOperatorBackground()));
        btnDivide.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMultiply.setBackground(hex2Color(theme.getOperatorBackground()));
        btnSubtract.setBackground(hex2Color(theme.getOperatorBackground()));
        btnAdd.setBackground(hex2Color(theme.getOperatorBackground()));
        btnRoot.setBackground(hex2Color(theme.getOperatorBackground()));
        btnLog.setBackground(hex2Color(theme.getOperatorBackground()));
        btnPower.setBackground(hex2Color(theme.getOperatorBackground()));
        btnEqual.setBackground(hex2Color(theme.getBtnEqualBackground()));
    }
}