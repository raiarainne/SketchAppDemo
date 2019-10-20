package com.zhan.fedemo;

import android.graphics.Path;

public class OnearrowObject {
    public Path arrow;
    public Path drawobject;
    public String midtext;
    public Float textOffsetX,textOffsetY;
    public Float startX, startY, endX, endY;
    public int type;  // 1: general arrow, 10: horizontal arrow, 11: vertical arrow

    public OnearrowObject(Path arrow, Path drawobject, String midtext, Float textOffsetX, Float textOffsetY, Float startX, float startY, float endX, float endY, int type) {
        this.arrow = arrow;
        this.drawobject = drawobject;
        this.midtext = midtext;
        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;

        this.startX=startX;
        this.startY=startY;
        this.endX=endX;
        this.endY=endY;

        this.type=type;
    }
}
