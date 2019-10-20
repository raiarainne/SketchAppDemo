package com.zhan.fedemo;

public class LineModel {
    public float line_start_x;
    public float line_start_y;
    public float line_end_x;
    public float line_end_y;
    public float angle;


    public LineModel(float line_start_x, float line_start_y, float line_end_x, float line_end_y) {
        this.line_start_x = line_start_x;
        this.line_start_y = line_start_y;
        this.line_end_x = line_end_x;
        this.line_end_y = line_end_y;

    }

    public LineModel(float line_start_x, float line_start_y, float line_end_x, float line_end_y, float angle) {
        this.line_start_x = line_start_x;
        this.line_start_y = line_start_y;
        this.line_end_x = line_end_x;
        this.line_end_y = line_end_y;
        this.angle = angle;
    }
}
