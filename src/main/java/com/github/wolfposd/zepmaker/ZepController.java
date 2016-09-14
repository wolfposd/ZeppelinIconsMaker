/*
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.github.wolfposd.zepmaker.formats.IFormat;
import com.github.wolfposd.zepmaker.formats.LockglyphFormat;
import com.github.wolfposd.zepmaker.formats.ZeppelinFormat;

public class ZepController {

    private File lastFile = null;

    private ZepUI zepui;

    private IFormat currentFormat = new ZeppelinFormat();

    public ZepController() {

        zepui = new ZepUI();

        zepui.createButton.addActionListener(e -> createAction());
        zepui.loadImageButton.addActionListener(e -> loadAction());
        zepui.imageLoadedPreview.setTransferHandler(new SingleFileTransferHandler(f -> {
            if (f != null) {
                String extension = getFileExtension(f).toLowerCase();
                if ("png".equals(extension)) {
                    loadImageIntoPreview(f);
                } else {
                    zepui.setErrorText("Error: Can only import PNG");
                }
            } else {
                zepui.setErrorText("Error: Can only import one file");
            }
        }));

        zepui.formatSelection.addActionListener(e -> formatSelectionChanged());

        zepui.setVisible(true);

    }

    private void formatSelectionChanged() {
        String value = (String) zepui.formatSelection.getSelectedItem();
        switch (value) {
            case "LockGlyph" :
                currentFormat = new LockglyphFormat();
                break;
            case "Zeppelin" :
            default :
                currentFormat = new ZeppelinFormat();
        }
        System.out.println(currentFormat);
    }

    public void createAction() {

        zepui.processing.setText("Starting Conversion...");
        zepui.processing.setForeground(Color.BLACK);

        SwingWorker<Integer, Object> w = new SwingWorker<Integer, Object>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return convertAndSaveImages();
            }
        };

        w.execute();
        w.addPropertyChangeListener(evt -> {
            if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
                try {
                    int result = w.get();
                    switch (result) {
                        case 1 :
                            zepui.setSuccessText("All done! :-)");
                            break;
                        case -1 :
                            zepui.setErrorText("Error: Couldn't create outputfolder :-(");
                            break;
                        case -2 :
                            zepui.setErrorText("Error: Couldn't create outputfolder :-(");
                            break;
                        case -3 :
                            zepui.setErrorText("Error: Select an image first");
                            break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    zepui.setErrorText("Error: Some unexpected shit happened :-(");
                    e.printStackTrace();
                }
            }
        });
    }

    private String getFileExtension(File f) {
        return f.getName().substring(f.getName().lastIndexOf(".") + 1);
    }

    private int convertAndSaveImages() {
        Image image = null;
        try {
            image = ((ImageIcon) zepui.imageLoadedPreview.getIcon()).getImage();
        } catch (NullPointerException ex) {
            return -3;
        }

        Image[] images = preparedImages(image, zepui.keepColors.isSelected(), zepui.paddingEnabled.isSelected());

        String fileExtension = getFileExtension(lastFile);
        File newFolder = new File(lastFile.getParentFile(), lastFile.getName().substring(0, lastFile.getName().lastIndexOf(".")));

        if (!newFolder.exists()) {
            newFolder.mkdir();
        }

        if (newFolder.isDirectory() && newFolder.canWrite()) {
            for (int i = 0; i < images.length; i++) {
                Image curIm = images[i];
                int w = curIm.getWidth(null);
                int h = curIm.getHeight(null);

                for (int j = 0; j < currentFormat.getSizes().length; j++) {
                    int size = currentFormat.getSizes()[j];

                    float scale = (float) h / (float) size;
                    int newW = Math.round(Math.round((double) w / scale));
                    int newH = Math.round(Math.round((double) h / scale));

                    Image newImg = curIm.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

                    File destinationFile = new File(newFolder, currentFormat.getFileNames()[i] + "" + currentFormat.getFileappends()[j]
                            + "." + fileExtension);

                    try {
                        ImageIO.write(toBufferedImage(newImg), fileExtension, destinationFile);
                    } catch (IOException e) {
                        System.out.println("failed creating image");
                        e.printStackTrace();
                        return -2;
                    }
                }

            }
        } else {
            return -1;
        }
        return 1;
    }
    public void loadAction() {

        JFileChooser jf = new JFileChooser();
        jf.setFileFilter(new FileNameExtensionFilter("Only Imagefiles (png)", "png"));
        jf.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int res = jf.showOpenDialog(zepui.frame);
        File f = jf.getSelectedFile();

        if (res == JFileChooser.APPROVE_OPTION && f != null) {
            loadImageIntoPreview(f);
        }
    }

    public void loadImageIntoPreview(File f) {
        lastFile = f;
        ImageIcon icon = new StretchIcon(f.getAbsolutePath());
        zepui.imageLoadedPreview.setIcon(icon);
        zepui.imageLoadedPreview.setText("");
        zepui.setSuccessText("Loaded image.");
    }

    public Image[] preparedImages(Image image, boolean leavecolors, boolean padding) {
        Image[] result = new Image[currentFormat.getFileNames().length];

        if (!leavecolors) {
            Color[] recolors = currentFormat.getRecolors();
            for (int i = 0; i < recolors.length; i++) {
                Color rec = recolors[i];
                result[i] = recolorImage(image, rec.getRGB(), padding);
            }
        } else {
            for (int i = 0; i < currentFormat.getFileNames().length; i++) {
                result[i] = padding ? padImage(image) : toBufferedImage(image);
                // this also creates copies
            }
        }

        return result;
    }

    public Image padImage(Image origImage) {
        int width = origImage.getWidth(null);
        float scale = width / currentFormat.getSizeBiggest();
        int padding = Math.round(currentFormat.getPaddingForBiggest() * scale);

        BufferedImage bufimage = new BufferedImage(padding + origImage.getWidth(null), origImage.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = bufimage.getGraphics();

        g.drawImage(origImage, padding, 0, null);

        return bufimage;

    }

    public Image recolorImage(Image origImage, int newColor, boolean paddingEnabled) {

        int padding = 0;
        if (paddingEnabled) {
            int width = origImage.getWidth(null);
            float scale = width / currentFormat.getSizeBiggest();
            padding = Math.round(currentFormat.getPaddingForBiggest() * scale);
        }

        BufferedImage bufimage = new BufferedImage(padding + origImage.getWidth(null), origImage.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = bufimage.getGraphics();

        g.drawImage(origImage, padding, 0, null);

        for (int x = padding; x < bufimage.getWidth(); x++) {
            for (int y = 0; y < bufimage.getHeight(); y++) {
                int rgb = bufimage.getRGB(x, y);
                Color c = new Color(rgb, true);
                if (c.getAlpha() > 0) {
                    bufimage.setRGB(x, y, newColor);
                }
            }
        }

        return bufimage;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
