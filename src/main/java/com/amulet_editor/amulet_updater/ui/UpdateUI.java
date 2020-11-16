package com.amulet_editor.amulet_updater.ui;

import javax.swing.*;
import java.awt.*;

public class UpdateUI {

    private final JFrame frame;
    private final JProgressBar overallProgressBar;
    private final JProgressBar stepProgressBar;

    public UpdateUI(String currentVer, String newVer, int stepsLen) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        this.frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel mainPanel = new JPanel();

        // Start Label Panel
        JPanel labelPanel = new JPanel();

        JLabel titleLabel = new JLabel("Amulet Updater");
        JLabel updateLabel = new JLabel(currentVer + " -> " + newVer);

        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        updateLabel.setHorizontalAlignment(JLabel.CENTER);

        labelPanel.add(titleLabel);
        labelPanel.add(updateLabel);

        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // End Label Panel

        // Start Progress Bar Panel
        JPanel progressBarPanel = new JPanel();

        overallProgressBar = new JProgressBar(0, stepsLen);
        overallProgressBar.setValue(0);
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setBorderPainted(true);

        progressBarPanel.add(new JLabel("Overall Progress: "));
        progressBarPanel.add(overallProgressBar);

        stepProgressBar = new JProgressBar();
        stepProgressBar.setIndeterminate(true);
        stepProgressBar.setStringPainted(true);

        progressBarPanel.add(new JLabel("Step Progress: "));
        progressBarPanel.add(stepProgressBar);

        progressBarPanel.setLayout(new GridLayout(2,2, 0, 5));
        // End Progress Bar Panel


        mainPanel.add(labelPanel);
        mainPanel.add(progressBarPanel);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        frame.add(mainPanel);
        frame.pack();

        frame.setVisible(true);
    }

    public void updateStep(String stepName) {
        stepProgressBar.setString(stepName);
    }

    public void incrementStep() {
        int i = overallProgressBar.getValue();
        overallProgressBar.setValue(++i);
    }

    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }
}
