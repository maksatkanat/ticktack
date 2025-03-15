package kz.gz.ticktack.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.gz.ticktack.data.AppNames;
import kz.gz.ticktack.data.config.Config;
import kz.gz.ticktack.data.config.Constants;
import kz.gz.ticktack.util.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.*;

@Slf4j
public class StarterGui extends JFrame {
    private JTextField announceUrlField, portalPasswordField, ecpPasswordField, ecpAbsPathField;
    private JTextField subjectAddressIdField, iikIdField, phoneNumberField, ipAddressesField;
    private JCheckBox legalEntityCheckBox, productAnnoCheckBox, subLotsTypeCheckBox;
    private Map<AppNames, JCheckBox> parallelAppsCheckBoxes;

    public StarterGui(Config config) {
        setTitle("Конфигурация");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Поля для ввода текста
        announceUrlField = createTextField("URL конкурса:", gbc, 0, config.getAnnounceUrl());
        portalPasswordField = createTextField("Пароль в ПОРТАЛ:", gbc, 1, config.getPortalPassword());
        ecpPasswordField = createTextField("Пароль ЭЦП:", gbc, 2, config.getEcpPassword());
        ecpAbsPathField = createFileChooserField("Файл ЭЦП:", gbc, 3, config.getEcpAbsPath());
//        subjectAddressIdField = createTextField("ID Юр.адреса:", gbc, 4, config.getSubjectAddressId());
//        iikIdField = createTextField("ID ИИК:", gbc, 5, config.getIikId());
//        phoneNumberField = createTextField("Номер Телефона:", gbc, 6, config.getPhoneNumber());
//        ipAddressesField = createTextField("IP Адреса собак (через запятую):", gbc, 7,
//                String.join(",", config.getIpAddresses()));

        // Чекбоксы
        gbc.gridwidth = 2;
        legalEntityCheckBox = createCheckBox("Запустить подписатор NCA:", gbc, 8, config.isLegalEntity(), false);
//        productAnnoCheckBox = createCheckBox("Тип конкурса ТОВАР:", gbc, 9, config.isProductAnno(), false);
//        subLotsTypeCheckBox = createCheckBox("SubLots Type:", gbc, 10, config.isSubLotsType(), false);



        // Добавляем отступ (пустую панель)
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1; // Маленький вес для выравнивания
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(1, 20)); // Высота 20px
        add(spacer, gbc);

        // Чекбоксы для Parallel Apps
        parallelAppsCheckBoxes = new LinkedHashMap<>();
        int row = 12;
//        for (AppNames app : AppNames.values()) {
//            boolean selected = config.getParallelAppsConfig().getOrDefault(app, false);
//            boolean disabled = false;
//            parallelAppsCheckBoxes.put(app, createCheckBox("Parallel " + app.name() + ":", gbc, row++, selected, disabled));
//        }

        // Кнопка сохранения
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JButton saveButton = new JButton("Сохранить и запустить программу");
        saveButton.setPreferredSize(new Dimension(150, 40));
        saveButton.addActionListener(this::saveConfig);
        add(saveButton, gbc);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        setAlwaysOnTop(true);
        setVisible(true);
    }

    private JTextField createTextField(String label, GridBagConstraints gbc, int row, String defaultValue) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3; // Метка занимает меньше места
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Поле ввода занимает всё доступное пространство
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 25)); // Фиксированный размер
        textField.setText(defaultValue);
        add(textField, gbc);
        return textField;
    }

    private JTextField createFileChooserField(String label, GridBagConstraints gbc, int row, String defaultPath) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 25));
        textField.setText(defaultPath);
        textField.setEditable(false); // Делаем поле только для чтения

        // Кнопка "Обзор..."
        JButton browseButton = new JButton("Обзор...");
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Добавляем поле и кнопку
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        gbc.gridwidth = 2; // Чтобы поле и кнопка занимали 2 колонки
        add(panel, gbc);

        return textField;
    }

    private JCheckBox createCheckBox(String label, GridBagConstraints gbc, int row, boolean selected, boolean disabled) {
        gbc.gridx = 0;
        gbc.gridy = row;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(selected);
        if (disabled) {
            checkBox.setEnabled(false);
        }
        add(checkBox, gbc);
        return checkBox;
    }

    private void saveConfig(ActionEvent e) {
        if (announceUrlField.getText().isEmpty() || portalPasswordField.getText().isEmpty() ||
                ecpPasswordField.getText().isEmpty() || ecpAbsPathField.getText().isEmpty() ) {

            JOptionPane.showMessageDialog(this, "Все обязательные поля должны быть заполнены!",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        HashMap<AppNames, Boolean> parallelAppNames = new HashMap<>();

        for (AppNames appName : parallelAppsCheckBoxes.keySet()) {
            Boolean value = parallelAppsCheckBoxes.get(appName).isSelected();
            parallelAppNames.put(appName, value);
        }

        List<String> validatedIps = Collections.synchronizedList(new ArrayList<>());

        // Сохраняем данные
        Config config = new Config(
                announceUrlField.getText(),
                portalPasswordField.getText(),
                ecpPasswordField.getText(),
                ecpAbsPathField.getText(),
                "",
                "",
                "",
                legalEntityCheckBox.isSelected(),
                false,
                false,
                parallelAppNames,
                validatedIps
        );

        ConfigUtils.saveConfig(config);

        // Конвертируем в JSON и печатаем в консоль
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            log.info("j8xYLSZ2wf :: StarterGui finished");
            log.info(json);
            //JOptionPane.showMessageDialog(this, "Конфигурация сохранена! JSON выведен в консоль.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении JSON!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }

        Constants.STARTER_GUI_FINISHED = true;
        dispose();
    }

        public boolean isValidIp(String ip) {
            try {
                InetAddress address = InetAddress.getByName(ip);
                return ip.equals(address.getHostAddress());
            } catch (UnknownHostException e) {
                return false;
            }
        }
}
