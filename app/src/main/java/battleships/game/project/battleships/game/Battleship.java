package battleships.game.project.battleships.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import static battleships.game.project.battleships.ui.Board.grid_rows;


public class Battleship
{

    int size = 1;
    int rotation = -90;
    public ID grid_id;
    Paint paint;
    int alpha = 126;
    public boolean useRedOverlay = false;
    int grids_found = 0;

    public Battleship(int size)
    {

        paint = new Paint();
        paint.setColor(Color.argb(alpha,0,0,255));
        paint.setFilterBitmap(true);
        grid_id = new ID();

        this.size = size;
    }



    public boolean checkIfPlaceIsGood(int[] data)
    {
        int a, x,y,l;
        if(rotation==0)
        {
            for(x=grid_id.x-1; x<grid_id.x+2; x++)
                for(y=grid_id.y-1; y<grid_id.y+size+1; y++)
                {
                    a = y*grid_rows+x;
                    if (x >= 0 && x< grid_rows && y<grid_rows && y>=0)
                        if(data[a]==1) {
                            useRedOverlay = true;
                            return false;
                        }
                }
            for(y=grid_id.y; y<grid_id.y+size;y++)
            {
                if (y < 0 || y>= grid_rows || grid_id.x>=grid_rows || grid_id.x<0)
                {
                    useRedOverlay = true;
                    return false;
                }
            }
        }
        else {
            for (x = grid_id.x - 1; x < grid_id.x + size+1; x++)
                for (y = grid_id.y - 1; y < grid_id.y + 2; y++) {
                    a = y * grid_rows + x;
                    if (x >= 0 && x< grid_rows && y<grid_rows && y>=0)
                        if (data[a] == 1) {
                            useRedOverlay = true;
                            return false;
                        }
                }
            for(x=grid_id.x; x<grid_id.x+size;x++)
            {
                if (x < 0 || x>= grid_rows || grid_id.y>=grid_rows || grid_id.y<0)
                {
                    useRedOverlay = true;
                    return false;
                }
            }
        }
        useRedOverlay = false;
        return true;
    }
    public boolean checkIfIsInBoardLimits()
    {
        int xx,yy,l;
        if(rotation==0)
            for(yy=grid_id.y; yy<grid_id.y+size;yy++)
            {
                l = grid_id.transformToInt(grid_id.x,yy);
                if(l<0 || l>= grid_rows*grid_rows)
                {
                    useRedOverlay = true;
                    return false;
                }
            }
        else
            for(xx=grid_id.x; xx<grid_id.x+size;xx++)
            {
                l = grid_id.transformToInt(xx,grid_id.y);
                if(l<0 || l>= grid_rows*grid_rows)
                {
                    useRedOverlay = true;
                    return false;
                }
            }
        return true;
    }
    private void checkIfGridIsOccupied(int[] data,boolean result, int xx, int yy)
    {
        if(yy>=0 && yy<grid_rows)
            if(data[yy*grid_rows+xx] == 1) {
                useRedOverlay = true;
                result = false;
            }

    }
    public void setDataFromGrid(int[] data)
    {
        int x = grid_id.x;
        int y = grid_id.y;
        if(rotation==0)
            for(int a=y; a<y+getSize(); a++)
            {
                int f = a*grid_rows+x;
                if(f>=0 && f<grid_rows*grid_rows)
                    data[f] = 1;
            }

        else
            for(int a=x; a<x+getSize(); a++) {
                int f = y*grid_rows+a;
                if(f>=0 && f<grid_rows*grid_rows)
                    data[f] = 1;
            }
    }
    public void clearDataFootprint(int[] data)
    {
        int i;
        if(rotation==0)
            for(i=grid_id.y; i<grid_id.y+size; i++)
                data[i*grid_rows+grid_id.x] = 0;
        else
            for(i=grid_id.x; i<grid_id.x+size; i++)
                data[grid_id.y*grid_rows+i] = 0;

    }
    public void showAdjacentWaterGrids(Canvas canvas, Bitmap point_bitmap,Paint bitmapPaint,
                                       Matrix matrix, float grid_step)
    {
        if(size==1)
        {
            drawRemainingVerticalWaterGrids(canvas,point_bitmap,bitmapPaint,matrix,grid_step);
            drawRemainingHorizontalWaterGrids(canvas,point_bitmap,bitmapPaint,matrix,grid_step);
        }
        else
        if(getRotation()==0)
            drawRemainingVerticalWaterGrids(canvas,point_bitmap,bitmapPaint,matrix,grid_step);
        else
            drawRemainingHorizontalWaterGrids(canvas,point_bitmap,bitmapPaint,matrix,grid_step);

    }
    private void drawRemainingHorizontalWaterGrids(Canvas canvas, Bitmap point_bitmap, Paint bitmapPaint,
                                 Matrix matrix, float grid_step)
    {

        for(int i=grid_id.x-1; i<grid_id.x+size+1; i++)
        {
            matrix.setTranslate(grid_step*i,grid_step*(grid_id.y-1));
            canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);
            matrix.setTranslate(grid_step*i,grid_step*(grid_id.y+1));
            canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);
        }
        matrix.setTranslate(grid_step*(grid_id.x-1),grid_step*grid_id.y);
        canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);
        matrix.setTranslate(grid_step*(grid_id.x+getSize()),
                grid_step* grid_id.y);
        canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);
    }
    private void drawRemainingVerticalWaterGrids(Canvas canvas, Bitmap point_bitmap, Paint bitmapPaint,
                                 Matrix matrix, float grid_step)
    {
        for(int i=grid_id.y-1; i<grid_id.y+size+1; i++)
        {
            matrix.setTranslate(grid_step*(grid_id.x-1),grid_step*i);
            canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);
            matrix.setTranslate(grid_step*(grid_id.x+1),grid_step*i);
            canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);
        }
        matrix.setTranslate(grid_step*grid_id.x,grid_step*(grid_id.y-1));
        canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);
        matrix.setTranslate(grid_step*grid_id.x,grid_step*
                (grid_id.y+size));
        canvas.drawBitmap(point_bitmap,matrix,bitmapPaint);

    }
    public boolean isSelfGrid(int x, int y)
    {

        int a;
        if(rotation==0) {
            for (a = grid_id.y; a < grid_id.y + size; a++)
                if (a == y && grid_id.x == x)
                    return true;
        }
        else
            for(a=grid_id.x; a<grid_id.x+size; a++)
                if(a==x && grid_id.y == y)
                    return true;
        return false;

    }
    public boolean isSelfGrid(ID id)
    {

        int a;
        if(rotation==0) {
            for (a = grid_id.y; a < grid_id.y + size; a++)
                if (a == id.y && grid_id.x == id.x)
                    return true;
        }
        else
            for(a=grid_id.x; a<grid_id.x+size; a++)
                if(a==id.x && grid_id.y == id.y)
                    return true;
        return false;

    }
    public void changeRotation()
    {
        if(rotation==-90)
            rotation= 0;
        else
            rotation = -90;
    }
    public void setGridId(int x, int y)
    {
        grid_id.x = x;
        grid_id.y = y;
    }
    public int getRotation() {
        return rotation;
    }
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
    public void setSize(int size)
    {
        this.size = size;
    }
    public int getSize() {return size;}
    public int getGrids_found() {
        return grids_found;
    }
    public void setGrids_found(int grid_found) {
        this.grids_found = grid_found;
    }
}
