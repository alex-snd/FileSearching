import javax.swing.*;
import java.awt.event.*;

public class CreateThread extends JFrame {

    private JButton showFileDialog = new JButton("Выбрать директорию");
    private JLabel enteredDir = new JLabel("Dir: ");
    private JTextField dirField = new JTextField();
    private JLabel LableMask = new JLabel("Поиск по маске: ");
    private JTextField enteredMaskField = new JTextField();
    private JCheckBox checkText = new JCheckBox("Содержимое в файле:");
    private JTextField enteredTextField = new JTextField();
    private JCheckBox searhOtherDir = new JCheckBox("Поиск в поддиректориях");
    private JButton searchButton = new JButton("Поиск");
    private JButton cancelButton = new JButton("Отмена");
    public static JLabel activThreadLabel = new JLabel("Активные потоки: " + Integer.toString(Thread.activeCount()));

    private JFrame currrentFrame;

    //Значениея для создания потока
    private String selectedDirectory;
    private String enteredMask;
    private String enteredText;
    private Boolean searchDir;

    public CreateThread(String windowName) {

        super(windowName);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 350);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        currrentFrame = this;

        this.searchDir = false;

        setLayout(null);

        //Отображение кнопки диалогового окна выбора директории
        showFileDialog.setBounds(50, 10, 200, 40);
        this.add(showFileDialog);//Окно выбора директории
        this.showFileDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    JFileChooser getDir = new JFileChooser();
                    getDir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    getDir.setMultiSelectionEnabled(false);
                    getDir.showDialog(currrentFrame, "choose directory");

                    if (getDir.getSelectedFile() != null) {
                        selectedDirectory = getDir.getSelectedFile().getAbsolutePath();
                        dirField.setText(selectedDirectory);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // отображение выбранной директории
        this.enteredDir.setBounds(50, 40, 30, 40);
        this.add(enteredDir);
        //Отображение поля для вывода выбранного path
        this.dirField.setBounds(70, 52, 180, 20);
        this.add(dirField);
        this.dirField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    e.consume();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // отображение надписи "Поиск по маске: "
        this.LableMask.setBounds(50, 60, 130, 40);
        this.LableMask.setToolTipText("? - один любой сивол; * - любые нессколько символов");
        this.add(LableMask);

        //Отображение поля для ввода маски
        this.enteredMaskField.setBounds(50, 90, 200, 20);
        this.add(enteredMaskField);
        this.enteredMaskField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if(!enteredMaskField.getText().equals(""))
                        enteredMask = enteredMaskField.getText();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Отображение галочки (содержимое в файле)
        this.checkText.setBounds(47, 110, 200, 20);
        this.checkText.setToolTipText("Только для *.txt");
        this.add(checkText);
        this.checkText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try{
                    enteredTextField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Отображение поля для ввода содержимого в файле
        this.enteredTextField.setBounds(50, 130, 200, 20);
        this.add(enteredTextField);
        this.enteredTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (checkText.isSelected())
                        enteredText = enteredTextField.getText();
                    else enteredTextField.setText("Кликните на галочку");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        this.enteredTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                try {
                    if (!checkText.isSelected())
                        e.consume();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Отображение галочки (поиск в поддиректориях)
        this.searhOtherDir.setBounds(47, 155, 200, 20);
        this.add(searhOtherDir);
        this.searhOtherDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try{
                    if (searhOtherDir.isSelected())
                        searchDir = true;
                    if (!searhOtherDir.isSelected())
                        searchDir = false;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        //Отображение кнопки "Поиск"
        searchButton.setBounds(50, 200, 200, 40);
        this.add(searchButton);//Окно выбора директории
        this.searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if (selectedDirectory == null)
                        throw new Exception("Не выбрана директория!");
                    enteredMask = enteredMaskField.getText();
                    if (enteredMask == null)
                        throw new Exception("Введите маску!");
                    if (checkText.isSelected()) {
                        enteredText = enteredTextField.getText();
                        if (enteredText == null)
                            throw new Exception("Введите отрывок содержимого файла!");
                    }

                    //создание потока Resault....
                   // ResultSearch newThread1 = new ResultSearch(enteredMask, selectedDirectory, enteredMask, enteredText, searchDir, 1);
                    ResultSearch newThread = new ResultSearch(enteredMask, selectedDirectory, enteredMask, enteredText, searchDir, 10);


                    Main.startedThread = true;
                    activThreadLabel.setText("Активные потоки: " + Integer.toString(Thread.activeCount()));

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Отображение кнопки "Отмена"
        cancelButton.setBounds(50, 240, 200, 40);
        this.add(cancelButton);//Окно выбора директории
        this.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    dispose();
                    System.exit(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // отображение активных потоков
        this.activThreadLabel.setBounds(90, 275, 200, 30);
        this.add(activThreadLabel);

        this.setVisible(true);


        Thread.activeCount();
    }

    public static void updateThreaActivCount(){
        activThreadLabel.setText("Активные потоки: " + Integer.toString(Thread.activeCount() - 2));
    }

}
