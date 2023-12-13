package org.example;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

//2 - не поставлены чбш
//1 - поставлен белый шарик
//0 - поставлен черный шарик

public class Server implements GameInterface {
    private int lastCoordinateX = -1, lastCoordinateY = -1;
    private boolean id1 = false;
    private static int [][] gameBoard = new int[19][19];
    private static int []movesCounter = new int[2];
    private static int winnerID = 2;

    public Server() {
        for(int i=0; i<18; i++)
            for(int j=0; j<18; j++)
                gameBoard[i][j] = 2;

        movesCounter[0]=0;
        movesCounter[1]=0;
    }

    private static boolean checkWinner(int x, int y, int checkColor){
        int curX = x;
        int curY = y;

        //Проверка по горизонтали
        while(gameBoard[curX][curY]==(checkColor)) {
            movesCounter[checkColor]++;
            curX+=1;
        }
        curX = x-1;
        while(gameBoard[curX][curY]==(checkColor)) {
            movesCounter[checkColor]++;
            curX-=1;
        }

        if(movesCounter[checkColor]>=6)
            return true;
        else{
            //Проверка по вертикали
            movesCounter[checkColor] = 0;
            curX = x;
            curY = y;
            while(gameBoard[curX][curY]==(checkColor)) {
                movesCounter[checkColor]++;
                curY+=1;
            }
            curY = y-1;
            while(gameBoard[curX][curY]==(checkColor)) {
                movesCounter[checkColor]++;
                curY-=1;
            }

            if(movesCounter[checkColor]>=6)
                return true;
            else{
                //Проверка по диагонали (возрастание)
                movesCounter[checkColor] = 0;
                curX = x;
                curY = y;
                while(gameBoard[curX][curY]==(checkColor)) {
                    movesCounter[checkColor]++;
                    curX+=1;
                    curY+=1;
                }
                curX = x-1;
                curY = y-1;
                while(gameBoard[curX][curY]==(checkColor)) {
                    movesCounter[checkColor]++;
                    curX-=1;
                    curY-=1;
                }

                if(movesCounter[checkColor]>=6)
                    return true;
                else{
                    //Проверка по диагонали 2 (убывание)
                    movesCounter[checkColor] = 0;
                    curX = x;
                    curY = y;
                    while(gameBoard[curX][curY]==(checkColor)) {
                        movesCounter[checkColor]++;
                        curX+=1;
                        curY-=1;
                    }
                    curX = x-1;
                    curY = y+1;
                    while(gameBoard[curX][curY]==(checkColor)) {
                        movesCounter[checkColor]++;
                        curX-=1;
                        curY+=1;
                    }
                    if(movesCounter[checkColor]>=6)
                        return true;
                }
            }
        }
        movesCounter[checkColor] = 0;
        return false;
    }

    public void setPosition(int x, int y, int color) {
        gameBoard[x][y] = color;
        lastCoordinateX = x;
        lastCoordinateY = y;

        if (checkWinner(x, y, color)) {
            winnerID = (color == 1) ? 1 : 0;
        }
    }

    public int getWinner(){
        return winnerID;
    }

    public int getColor(){
        if(!id1) {
            id1 = true;
            return 0;
        }
        else{
            return 1;
        }
    }

    public int[] getOpponent(){
        int[] enemy = new int[2];
        enemy[0] = lastCoordinateX;
        enemy[1] = lastCoordinateY;
        lastCoordinateX = -1;
        lastCoordinateY = -1;
        return enemy;
    }

    public static void main(String args[]) {
        try {
            Server obj = new Server();
            GameInterface stub = (GameInterface) UnicastRemoteObject.exportObject(obj, 1099);
            Registry registry = LocateRegistry.createRegistry(1099);//LocateRegistry.getRegistry();
            registry.rebind("Server", stub);
            System.out.println("Server is ready!");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}