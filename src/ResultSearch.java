import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ResultSearch extends JFrame implements Runnable {


    private String startPath;
    private String mask;
    private String text;
    private Boolean isSearchDir;
    private ArrayList<String> pathFindFiles;
    private static int count = 0;
    private Boolean isComplete = false;
    private Boolean isStoped = false;

    private Thread threadWork;

    //классы для работы с регулярными выражениями
    private Pattern pattern = null;

    @Override
    public void run() {
        try {
            File Directory = new File(this.startPath);

            search(Directory);

            this.statusLabel.setForeground(Color.GREEN);
            this.statusLabel.setText("Статус: Поиск завершён");
            if (pathFindFiles.isEmpty())
                JOptionPane.showMessageDialog(this, "Уведомление: файлы типа '" + Thread.currentThread().getName() + "' не найдены", "Уведомление", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(this, "Уведомление: поиск '" + Thread.currentThread().getName() + "' завершён", "Уведомление", JOptionPane.INFORMATION_MESSAGE);

            isComplete = true;
        } catch (Exception e) {
            JOptionPane.setRootFrame(null);
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean fileContainsText(String fileName) {

        if (!fileName.endsWith(".txt") || this.text == null)//не нужно читать в файлах другого расширения
            return true;
        try {
            String allFile = WorkFile.readFile(fileName);

            if (allFile.contains(this.text))
                return true;
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private boolean accept(File fileName) {

        if (this.pattern == null)
            return true;

        if (this.pattern.matcher(fileName.getName()).matches() && fileContainsText(fileName.getAbsolutePath()))
            return true;
        return false;
    }

    private void search(File Directory) {

        try {
            File[] buffDir = Directory.listFiles();
            if (buffDir == null)
                return;

            for (File it : buffDir) {

                if(isStoped)
                    synchronized (this){
                        try{
                            wait();
                        }catch(InterruptedException e){
                        }
                    }
                   // TimeUnit.MICROSECONDS.sleep(10000);

                if (it.isDirectory()) {
                    if (isSearchDir)
                        try {
                            search(it);
                        } catch (Exception e) {

                        }
                    continue;
                }

                TimeUnit.MICROSECONDS.sleep(1000);

                if (accept(it)) {
                    listAdded.addElement(it.getName());
                    pathFindFiles.add(Directory.getAbsolutePath());
                }
            }
        } catch (Exception e) {

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////Интерфейс
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////Интерфейс
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////Интерфейс
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////Интерфейс
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////Интерфейс

    private JList<String> listResults;
    private JButton pauseOrStopButton = new JButton("Пауза");
    private JButton saveButton = new JButton("Сохранить");
    private JButton cancleButton = new JButton("Отмена");
    private JLabel dirLabel = new JLabel("Директория выбранного файла: ");
    private JButton showButton = new JButton("Показать");
    private JTextField dirField = new JTextField();
    private JLabel statusLabel = new JLabel("Статус: Поиск ... ");

    private JFrame currrentFrame;

    //Переменные для хранения данных
    private DefaultListModel<String> listAdded;

    //Создание маски Java
    private String crateMask(String inputMask) {

        StringBuffer mask = new StringBuffer();

        for (int i = 0; i < inputMask.length(); i++) {
            switch (inputMask.charAt(i)) {
                case '?': {
                    mask.append(".{1}");
                    break;
                }
                case '*': {
                    mask.append(".*");
                    break;
                }
                case '.': {
                    mask.append("\\.");
                    break;
                }
                default: {
                    mask.append(inputMask.charAt(i));
                    break;
                }
            }
        }

        return mask.toString();
    }

    public ResultSearch(String windowName, String startPath, String mask,
                        String text, Boolean searchDir, int priority) throws Exception {

        super("Результаты поиска: " + windowName);
        currrentFrame = this;

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setLayout(null);
        this.repaint(1, 0, 0, 400, 500);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                dispose();
                threadWork.interrupt();
            }
        });

        //Инициализация данных для поиска
        this.startPath = startPath;
        this.mask = mask;
        this.text = text;
        this.isSearchDir = searchDir;
        this.pathFindFiles = new ArrayList<String>();
        if (!mask.equals(""))
            pattern = Pattern.compile(crateMask(mask), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);


        //Инициализация интерфейса
        listResults = new JList<String>();
        listResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listAdded = new DefaultListModel<String>();
        listResults.setModel(listAdded);
        listResults.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                try {
                    if (!e.getValueIsAdjusting())
                        dirField.setText((String) pathFindFiles.get(listResults.getSelectedIndex()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //добавление списка
        JScrollPane pane = new JScrollPane(listResults);
        pane.setBounds(0, 15, 400, 350);
        this.add(pane);

        //отображение надписи "дир выбр файла"
        this.dirLabel.setBounds(0, 365, 300, 15);
        this.add(dirLabel);

        //Отображение поля для вывода path
        this.dirField.setBounds(0, 385, 300, 30);
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

        //отображение кнопки Показать
        this.showButton.setBounds(302,385,90,29);
        this.add(showButton);
        this.showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(!listResults.isSelectionEmpty()) {
                        Desktop op = null;
                        op.getDesktop().open(new File((String) pathFindFiles.get(listResults.getSelectedIndex())));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        //отображение кнопки pause/stop
        this.pauseOrStopButton.setBounds(35, 420, 100, 40);
        this.add(pauseOrStopButton);
        this.pauseOrStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!isComplete)
                        if(!isStoped){
                            isStoped = true;
                            statusLabel.setForeground(Color.red);
                            statusLabel.setText("Статус: Пауза");
                            pauseOrStopButton.setText("Пуск");
                        }
                        else{
                            isStoped = false;
                            threadWork.interrupt();
                            statusLabel.setForeground(Color.BLUE);
                            statusLabel.setText("Статус: Поиск ...");
                            pauseOrStopButton.setText("Пауза");
                        }


                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //отображение кнопки save
        this.saveButton.setBounds(150, 420, 100, 40);
        this.add(saveButton);
        this.saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    threadWork.wait();


                    StringBuffer buff = new StringBuffer();
                    for (int i = 0; i < listAdded.size(); i++)
                        buff.append("Имя файла: ").append(listAdded.get(i)).append("\nПуть к файлу: ").append(pathFindFiles.get(i)).append("\n\n\n");

                    count++;
                    StringBuffer pathToSaveFile = new StringBuffer(startPath).append("\\")
                            .append("Результаты поиска").append(Integer.toString(count)).append(".txt");
                    WorkFile.writeFile(pathToSaveFile.toString(), buff.toString());

                    threadWork.notify();

                    JOptionPane.showMessageDialog(currrentFrame, "Путь к сохранённому файлу (находится в выбранной Вами директории): \n" + pathToSaveFile.toString(), "Уведомление", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //отображение кнопки cancle
        this.cancleButton.setBounds(265, 420, 100, 40);
        this.add(cancleButton);
        this.cancleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    threadWork.interrupt();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(currrentFrame, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Отображение поля статуса
        this.statusLabel.setBounds(1, 0, 200, 15);
        this.statusLabel.setForeground(Color.BLUE);

        this.add(statusLabel);

        this.setVisible(true);

        //запуск потока (метода run)
        this.threadWork = new Thread(this, mask);//Создвание нового объекта Thread с указанием объекта, для которого будет вызываться метод run и имя этого потока
        this.threadWork.start();
        this.threadWork.setPriority(priority);
    }
}
