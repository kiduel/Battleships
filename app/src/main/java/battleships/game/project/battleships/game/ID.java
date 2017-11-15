package battleships.game.project.battleships.game;

import static battleships.game.project.battleships.ui.Board.grid_rows;


public class ID
{

    public int x,y;

    public ID()
    {

    }

    public int transformToInt(int x, int y)
    {
        return (y)*grid_rows + x;
    }
    public int transformSelfToInt()
    {
        return y*grid_rows + x;
    }
}