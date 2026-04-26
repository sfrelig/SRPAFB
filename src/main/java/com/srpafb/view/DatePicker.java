package com.srpafb.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DatePicker extends JPanel {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private LocalDate selectedDate;
    private final JTextField txtDate;
    private final JButton btnCalendar;
    private LocalDate currentMonth;

    public DatePicker() {
        this(LocalDate.now());
    }

    public DatePicker(LocalDate initialDate) {
        selectedDate = initialDate != null ? initialDate : LocalDate.now();
        currentMonth = selectedDate.withDayOfMonth(1);

        setLayout(new BorderLayout(4, 0));

        txtDate = new JTextField(10);
        txtDate.setEditable(false);
        txtDate.setText(format(selectedDate));

        btnCalendar = new JButton("📅");
        btnCalendar.setFocusable(false);
        btnCalendar.addActionListener(this::mostrarCalendario);

        add(txtDate, BorderLayout.CENTER);
        add(btnCalendar, BorderLayout.EAST);
    }

    public LocalDate getDate() {
        return selectedDate;
    }

    public void setDate(LocalDate date) {
        LocalDate oldValue = this.selectedDate;
        this.selectedDate = date != null ? date : LocalDate.now();
        this.currentMonth = this.selectedDate.withDayOfMonth(1);
        txtDate.setText(format(this.selectedDate));
        firePropertyChange("date", oldValue, this.selectedDate);
    }

    public void addDateChangeListener(Runnable listener) {
        addPropertyChangeListener("date", evt -> listener.run());
    }

    private void mostrarCalendario(ActionEvent e) {
        Window window = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(window, "Seleccionar fecha", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(8, 8));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        JComboBox<String> monthCombo = new JComboBox<>();
        for (Month month : Month.values()) {
            monthCombo.addItem(month.getDisplayName(TextStyle.FULL_STANDALONE, new Locale("es")));
        }
        monthCombo.setSelectedIndex(currentMonth.getMonthValue() - 1);

        SpinnerNumberModel yearModel = new SpinnerNumberModel(currentMonth.getYear(), 1900, 2100, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);

        header.add(monthCombo);
        header.add(yearSpinner);
        dialog.add(header, BorderLayout.NORTH);

        JPanel daysContainer = new JPanel(new BorderLayout(4, 4));
        JPanel weekHeader = new JPanel(new GridLayout(1, 7, 4, 4));
        String[] dias = new String[]{"L", "M", "X", "J", "V", "S", "D"};
        for (String dia : dias) {
            JLabel label = new JLabel(dia, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            weekHeader.add(label);
        }
        daysContainer.add(weekHeader, BorderLayout.NORTH);

        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 4, 4));
        daysContainer.add(daysPanel, BorderLayout.CENTER);
        dialog.add(daysContainer, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnHoy = new JButton("Hoy");
        btnHoy.addActionListener(ev -> {
            setDate(LocalDate.now());
            dialog.dispose();
        });
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(ev -> dialog.dispose());
        footer.add(btnHoy);
        footer.add(btnCerrar);
        dialog.add(footer, BorderLayout.SOUTH);

        Runnable actualizar = () -> {
            currentMonth = LocalDate.of((int) yearSpinner.getValue(), monthCombo.getSelectedIndex() + 1, 1);
            actualizarDias(daysPanel);
        };

        monthCombo.addActionListener(ev -> actualizar.run());
        yearSpinner.addChangeListener(ev -> actualizar.run());

        actualizar.run();
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void actualizarDias(JPanel daysPanel) {
        daysPanel.removeAll();

        LocalDate firstDayOfMonth = currentMonth.withDayOfMonth(1);
        int offset = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        if (offset == 0) {
            offset = 6;
        } else {
            offset -= 1;
        }

        for (int i = 0; i < offset; i++) {
            daysPanel.add(new JLabel(""));
        }

        int diasDelMes = currentMonth.lengthOfMonth();
        for (int dia = 1; dia <= diasDelMes; dia++) {
            LocalDate fecha = currentMonth.withDayOfMonth(dia);
            JButton btnDia = new JButton(String.valueOf(dia));
            btnDia.setMargin(new Insets(4, 4, 4, 4));
            if (fecha.equals(selectedDate)) {
                btnDia.setBackground(new Color(0xAED581));
            }
            btnDia.addActionListener(ev -> {
                setDate(fecha);
                SwingUtilities.getWindowAncestor(daysPanel).dispose();
            });
            daysPanel.add(btnDia);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private static String format(LocalDate date) {
        return date != null ? date.format(FORMATTER) : "";
    }
}
