package battleships.game.project.battleships.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


import battleships.game.project.battleships.game.Battleship;
import battleships.game.project.battleships.game.GameLogicManager;
import battleships.game.project.battleships.game.GameMoveListener;
import battleships.game.project.battleships.game.HttpEventListener;
import battleships.game.project.battleships.game.ID;
import battleships.game.project.battleships.R;


/**
 * Created by Madalin on 5/31/2017.
 */

public class Board extends ImageView {

    boolean isPlayerBoard = true;
    Canvas canvas;
    Bitmap bitmap;
    Bitmap green_bitmap, red_bitmap, point_bitmap;
    Paint bitmapPaint;
    int width, height, grid_step;
    public static int grid_rows=8, grid_total_size = grid_rows*grid_rows;
    GameMoveListener gameMoveListener;
    public int[] data = new int[grid_total_size];
    Matrix matrix;

    Bitmap[] battleships_bitmaps = new Bitmap[4];
    Battleship[] battleships = new Battleship[7];
    ColorFilter redFilter = new LightingColorFilter(Color.RED, 0);
    ColorFilter noFilter = new ColorFilter();

    boolean canTouch = true;
    int gameState;
    int grids_detected=0;

    Vibrator vibrator;

    public Board(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initiate();
    }

    public Board(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initiate();
    }
    public Board(Context context) {
        super(context);
        initiate();
    }

    public void initiate()
    {
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setFlags(Paint.FILTER_BITMAP_FLAG);

        for(int i=0; i<grid_total_size; i++)
            data[i] = 0;
    }

    public void prepareBoard(Vibrator vibrator,
                             GameMoveListener gameMoveListener, int[]  random_positions,
                             boolean isPlayerBoard)
    {
        this.gameMoveListener = gameMoveListener;
        this.isPlayerBoard = isPlayerBoard;
        this.vibrator = vibrator;
        width = getLayoutParams().width;
        height = getLayoutParams().height;
        Log.e("width", Float.toString(width));
        grid_step = width/grid_rows;
        matrix = new Matrix();

        loadAndScaleBitmaps();
        initializeBattleShips();
        bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        drawBoardGrid();
        setImageBitmap(bitmap);
        if(isPlayerBoard)
            setOnTouchListener(createTouchListener());
        setRandomBattleShipsPosition(random_positions);
      //  drawAllBattleShips();

    }

    void loadAndScaleBitmaps()
    {
        green_bitmap = loadBitmap(R.drawable.green_texture);
        green_bitmap = Bitmap.createScaledBitmap(green_bitmap,grid_step,grid_step,true);
        red_bitmap = loadBitmap(R.drawable.red_texture);
        red_bitmap = Bitmap.createScaledBitmap(red_bitmap,grid_step,grid_step,true);
        point_bitmap = loadBitmap(R.drawable.point_texture);
        point_bitmap = Bitmap.createScaledBitmap(point_bitmap,grid_step,grid_step,true);

        battleships_bitmaps[0] = loadBitmap(R.drawable.battleship_texture);
        battleships_bitmaps[3] = Bitmap.createScaledBitmap(battleships_bitmaps[0],
                grid_step,grid_step*4,true);
        battleships_bitmaps[2] = Bitmap.createScaledBitmap(battleships_bitmaps[0],
                grid_step,grid_step*3,true);
        battleships_bitmaps[1] = Bitmap.createScaledBitmap(battleships_bitmaps[0],
                grid_step,grid_step*2,true);
        battleships_bitmaps[0] = Bitmap.createScaledBitmap(battleships_bitmaps[0],
                grid_step,grid_step*1,true);
    }

    public void setDataFromBattleshipsPosition()
    {
        clearData();
        for(int i=0; i<battleships.length; i++)
        {
           battleships[i].setDataFromGrid(data);
        }
    }
    void initializeBattleShips()
    {
        for(int i=0; i<2; i++)
            battleships[i] = new Battleship(1);
        for(int i=2; i<4; i++)
            battleships[i] = new Battleship(2);
        for(int i=4; i<6; i++)
            battleships[i] = new Battleship(3);
        battleships[6] = new Battleship(4);

    }
    public void setRandomBattleShipsPosition(int[] random_positions)
    {
        int a = 0;
        int f=0;
        int i;
        for(i = 0; i< a; i++)
            f += (int)(13*Math.random());
        f = (int) (13*Math.random());
        int x = 0;
        for(i=0; i<7; i++)
        {
            int g = f*21+3*i;
            battleships[i].grid_id.x = random_positions[g];
            battleships[i].grid_id.y = random_positions[g+1];
            battleships[i].setRotation(random_positions[g+2]);
        }
    }
    int getTouchedBattleshipId(ID touched_grid)
    {
        for(int i=0; i<battleships.length; i++)
            if(battleships[i].isSelfGrid(touched_grid))
                return i;
        return 0;
    }

