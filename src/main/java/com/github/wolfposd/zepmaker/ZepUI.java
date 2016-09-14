/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 wolfposd
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.wolfposd.zepmaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ZepUI {

    public JFrame frame;
    public JLabel imageLoadedPreview;

    public JCheckBox paddingEnabled;
    public JCheckBox keepColors;

    public JLabel processing;
    public JButton createButton;
    public JButton loadImageButton;
    public JComboBox<String> formatSelection;

    public ZepUI() {
        frame = new JFrame("Zeppelin Logo Maker 0.0.4");
        frame.setLayout(new BorderLayout());

        imageLoadedPreview = new JLabel();
        imageLoadedPreview.setHorizontalAlignment(JLabel.CENTER);
        imageLoadedPreview.setText("Select Image or Drag/Drop");

        loadImageButton = new JButton("Select Image");

        paddingEnabled = new JCheckBox("Add left-padding");
        keepColors = new JCheckBox("Keep colors");

        createButton = new JButton("Convert Image");

        formatSelection = new JComboBox<>(new String[]{"Zeppelin", "LockGlyph"});

        processing = new JLabel(" ");
        processing.setFont(processing.getFont().deriveFont(22.0f));
        processing.setHorizontalAlignment(JLabel.CENTER);

        JPanel southPanel = new JPanel(new GridLayout(6, 1));
        southPanel.add(paddingEnabled);
        southPanel.add(keepColors);
        southPanel.add(wrap(new FlowLayout(FlowLayout.LEFT), new JLabel("Convert for: "), formatSelection));
        southPanel.add(loadImageButton);
        southPanel.add(createButton);
        southPanel.add(processing);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLoadedPreview, BorderLayout.CENTER);

        imagePanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        southPanel.setBorder(BorderFactory.createTitledBorder("Config"));

        frame.add(imagePanel, BorderLayout.CENTER);
        frame.add(southPanel, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

    }
    public void setSuccessText(String t) {
        processing.setText(t);
        processing.setForeground(new Color(0, 100, 0));
    }

    public void setErrorText(String t) {
        processing.setText(t);
        processing.setForeground(new Color(178, 34, 34));
    }

    public void setVisible(boolean vs) {
        frame.setVisible(vs);
    }

    private JPanel wrap(LayoutManager layout, Component... components) {
        JPanel p = new JPanel(layout);
        for (Component c : components) {
            p.add(c);
        }
        return p;
    }
}
