package com.zhan.fedemo;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public class ArrowObj {
    CanvasView canvasView;
    float angle = 0.1f;

    float length = 0.1f;

    float endX, drag_endx;
    float endY, drag_endy;
    float startX, drag_startx;
    float startY, drag_starty;
    Path tempPath = new Path();

    public ArrowObj(CanvasView canvasView) {
        this.canvasView= canvasView;
    }

    public Path drawInitialLine() {
        Path path = new Path();
        path.moveTo(this.startX, this.startY);
        path.lineTo(this.endX, this.endY);
        path.computeBounds(new RectF(), true);
        this.tempPath = path;
        return path;
    }

    public void makeArraw(String midText, float length2, float angle2, int type, int add_or_replace) {

        float textoffestx=0, txtoffesty=0;

        Paint paint2 = new Paint(1);
        paint2.setTextSize(25.0f);
        paint2.setStyle(Style.FILL);
        float textwidth = paint2.measureText(midText);
        float offset = (length2 - textwidth) / 2.0f;

        RectF bounds = new RectF();
        Log.d("screen==", String.valueOf(canvasView.screenW));

        this.tempPath.computeBounds(bounds, true);
        this.startX = bounds.centerX() - (length2 / 2.0f);
        this.startY = bounds.centerY();

        this.endX = bounds.centerX() + (length2 / 2.0f);
        this.endY = bounds.centerY();
        Path mainPath = new Path();
        Path path = new Path();
        path.moveTo(this.startX, this.startY);
        path.lineTo(this.startX + 20.0f, this.startY - 5.0f);
        path.lineTo(this.startX + 20.0f, this.startY + 5.0f);
        path.lineTo(this.startX, this.startY);
        path.close();
        path.moveTo(this.startX + length2, this.startY);
        path.lineTo((this.startX - 20.0f) + length2, this.startY - 5.0f);
        path.lineTo((this.startX - 20.0f) + length2, this.startY + 5.0f);
        path.lineTo(this.startX + length2, this.startY);
        path.close();
        if (textwidth >= length2 - 50.0f) {
            mainPath.moveTo(bounds.centerX() - (textwidth / 2.0f), this.startY);
            mainPath.lineTo(bounds.centerX() + (textwidth / 2.0f), this.endY);
            mainPath.close();
            path.moveTo(this.startX + 5.0f, this.startY);
            path.lineTo(this.endX - 5.0f, this.endY);
            path.close();
            txtoffesty = Float.valueOf(-15.0f);
            textoffestx=Float.valueOf(0.0f);
        } else {
            mainPath.moveTo(this.startX, this.startY);
            mainPath.lineTo(this.endX, this.endY);
            mainPath.close();
            path.moveTo(this.startX + 5.0f, this.startY);
            path.lineTo((this.startX + offset) - 10.0f, this.startY);
            path.close();
            path.moveTo((this.startX + length2) - 5.0f, this.startY);
            path.lineTo(((this.startX + length2) - offset) + 10.0f, this.startY);
            path.close();
            textoffestx= Float.valueOf(offset);
            txtoffesty=Float.valueOf(6.0f);
        }

       Log.d("angle===", String.valueOf(angle2));
        if(angle2>=90 && angle2<180) angle2=angle2+180;
        else if(angle2>=180 && angle2<270) angle2=angle2-180;
        //else if(angle2>=270 && angle2<360) angle2=angle2+180;

        Matrix lMatrix = new Matrix();
        lMatrix.postRotate(-angle2, bounds.centerX(), bounds.centerY());
        mainPath.transform(lMatrix);



        Matrix mMatrix = new Matrix();
        path.computeBounds(new RectF(), true);
        mMatrix.postRotate(-angle2, bounds.centerX(), bounds.centerY());
        path.transform(mMatrix);

        OnearrowObject onearrowObject = new OnearrowObject(path,mainPath,midText,textoffestx, txtoffesty, drag_startx, drag_starty, drag_endx, drag_endy, type);
        MainDrawObject mainDrawObject= new MainDrawObject(onearrowObject,1);
        if(add_or_replace==0)
            canvasView.mainDrawObjects.add(mainDrawObject);
        else if(add_or_replace==1)
            canvasView.mainDrawObjects.set(canvasView.mainDrawObjects.size()-1, mainDrawObject);
        this.startX = 0.0f;
        this.startY = 0.0f;
        this.endX = 0.0f;
        this.endY = 0.0f;
    }

    public void putInputs() {
        this.drag_startx=startX;
        this.drag_starty=startY;
        this.drag_endx=endX;
        this.drag_endy=endY;
        this.length = (float) Math.sqrt((double) (((this.endX - this.startX) * (this.endX - this.startX)) + ((this.endY - this.startY) * (this.endY - this.startY))));
        this.angle = calculateAngle(this.endX, this.endY);
    }



    private float calculateAngle(float xpos, float ypos) {
        double angle2 = 0.0d;
        if (xpos >= this.startX && ypos <= this.startY) {
            angle2 = Math.toDegrees(Math.atan((double) ((this.startY - ypos) / (xpos - this.startX))));
        } else if (xpos < this.startX && ypos <= this.startY) {
            angle2 = Math.toDegrees(Math.atan((double) ((this.startY - ypos) / (xpos - this.startX))) + 3.141592653589793d);
        } else if (xpos <= this.startX && ypos >= this.startY) {
            angle2 = Math.toDegrees(Math.atan((double) ((this.startY - ypos) / (xpos - this.startX))) + 3.141592653589793d);
        } else if (xpos >= this.startX && ypos > this.startY) {
            angle2 = Math.toDegrees(Math.atan((double) ((this.startY - ypos) / (xpos - this.startX))) + 6.283185307179586d);
        }
        this.angle = (float) angle2;
        return (float) angle2;
    }

}