    // ----------------------- drawing functions -------------------------------
    public void drawBoardGrid()
    {
        matrix.setTranslate(0,0);
        Bitmap grid = loadBitmap(R.drawable.board_grid);
        grid = Bitmap.createScaledBitmap(grid,width,height,true);
        canvas.drawBitmap(grid,matrix,bitmapPaint);
    }

    public void drawAllBattleShips()
    {
        for(int i=0; i<battleships.length; i++) {
            drawBattleShipToPlace(battleships[i]);
        }
        setImageBitmap(bitmap);
    }
    void clearData()
    {
        for(int i=0; i<grid_rows*grid_rows; i++)
            data[i] = 0;
    }
    void drawAllBattleShipsButOne(int id)
    {

       clearData();
        for( int i=0; i<battleships.length; i++)
            if(i!=id) {
                battleships[i].setDataFromGrid(data);
                drawBattleShipToPlace(battleships[i]);
            }

        setImageBitmap(bitmap);
    }
    void drawBattleShipToPlace(Battleship battleship,float x, float y)
    {
        if(battleship.useRedOverlay)
            bitmapPaint.setColorFilter(redFilter);

        matrix.setTranslate(x,y);
        if(battleship.getRotation()==-90) {
            matrix.postRotate(battleship.getRotation(), x, y);
            matrix.postTranslate(0, grid_step);
        }
        canvas.drawBitmap(battleships_bitmaps[battleship.getSize()-1],matrix,bitmapPaint);
        battleship.useRedOverlay = false;
        bitmapPaint.setColorFilter(noFilter);
    }
    void drawBattleShipToPlace(Battleship battleship)
    {
        if(battleship.useRedOverlay) {
            bitmapPaint.setColorFilter(redFilter);
        }

        float x = battleship.grid_id.x*grid_step;
        float y = battleship.grid_id.y*grid_step;

        matrix.setTranslate(x,y);
        if(battleship.getRotation()==-90) {
            matrix.postRotate(battleship.getRotation(), x, y);
            matrix.postTranslate(0, grid_step);
        }
        canvas.drawBitmap(battleships_bitmaps[battleship.getSize()-1],matrix,bitmapPaint);
        bitmapPaint.setColorFilter(noFilter);
    }
    void dropBattleShipToPlace(Battleship battleship,float x, float y)
    {
        if(battleship.useRedOverlay) {
            bitmapPaint.setColorFilter(redFilter);
        }

        battleship.grid_id.x = (int)(x/grid_step);
        battleship.grid_id.y = (int)(y/grid_step);


        float xx = battleship.grid_id.x*grid_step;
        float yy = battleship.grid_id.y*grid_step;
        matrix.setTranslate(xx,yy);
        if(battleship.getRotation()==-90) {
            matrix.postRotate(battleship.getRotation(), xx, yy);
            matrix.postTranslate(0, grid_step);
        }
        canvas.drawBitmap(battleships_bitmaps[battleship.getSize()-1],matrix,bitmapPaint);
        bitmapPaint.setColorFilter(noFilter);

    }
    public void dropGridToPlace(ID grid_id)
    {

        int i = grid_id.y*grid_rows+grid_id.x;
        matrix.setTranslate(grid_step*grid_id.x,grid_step*grid_id.y);

        if(data[i]==0) {
            canvas.drawBitmap(point_bitmap, matrix, bitmapPaint);
            vibrator.vibrate(30);
        }
        else {
            vibrator.vibrate(200);
            grids_detected ++;
            canvas.drawBitmap(red_bitmap, matrix, bitmapPaint);

            for(i=0; i<battleships.length; i++)
                if(battleships[i].isSelfGrid(grid_id))
                {
                    battleships[i].setGrids_found(battleships[i].getGrids_found()+1);
                    if(battleships[i].getGrids_found()==battleships[i].getSize())
                    {
                       battleships[i].showAdjacentWaterGrids(canvas,point_bitmap,bitmapPaint,
                               matrix,grid_step);
                    }

                }

        }

        setImageBitmap(bitmap);
    }
    public void resetBattleShipsGridsDetected()
    {
        for(int i=0; i<battleships.length; i++)
            battleships[i].setGrids_found(0);
    }
    // -----------------------------------------------------
    Bitmap loadBitmap(int drawable_id)
    {
        return  BitmapFactory.decodeResource(getContext().getResources(), drawable_id);
    }


