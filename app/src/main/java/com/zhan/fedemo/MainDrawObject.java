package com.zhan.fedemo;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Point;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainDrawObject {
    public OnearrowObject onearrowObject;
    public LineModel lineModel;
    public Bitmap bitmap;
    public int type=0;  //0: bitmap,1: arrow path, 2: text, 3: first horizontoal line, 4: first text, 5: horizontal line, 6: veritical line 7: slash , 8: free line  9: horizontal arrow , 10: vertical arrow
    public float pos_x=0;
    public float posPy = 0;

    public String text;
    public Path path;

    public float start_x, start_y, end_x, end_y;

    public ArrayList<Point> points = new ArrayList<>();




    public MainDrawObject(Bitmap bitmap, int type, float pos_x, float pos_y) {
        this.bitmap = bitmap;
        this.type = type;
        this.pos_x=pos_x;
        this.posPy=pos_y;
    }

    public MainDrawObject(OnearrowObject onearrowObject, int i) {
        this.onearrowObject = onearrowObject;
        this.type = i;
    }
    public MainDrawObject(String text,Path path,int i, float pos_x, float pos_y) {
        this.text = text;
        this.type = i;
        this.path=path;
        this.pos_x=pos_x;
        this.posPy=pos_y;
    }

    public MainDrawObject(float start_x, float start_y, float end_x, float edn_y) {
        this.start_x = start_x;
        this.start_y = start_y;
        this.end_x = end_x;
        this.end_y = edn_y;
        this.type = 3;
    }

    public MainDrawObject(LineModel lineModel, int type) {
        this.lineModel = lineModel;
        this.type = type;
    }

    public MainDrawObject(ArrayList<Point> points){
        this.points=points;
        this.type=8;
    }
}
