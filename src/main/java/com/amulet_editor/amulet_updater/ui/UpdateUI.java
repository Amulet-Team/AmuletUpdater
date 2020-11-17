package com.amulet_editor.amulet_updater.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateUI implements ActionListener {

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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(this);


        mainPanel.add(labelPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(progressBarPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(cancelButton);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        frame.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        frame.add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        frame.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        frame.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        frame.add(mainPanel, BorderLayout.CENTER);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
