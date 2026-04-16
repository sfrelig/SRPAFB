package com.srpafb.app;

import com.srpafb.view.MenuPrincipal;
import com.srpafb.view.ResultadoForm;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new MenuPrincipal();
        });

    }
}