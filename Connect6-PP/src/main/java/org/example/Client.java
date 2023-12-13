package org.example;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Класс Client является JPanel, что позволяет использовать его для создания пользовательского интерфейса
public class Client extends JPanel{
    private static final int CELL_SIZE = 27; //расстояние между клеточками
    private static final int STONE_RADIUS = 12; // радиус шарика
    private static final int BOARD_BORDER = 15; //смещение
    private static final int SLEEP_TIME = 15;
    private static JFrame frame = new JFrame("Connect6");
    private static int clickX, clickY;
    private static int x,y;
    private static int playerColor;
    private static boolean isMyTurn = false;
    private static int [] opponentMove;
    private static int [][] gameBoard = new int[19][19];
    private static int turnCount = 1;
    private Client() {}

    // Инициализация графического интерфейса
    public static void initializeGUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(530, 552);
        frame.add(new GameBoard());
        frame.setVisible(true);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickX = e.getX();
                clickY = e.getY();
            }
        });
    }

    // Инициализация игровой доски
    public static void initializeBoard(){
        for(int i=0; i<18; i++)
            for(int j=0; j<18; j++)
                gameBoard[i][j] = 2;
    }

    private static boolean isValidMove(int x, int y) {
        return x >= 0 && x <= 18 && y >= 0 && y <= 18 && gameBoard[x][y] == 2;
    }

    private static void handleOpponentMove() {
        if (opponentMove[0] != -1) {
            drawMove(opponentMove[0], opponentMove[1], 1 - playerColor);
            gameBoard[opponentMove[0]][opponentMove[1]] = 1 - playerColor;
            turnCount++;
            if (turnCount == 2) {
                isMyTurn = true;
                turnCount = 0;
            }
        }
        clickX = -100;
        clickY = -100;
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void drawMove(int x, int y, int color) {
        SwingUtilities.invokeLater(() -> {
            //frame.add(new CustomStone(BOARD_BORDER + CELL_SIZE * x - STONE_RADIUS, BOARD_BORDER + CELL_SIZE * y - STONE_RADIUS, color));
            frame.add(new CustomStone(BOARD_BORDER + CELL_SIZE * x-STONE_RADIUS , BOARD_BORDER + CELL_SIZE * y -STONE_RADIUS, color));
            frame.setVisible(true);
        });
    }

    private static void setHeadWindow() {
        if(playerColor == 1)
            frame.setTitle("Connect6 - White");
        else
            frame.setTitle("Connect6 - Black");
        opponentMove = new int[2];
        if(playerColor == 0)
            isMyTurn = true;
    }

    private static void Move(GameInterface gameStub) {
        makeMove(x, y, gameStub);
        // Визуализация хода с помощью CustomStone
        drawMove(x,y,playerColor);
        // Сброс координат клика и увеличение счетчика ходов
        clickX = -100;
        clickY = -100;
        turnCount++;
        if(turnCount == 2) {
            isMyTurn = false;
            turnCount = 0;
        }
    }
    private static void makeMove(int x, int y, GameInterface gameStub) {
        gameBoard[x][y] = playerColor;
        try {
            gameStub.setPosition(x, y, playerColor);
        } catch (Exception e) {
            System.err.println("Exception: " + e.toString());
        }
    }


    public static void main(String[] args) {
        initializeGUI();
        initializeBoard();

        try {
            // Подключение к удаленному реестру RMI и получение stub объекта
            Registry registry = LocateRegistry.getRegistry(1099);
            GameInterface gameStub = (GameInterface) registry.lookup("Server");

            // Получение цвета игрока и настройка заголовка окна
            playerColor =  gameStub.getColor();
            setHeadWindow();

            // Бесконечный цикл для взаимодействия с игрой
            while (true) {
                try {
                    if(isMyTurn) {
                        // Преобразование координат клика в номер строки и столбца на игровой доске
                        x = (clickX - BOARD_BORDER + STONE_RADIUS) / CELL_SIZE;
                        y = (clickY - BOARD_BORDER + STONE_RADIUS) / CELL_SIZE - 1;

                        // Проверка возможности хода и установка значения на игровой доске
                        if (isValidMove(x,y)) {
                            Move(gameStub);
                        }
                        // Задержка для улучшения производительности и избежания частых кликов
                        Thread.sleep(SLEEP_TIME);
                    }
                    else {
                        // Получение хода соперника и визуализация на игровой доске

                        opponentMove = gameStub.getOpponent();
                        handleOpponentMove();
                    }

                    // Проверка наличия победителя
                    int winner = gameStub.getWinner();
                    if(winner != 2) {
                        // Определение победителя и вывод сообщения
                        isMyTurn = false;
                        if(winner == playerColor)
                            JOptionPane.showMessageDialog(null, "Поздравляем с победой!!!");
                        else
                            JOptionPane.showMessageDialog(null, "Вы проиграли(");

                        // Закрытие окна после завершения игры
                        frame.dispose();
                        break;
                    }
                }
                // Обработка исключений
                catch (Exception e) {
                    System.err.println("SetPoint exception: " + e.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}


