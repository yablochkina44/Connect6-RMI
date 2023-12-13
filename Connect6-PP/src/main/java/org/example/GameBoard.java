package org.example;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
public class GameBoard extends JPanel {
    private Image board;

    public GameBoard() {
        try {
            // Загрузка изображения с использованием ImageIO и URL

            // Получаем URL изображения с использованием относительного пути к ресурсу
            URL imageUrl = getClass().getResource("/boardconnect6.png");

            if (imageUrl != null) {
                // Если URL изображения найден, загружаем изображение с помощью ImageIO
                board = ImageIO.read(imageUrl);
            } else {
                // В случае, если URL изображения не найден, выводим сообщение об ошибке
                System.err.println("Unable to find the image resource.");
            }
        } catch (IOException e) {
            // Обработка ошибок ввода-вывода при загрузке изображения
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        if (board != null) {
            // Если изображение было успешно загружено, рисуем его
            g2d.drawImage(board, 0, 0, null);
        } else {
            // Обработка случая, когда изображение не было загружено

            // Заливаем панель красным цветом
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Выводим сообщение об ошибке
            g2d.setColor(Color.WHITE);
            g2d.drawString("Error loading image", 10, 20);
        }

        g2d.dispose();
    }

}