    OnTouchListener createTouchListener()
    {
        return new OnTouchListener()
        {
            float x, y;
            ID id;
            int i=0;
            int touch_count = 0;
            long initialTap;
            boolean double_tap = false;
            int initial_grid_x, initial_grid_y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                x = event.getX();
                y = event.getY();
                if(canTouch) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            id = new ID();
                            id.x = (int) (x / grid_step);
                            id.y = (int) (y / grid_step);
                            i = getTouchedBattleshipId(id);
                            initial_grid_x = battleships[i].grid_id.x;
                            initial_grid_y = battleships[i].grid_id.y;
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE: {

                            if (gameState == GameLogicManager.GAME_BATTLESHIPS_POSITIONING) {
                                battleships[i].grid_id = computeGridIdFromTouch(x, y);
                                drawBoardGrid();
                                drawAllBattleShipsButOne(i);
                                battleships[i].checkIfPlaceIsGood(data);
                                dropBattleShipToPlace(battleships[i], x, y);
                                battleships[i].setDataFromGrid(data);


                                return true;
                            }
                        }

                        case MotionEvent.ACTION_UP: {

                            if (gameState == GameLogicManager.GAME_BATTLESHIPS_POSITIONING) {
                                if (touch_count == 1) {
                                    touch_count = 0;
                                    if (System.currentTimeMillis() - initialTap < 700) {
                                        double_tap = true;
                                        battleships[i].clearDataFootprint(data);
                                        battleships[i].changeRotation();
                                    }
                                } else {
                                    double_tap = false;
                                    initialTap = System.currentTimeMillis();
                                    touch_count = 1;
                                }
                                drawBoardGrid();
                                drawAllBattleShipsButOne(i);
                                if (!battleships[i].checkIfPlaceIsGood(data)) {
                                    battleships[i].grid_id.x = initial_grid_x;
                                    battleships[i].grid_id.y = initial_grid_y;
                                    if (!double_tap)
                                        battleships[i].useRedOverlay = false;
                                    drawBattleShipToPlace(battleships[i]);

                                } else {
                                    dropBattleShipToPlace(battleships[i], x, y);
                                    setDataFromBattleshipsPosition();
                                }

                            } else {

                                int k = data[7];
                                //ID id = computeGridIdFromTouch(x, y);
                                dropGridToPlace(id);
                                gameMoveListener.OnPlayerMove(id.x, id.y);


                            }

                            return false;
                        }
                    }
                }
                return false;
            }
        };
    }

    ID computeGridIdFromTouch(float x, float y)
    {
        ID id = new ID();
        id.x = (int)(x/grid_step);
        id.y = (int)(y/grid_step);
        return id;
    }
    public void setCanTouch(boolean canTouch)
    {
        this.canTouch = canTouch;
    }
    public void  setGameState(int state)
    {
        gameState = state;
    }
    public void copyBattlehipsTo(Board board)
    {
        for(int i=0; i<battleships.length; i++) {
            board.battleships[i].grid_id.x = battleships[i].grid_id.x;
            board.battleships[i].grid_id.y = battleships[i].grid_id.y;
            board.battleships[i].setSize(battleships[i].getSize());
            board.battleships[i].setRotation(battleships[i].getRotation());

        }
        board.setDataFromBattleshipsPosition();
    }
    public boolean areAllBattleShipsInRightPlace()
    {
        for(int i=0; i<battleships.length; i++)
            if(battleships[i].useRedOverlay)
                return false;
        return true;
    }

    public int getGrids_detected() {
        return grids_detected;
    }

    public void setGrids_detected(int grids_detected) {
        this.grids_detected = grids_detected;
    }
    public Battleship[] getPlayerBattleships()
    {
        return battleships;
    }
    public void setReceivedBattleships(int[] data)
    {
        for(int i=0; i<7;i++)
        {
            battleships[i].grid_id.x = data[3*i];
            battleships[i].grid_id.y = data[3*i+1];
            battleships[i].setRotation(data[3*i+2]);
        }
        setDataFromBattleshipsPosition();
    }
}
