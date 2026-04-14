package com.srpafb.app;

import com.srpafb.view.MenuPrincipal;
import com.srpafb.view.ResultadoForm;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal();
        });

    }
}