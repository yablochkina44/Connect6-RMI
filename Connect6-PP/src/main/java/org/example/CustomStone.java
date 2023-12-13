package org.example;
import javax.swing.*;
import java.awt.*;

public class CustomStone extends JPanel {
    private int colorStone;
    private int x, y;
    Image image;

    CustomStone(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.colorStone = color;
    }

    public void paint(Graphics g) {

        try {
            if (colorStone == 1) {
                image = new ImageIcon("src/main/resources/white.png").getImage();
            } else {
                image = new ImageIcon("src/main/resources/black.png").getImage();
            }
            g.drawImage(image, x, y, this);

        }
        catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }


    }

}