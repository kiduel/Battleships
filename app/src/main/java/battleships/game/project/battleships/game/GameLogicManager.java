package battleships.game.project.battleships.game;

import android.os.Vibrator;

import battleships.game.project.battleships.ui.Board;

public class GameLogicManager
{
    public static int GAME_RUNNING = -1;
    public static int GAME_BATTLESHIPS_POSITIONING = -2;
    Board playerBoard;
    Board opponentBoard;

    public int game_state = GAME_BATTLESHIPS_POSITIONING;

    public GameLogicManager(Vibrator vibrator,Board playerBoard, Board opponentBoard,
                            GameMoveListener listener)
    {
        this.playerBoard = playerBoard;
        this.opponentBoard = opponentBoard;

        playerBoard.prepareBoard(vibrator, listener, random_battleships_positions,true);
        opponentBoard.prepareBoard(vibrator,listener, random_battleships_positions, false);

        playerBoard.setCanTouch(false);
        //setGameState(GAME_BATTLESHIPS_POSITIONING);
        playerBoard.drawBoardGrid();
    }

    public int[] getPlayerBoardData()
    {
        return playerBoard.data;
    }

    public void setGameState(int state)
    {
        if(state == GAME_RUNNING)
        {
            opponentBoard.drawBoardGrid();
            opponentBoard.drawAllBattleShips();
            playerBoard.drawBoardGrid();
            playerBoard.setGrids_detected(0);
            opponentBoard.setGrids_detected(0);
            playerBoard.resetBattleShipsGridsDetected();
            opponentBoard.resetBattleShipsGridsDetected();
        }
        else
            if(state == GAME_BATTLESHIPS_POSITIONING)
            {
                //playerBoard.prepareBoard(this,true);
                playerBoard.setRandomBattleShipsPosition(random_battleships_positions);
                playerBoard.drawBoardGrid();
                playerBoard.drawAllBattleShips();
                playerBoard.setDataFromBattleshipsPosition();
                playerBoard.setGrids_detected(0);
                opponentBoard.setGrids_detected(0);
                playerBoard.setCanTouch(false);

            }
        game_state = state;
        opponentBoard.setGameState(state);
        playerBoard.setGameState(state);
    }

    public void copyPlayerBoardToOpponent()
    {
        playerBoard.copyBattlehipsTo(opponentBoard);
    }
    public boolean areAllBattleshipsInRightPlace()
    {
        return playerBoard.areAllBattleShipsInRightPlace();
    }
    public Battleship[] getPlayerBattleships()
    {
        return playerBoard.getPlayerBattleships();
    }
    public void setReceivedBattleships(int[] bdata)
    {
        playerBoard.setReceivedBattleships(bdata);
    }


    int[] random_battleships_positions =
            {7, 0, 0, 7, 5, 0, 4, 0, 0, 0, 3, -90, 0, 0, -90, 5, 3, 0, 3, 4, 0,
                    4, 0, 0, 6, 0, 0, 2, 1, 0, 0, 4, -90, 5, 4, -90, 2, 6, -90, 4, 2, -90,
             7, 0, 0, 7, 6, 0, 5, 1, 0, 0, 6, -90, 0, 0, -90, 7, 2, 0, 3, 4, 0,
             7, 0, 0, 7, 6, 0, 0, 2, 0, 0, 6, -90, 3, 0, -90, 3, 2, 0, 5, 4, 0,
                    4, 0, 0, 4, 7, 0, 1, 1, 0, 6, 6, -90, 0, 6, -90, 4, 2, -90, 4, 4, -90,
             3, 0, 0, 7, 0, 0, 7, 5, 0, 0, 6, -90, 3, 7, -90, 0, 1, 0, 5, 2, 0,
             3, 0, 0, 3, 5, 0, 4, 2, 0, 6, 5, -90, 3, 7, -90, 7, 0, 0, 1, 0, 0,
                    1, 4, 0, 7, 6, 0, 5, 3, 0, 2, 2, -90, 0, 6, -90, 5, 1, -90, 0, 0, -90,
             7, 0, 0, 7, 5, 0, 0, 6, 0, 6, 7, -90, 5, 2, -90, 2, 5, 0, 4, 4, 0,
             6, 0, 0, 7, 2, 0, 7, 4, 0, 0, 0, -90, 0, 6, -90, 0, 2, 0, 4, 0, 0,
                    0, 2, 0, 1, 4, 0, 5, 2, 0, 6, 6, -90, 0, 6, -90, 4, 5, 0, 3, 0, -90,
             7, 0, 0, 7, 7, 0, 0, 0, 0, 4, 7, -90, 0, 7, -90, 0, 3, 0, 7, 2, 0,
             4, 0, 0, 6, 0, 0, 0, 0, 0, 0, 4, -90, 0, 7, -90, 3, 2, 0, 5, 2, 0,
                    0, 0, 0, 2, 1, 0, 4, 0, 0, 2, 3, -90, 5, 5, -90, 0, 3, 0, 1, 7, -90
                    //---------------------------------------------------------------
            };
}
