package com.zhan.fedemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class CanvasView extends View {
    ArrowObj arraow;
    Paint paint = new Paint(1);
    Paint paint2;
    float screenH;
    float screenW;
    public ArrayList<MainDrawObject> mainDrawObjects= new ArrayList<>();
    public ArrayList<MainDrawObject> history_mainDrawObjects= new ArrayList<>();
    float x=0,y=0;
    Bitmap myBitmap;
    Bitmap first_handle, second_handle, middle_handle;


    //=================== For bitmap zoom in out=====================
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
// we can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int zoomorrotation_mode = NONE;
// remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private Integer[]bockicons = new Integer[]{R.drawable.brick, R.drawable.concrete, R.drawable.masonry, R.drawable.steel, R.drawable.stone,R.drawable.timber, R.drawable.others};
    private Integer[]other = new Integer[]{R.drawable.horizonal, R.drawable.vertical, R.drawable.slash, R.drawable.free, R.drawable.hori_measure,R.drawable.vertical_measure, R.drawable.freedimen, R.drawable.text};
    private String[]blocktexts = new String[]{"Brick", "Concrete", "Masonry", "Steel", "Stone","Timber", "Others"};

    Dialog accepteddialog,addtextdialog;

    //========== For Text change =================
    float positionstart_x, positionstart_y;
    MainDrawObject drawtext;
    Boolean text_dragstatus = true;

    //========= For Line draw ========================
    int pickedpositionstatus = -1;  // 0: start picked , 1: end picked , 2: middle picked

    //============== For free line===============
    ArrayList<Point> points = new ArrayList<>();

    //=============== For Arrow Lines==============
    float old_x, old_y;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint.setStyle(Style.FILL_AND_STROKE);
        this.paint.setColor(-16777216);
        this.paint.setStrokeWidth(1.0f);
        this.paint2 = new Paint(1);
        this.paint2.setTextSize(25.0f);
        this.paint2.setStyle(Style.FILL);
        this.arraow = new ArrowObj(this);
        setWillNotDraw(false);
    }

    public void updateta(){
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        int metaState = 0;
        MotionEvent motionEvent2 = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_DOWN,
                x,
                y,
                metaState
        );
        dispatchTouchEvent(motionEvent2);
        invalidate();
    }
    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("hellworld==cav="+String.valueOf(this.mainDrawObjects.size()));
        canvas.drawPath(new Path(), this.paint2);

        for (int i = 0; i < this.mainDrawObjects.size(); i++) {
            MainDrawObject mainDrawObject_item = mainDrawObjects.get(i);
            switch (mainDrawObject_item.type){
                case 0:  // add block image
                    Bitmap bitmap = mainDrawObject_item.bitmap;
                    System.out.println("image");
                    canvas.drawBitmap(bitmap, (float) mainDrawObject_item.pos_x-bitmap.getWidth()/2, mainDrawObject_item.posPy-bitmap.getHeight()/2, null);
                    if(i==mainDrawObjects.size()-1) {
                        Constant.mode=0;
                    }
                    break;
                case 1:  // draw arrow
                    OnearrowObject onearrowObject= mainDrawObject_item.onearrowObject;
                    System.out.println("arrow");
                    canvas.drawPath((Path) onearrowObject.arrow, this.paint);
                    canvas.drawTextOnPath((String) onearrowObject.midtext, (Path) onearrowObject.drawobject, ((Float) onearrowObject.textOffsetX).floatValue(), ((Float) onearrowObject.textOffsetY).floatValue(), this.paint2);

                    if(i==mainDrawObjects.size()-1) {
                        Constant.mode=-2;
                        Log.d("hadle==", "handle");
                        Log.d(String.valueOf(onearrowObject.startX)+"===", String.valueOf(onearrowObject.startY));
                        Log.d(String.valueOf(onearrowObject.endX)+"===", String.valueOf(onearrowObject.endY));
                        first_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        second_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        middle_handle = BitmapFactory.decodeResource(getResources(), R.drawable.move);
                        canvas.drawBitmap(first_handle, (float) onearrowObject.startX - first_handle.getWidth() / 2, onearrowObject.startY - first_handle.getHeight() / 2, null);
                        canvas.drawBitmap(second_handle, (float) onearrowObject.endX - first_handle.getWidth() / 2, onearrowObject.endY - second_handle.getHeight() / 2, null);
                        canvas.drawBitmap(middle_handle, (float) getmiddle_x(onearrowObject.startX, onearrowObject.endX) - middle_handle.getWidth() / 2, getmiddle_y(onearrowObject.startY, onearrowObject.endY) - middle_handle.getHeight() / 2, null);
                    }

                    break;
                case 2:  // write text
                    this.paint2.setTextSize(30.0f);
                    System.out.println("text");
                    canvas.drawTextOnPath(mainDrawObject_item.text, (Path) mainDrawObject_item.path, ((Float) mainDrawObject_item.pos_x).floatValue(), ((Float) mainDrawObject_item.posPy).floatValue(), this.paint2);

                    if(i==mainDrawObjects.size()-1) drawtext=mainDrawObject_item;
                    break;
                case 3:  // draw first horizontal line
                    canvas.drawLine(mainDrawObject_item.start_x, mainDrawObject_item.start_y, mainDrawObject_item.end_x, mainDrawObject_item.end_y, paint2);
                    break;
                case 4:  // draw GL text
                    this.paint2.setTextSize(30.0f);
                    System.out.println("text");
                    canvas.drawTextOnPath(mainDrawObject_item.text, (Path) mainDrawObject_item.path, ((Float) mainDrawObject_item.pos_x).floatValue(), ((Float) mainDrawObject_item.posPy).floatValue(), this.paint2);
                    break;
                case 5:  // Horizontal Line
                    LineModel lineModel= mainDrawObject_item.lineModel;
                    canvas.drawLine(lineModel.line_start_x, lineModel.line_start_y, lineModel.line_end_x, lineModel.line_end_y, paint2);
                    if(i==mainDrawObjects.size()-1){
                        first_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        second_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        middle_handle = BitmapFactory.decodeResource(getResources(), R.drawable.move);
                        canvas.drawBitmap(first_handle, (float) lineModel.line_start_x-first_handle.getWidth()/2, lineModel.line_start_y-first_handle.getHeight()/2, null);
                        canvas.drawBitmap(second_handle, (float) lineModel.line_end_x-first_handle.getWidth()/2, lineModel.line_end_y-second_handle.getHeight()/2, null);
                        canvas.drawBitmap(middle_handle, getmiddle_x(lineModel.line_start_x, lineModel.line_end_x)-middle_handle.getWidth()/2, lineModel.line_start_y-middle_handle.getHeight()/2, null);
                        Constant.mode=5;
                    }
                    break;
                case 6:  // Horizontal Line
                    LineModel lineModel1= mainDrawObject_item.lineModel;
                    canvas.drawLine(lineModel1.line_start_x, lineModel1.line_start_y, lineModel1.line_end_x, lineModel1.line_end_y, paint2);
                    if(i==mainDrawObjects.size()-1){
                        first_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        second_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        middle_handle = BitmapFactory.decodeResource(getResources(), R.drawable.move);
                        canvas.drawBitmap(first_handle, (float) lineModel1.line_start_x-first_handle.getWidth()/2, lineModel1.line_start_y-first_handle.getHeight()/2, null);
                        canvas.drawBitmap(second_handle, (float) lineModel1.line_end_x-first_handle.getWidth()/2, lineModel1.line_end_y-second_handle.getHeight()/2, null);
                        canvas.drawBitmap(middle_handle, (float) lineModel1.line_start_x-middle_handle.getWidth()/2, getmiddle_y(lineModel1.line_start_y, lineModel1.line_end_y)-middle_handle.getHeight()/2, null);
                        Constant.mode=6;
                    }
                    break;
                case 7:  // Slash Line
                    LineModel lineModel2= mainDrawObject_item.lineModel;
                    canvas.drawLine(lineModel2.line_start_x, lineModel2.line_start_y, lineModel2.line_end_x, lineModel2.line_end_y, paint2);
                    if(i==mainDrawObjects.size()-1){
                        first_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        second_handle = BitmapFactory.decodeResource(getResources(), R.drawable.handleicon);
                        middle_handle = BitmapFactory.decodeResource(getResources(), R.drawable.move);
                        canvas.drawBitmap(first_handle, (float) lineModel2.line_start_x-first_handle.getWidth()/2, lineModel2.line_start_y-first_handle.getHeight()/2, null);
                        //canvas.drawBitmap(second_handle, (float) lineModel2.line_end_x-first_handle.getWidth()/2, lineModel2.line_end_y-second_handle.getHeight()/2, null);
                        canvas.drawBitmap(middle_handle, (float) getmiddle_x(lineModel2.line_start_x, lineModel2.line_end_x)-middle_handle.getWidth()/2, getmiddle_y(lineModel2.line_start_y, lineModel2.line_end_y)-middle_handle.getHeight()/2, null);
                        Constant.mode=7;
                    }
                    break;
                case 8:    // free line path
                    Path path = new Path();
                    boolean first = true;
                    for(int i1 = 0; i1 < mainDrawObjects.get(mainDrawObjects.size()-1).points.size(); i1 += 2){
                        Point point = points.get(i1);
                        if(first){
                            first = false;
                            path.moveTo(point.x, point.y);
                        }

                        else if(i1 < points.size() - 1){
                            Point next = points.get(i1 + 1);
                            path.quadTo(point.x, point.y, next.x, next.y);
                        }
                        else{
                            path.lineTo(point.x, point.y);
                        }
                    }

                    canvas.drawPath(path, paint2);
                    if(i==mainDrawObjects.size()-1) {
                        points =  mainDrawObjects.get(mainDrawObjects.size()-1).points;
                        Constant.mode=8;
                    }
                    break;
                    default:
                        break;
            }

        }
        canvas.drawPath(this.arraow.drawInitialLine(), this.paint);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenH=h;
        screenW=w;
        Log.d("width ==changed=", String.valueOf(screenW));
        this.x=this.screenW/2;
        this.y=this.screenH/2;

        MainDrawObject mainDrawObject = new MainDrawObject(0,screenH/2, screenW, screenH/2);
        mainDrawObjects.add(mainDrawObject);

        float  x1=CanvasView.this.screenW/2.0f; float y1=CanvasView.this.screenH/2.0f;
        Path path1 = new Path();
        path1.moveTo(0, 0);
        path1.lineTo(screenW, 0);
        path1.close();
        MainDrawObject drawObject= new MainDrawObject("GL",path1,4, 50, y1-20);
        mainDrawObjects.add(drawObject);

        CanvasView.this.invalidate();

    }


    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("hellworld=ca=="+String.valueOf(this.mainDrawObjects.size()));

        ((MainActivity) getContext()).undobutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(mainDrawObjects.size()==2){
                    Toast.makeText(getContext(), "NO back", Toast.LENGTH_SHORT).show();
                }else if(mainDrawObjects.size()>2){
                    initialzoom();
                    history_mainDrawObjects.add(mainDrawObjects.get(mainDrawObjects.size()-1));
                    mainDrawObjects.remove(mainDrawObjects.size()-1);
                    drawtext = null;
                    if(mainDrawObjects.get(mainDrawObjects.size()-1).type==0)
                        myBitmap=mainDrawObjects.get(mainDrawObjects.size()-1).bitmap;
                    else if(mainDrawObjects.get(mainDrawObjects.size()-1).type==2)
                        drawtext=mainDrawObjects.get(mainDrawObjects.size()-1);
                }
                CanvasView.this.invalidate();
            }
        });
        ((MainActivity) getContext()).btn_redo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(history_mainDrawObjects.size()==0){
                    Toast.makeText(getContext(), "NO redo", Toast.LENGTH_SHORT).show();
                }else if(mainDrawObjects.size()>0){
                    initialzoom();
                    mainDrawObjects.add(history_mainDrawObjects.get(history_mainDrawObjects.size()-1));
                    history_mainDrawObjects.remove(history_mainDrawObjects.size()-1);
                    drawtext = null;
                    if(mainDrawObjects.get(mainDrawObjects.size()-1).type==0)
                        myBitmap=mainDrawObjects.get(mainDrawObjects.size()-1).bitmap;
                    else if(mainDrawObjects.get(mainDrawObjects.size()-1).type==2)
                        drawtext=mainDrawObjects.get(mainDrawObjects.size()-1);
                }
                CanvasView.this.invalidate();
            }
        });
        ((MainActivity) getContext()).btn_gl.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                addtextdialog(0);
            }
        });
        ((MainActivity) getContext()).btn_other.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                selectpdftypedialog(1);
                CanvasView.this.invalidate();
            }
        });
        ((MainActivity) getContext()).blockbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                selectpdftypedialog(0);
                CanvasView.this.invalidate();
            }
        });


        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: //  0:
                Log.d("actiondown===","actiondown");
                if(Constant.mode==1) { // add arrow
                    this.arraow.startX = event.getX();
                    this.arraow.startY = event.getY();
                }else if(Constant.mode==0){ // for blocks
                    savedMatrix.set(matrix);
                    start.set(event.getX(), event.getY());
                    zoomorrotation_mode = DRAG;
                    lastEvent = null;
                }else if(Constant.mode==2){  // for text
                    float dist_x=event.getX()-drawtext.pos_x;
                    float dist_y = event.getY() - drawtext.posPy;
                    if(dist_x<10 && dist_y<10) text_dragstatus=true;
                }else if(Constant.mode==5 || Constant.mode==6 || Constant.mode==7){  // horizontal line or veritical line
                    pickedpositionstatus = getpickedstatus(event.getX(), event.getY());
                    positionstart_x=event.getX();
                    positionstart_y=event.getY();
                }else if(Constant.mode==8){  // free line
                    points= new ArrayList<>();
                    points.add(new Point((int)event.getX(), (int)event.getY()));
                }else if(Constant.mode==-2){  // arrow drag mode
                    pickedpositionstatus = getpickedstatus_forarrow(event.getX(), event.getY());
                    if(pickedpositionstatus==2){
                        old_x = event.getX();
                        old_y=event.getY();
                    }
                }
                break;
            case  MotionEvent.ACTION_UP:   //1:
                Log.d("actionup===","actionup");
                if(Constant.mode==1) {  // add arrow
                    ((MainActivity) getContext()).arrowPnl.setVisibility(VISIBLE);
                    setInputs();
                    ((MainActivity) getContext()).okButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            CanvasView.this.getInputs(0,0);
                            CanvasView.this.invalidate();
                            ((MainActivity) CanvasView.this.getContext()).arrowPnl.setVisibility(GONE);
                        }
                    });
                    ((MainActivity) getContext()).cancelbutton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                           // CanvasView.this.getInputs();
                            arraow.startX=0;
                            arraow.startY=0;
                            arraow.endX=0;
                            arraow.endY=0;
                            CanvasView.this.invalidate();
                            Constant.mode=-1;
                            ((MainActivity) CanvasView.this.getContext()).arrowPnl.setVisibility(GONE);
                        }
                    });
                }else if(Constant.mode==2){ // add text
                   float x_dist = positionstart_x-drawtext.pos_x+paint2.measureText(drawtext.text);
                   float y_dist = positionstart_y-drawtext.posPy+paint2.measureText(drawtext.text);
                   if(x_dist<30 && y_dist<30){
                       positionstart_x=event.getX();
                       positionstart_y=event.getY();
                   }

                    if(text_dragstatus== true){
                        Path path1 = new Path();
                        path1.moveTo(0, 0);
                        path1.lineTo(screenW, 0);


                        if (lastEvent != null && event.getPointerCount() == 2) {
                            path1 = new Path();
                            path1.moveTo(lastEvent[0], lastEvent[2]);
                            path1.lineTo(lastEvent[1], lastEvent[3]);
                        }

                        path1.close();
                        MainDrawObject drawObject= new MainDrawObject(drawtext.text,path1,2, event.getX(), event.getY());
                        mainDrawObjects.set(mainDrawObjects.size()-1, drawObject);
                        CanvasView.this.invalidate();
                    }
                }else if(Constant.mode==8){
                   // mainDrawObjects.add(new MainDrawObject(freelinepath));
                    //freelinepath.reset();
                   // invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE: //2:
                if(Constant.mode==1) {
                    this.arraow.endX = event.getX();
                    this.arraow.endY = event.getY();
                    invalidate();
                }
                else if(Constant.mode==0){
                    x=(int)event.getX();
                    y=(int)event.getY();
                    mainDrawObjects.get(mainDrawObjects.size()-1).pos_x=x;
                    mainDrawObjects.get(mainDrawObjects.size()-1).posPy=y;

                    //========== For two finger =========
                    if (zoomorrotation_mode == DRAG) {
                        matrix.set(savedMatrix);
                        float dx = event.getX() - start.x;
                        float dy = event.getY() - start.y;
                        matrix.postTranslate(dx, dy);

                    }
                    else if (zoomorrotation_mode == ZOOM) {
                        float newDist = spacing(event);

                        if (newDist > 10f) {
                            matrix.set(savedMatrix);
                            float scale = (newDist / oldDist);
                            matrix.postScale(scale, scale, mid.x, mid.y);
                        }

                        if (lastEvent != null && event.getPointerCount() == 3) {
                        //if (lastEvent != null && event.getPointerCount() == 2) {
                            newRot = rotation(event);
                            float r = newRot - d;
                            float[] values = new float[9];
                            matrix.getValues(values);
                            float tx = values[2];
                            float ty = values[5];
                            float sx = values[0];
                            float xc = (screenW/ 2) * sx;
                            float yc = (screenH / 2) * sx;
                            Log.d("values==", String.valueOf(newRot)+"-"+String.valueOf(d)+"-"+String.valueOf(tx));
                            Log.d("values==", String.valueOf(ty)+"-"+String.valueOf(xc)+"-"+String.valueOf(yc));
                            matrix.postRotate(r, tx + xc, ty + yc);
                        }
                    }
                    Bitmap resizedBitmap = Bitmap.createBitmap(myBitmap, 0, 0,
                    myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
                    mainDrawObjects.get(mainDrawObjects.size()-1).bitmap=resizedBitmap;

                    invalidate();
                }else if(Constant.mode==2){
                    if(text_dragstatus== true){
                        Path path1 = new Path();
                        path1.moveTo(0, 0);
                        path1.lineTo(screenW, 0);
                        if (lastEvent != null && event.getPointerCount() == 2) {
                            path1 = new Path();
                            path1.moveTo(lastEvent[0], lastEvent[2]);
                            path1.lineTo(lastEvent[1], lastEvent[3]);
                        }
                        path1.close();
                        MainDrawObject drawObject= new MainDrawObject(drawtext.text,path1,2, event.getX(), event.getY());
                        mainDrawObjects.set(mainDrawObjects.size()-1, drawObject);
                        CanvasView.this.invalidate();
                    }
                }else if(Constant.mode==5){
                    MainDrawObject mainDrawObject= mainDrawObjects.get(mainDrawObjects.size()-1);
                    if(pickedpositionstatus==0){  // start dot
                        mainDrawObject.lineModel.line_start_x=event.getX();
                    }else if(pickedpositionstatus==1) { // end dot
                        mainDrawObject.lineModel.line_end_x=event.getX();
                    }else if(pickedpositionstatus==2){ // middle dot
                        float x_move = positionstart_x-event.getX();
                        float y_move = positionstart_y-event.getY();
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_x=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_x - x_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_y=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_y - y_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_x=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_x - x_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_y=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_y - y_move;
                        positionstart_x=event.getX();
                        positionstart_y=event.getY();
                    }

                    invalidate();
                }else if(Constant.mode==6){
                    MainDrawObject mainDrawObject= mainDrawObjects.get(mainDrawObjects.size()-1);
                    if(pickedpositionstatus==0){  // start dot
                        mainDrawObject.lineModel.line_start_y=event.getY();
                    }else if(pickedpositionstatus==1) { // end dot
                        mainDrawObject.lineModel.line_end_y=event.getY();
                    }else if(pickedpositionstatus==2){ // middle dot
                        float x_move = positionstart_x-event.getX();
                        float y_move = positionstart_y-event.getY();
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_x=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_x - x_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_y=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_y - y_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_x=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_x - x_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_y=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_y - y_move;
                        positionstart_x=event.getX();
                        positionstart_y=event.getY();
                    }

                    invalidate();
                }else if(Constant.mode==7){
                    MainDrawObject mainDrawObject= mainDrawObjects.get(mainDrawObjects.size()-1);
                    if(pickedpositionstatus==0){  // start dot
                        Log.d("start==","start");
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_y=event.getY();
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_x=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_x +  (mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_y-event.getY())/(float) Math.tan(Math.toRadians(mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.angle));

                    }else if(pickedpositionstatus==2){ // middle dot
                        float x_move = positionstart_x-event.getX();
                        float y_move = positionstart_y-event.getY();
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_x=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_x - x_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_y=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_start_y - y_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_x=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_x - x_move;
                        mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_y=mainDrawObjects.get(mainDrawObjects.size()-1).lineModel.line_end_y - y_move;
                        positionstart_x=event.getX();
                        positionstart_y=event.getY();
                    }

                    invalidate();
                }else if(Constant.mode==8){
                    //freelinepath.lineTo(event.getX(), event.getY());
                    points.add(new Point((int)event.getX(), (int)event.getY()));
                    invalidate();
                }else if(Constant.mode==-2){  // drag arrow mode
                    ((MainActivity) getContext()).textInput.setText(mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.midtext);
                    if(pickedpositionstatus==0){  // start arrow
                        arraow.startX=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.startX;
                        arraow.endX=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.endX;
                        arraow.startY=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.startY;
                        arraow.endY=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.endY;

                        final int arrowtype = mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.type;  // 1: general, 10: horizontal arrow , 12: vertical arrow
                        if(arrowtype == 1){
                            arraow.startX=event.getX();
                            arraow.startY=event.getY();
                        }
                        else if(arrowtype == 10){
                            arraow.startX=event.getX();
                        }else if(arrowtype == 11){
                            arraow.startY=event.getY();
                        }
                        arraow.drawInitialLine();
                        invalidate();
                        setInputs();
                        CanvasView.this.getInputs(arrowtype, 1);
                        CanvasView.this.invalidate();
                    }else if(pickedpositionstatus==1){  // end arrow
                        arraow.startX=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.startX;
                        arraow.endX=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.endX;
                        arraow.startY=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.startY;
                        arraow.endY=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.endY;

                        final int arrowtype = mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.type;  // 1: general, 10: horizontal arrow , 12: vertical arrow
                        if(arrowtype == 1){
                            arraow.endX=event.getX();
                            arraow.endY=event.getY();
                        }
                        else if(arrowtype == 10){
                            arraow.endX=event.getX();
                        }else if(arrowtype == 11){
                            arraow.endY=event.getY();
                        }
                        arraow.drawInitialLine();
                        invalidate();
                        setInputs();
                        CanvasView.this.getInputs(arrowtype, 1);
                        CanvasView.this.invalidate();
                    }else if(pickedpositionstatus==2){  // middle
                        arraow.startX=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.startX;
                        arraow.endX=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.endX;
                        arraow.startY=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.startY;
                        arraow.endY=mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.endY;

                        final int arrowtype = mainDrawObjects.get(mainDrawObjects.size()-1).onearrowObject.type;  // 1: general, 10: horizontal arrow , 12: vertical arrow
                        arraow.startX=arraow.startX - (old_x-event.getX());
                        arraow.endX=arraow.endX - (old_x-event.getX());
                        arraow.startY=arraow.startY - (old_y-event.getY());
                        arraow.endY=arraow.endY - (old_y-event.getY());


                        arraow.drawInitialLine();
                        invalidate();
                        setInputs();
                        CanvasView.this.getInputs(arrowtype, 1);
                        CanvasView.this.invalidate();

                        old_x=event.getX();
                        old_y= event.getY();

                    }
                }
                break;

                //=================== For two finger with bitmap ==========
                case MotionEvent.ACTION_POINTER_DOWN:
                    if(Constant.mode==0){
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            zoomorrotation_mode = ZOOM;
                        }

                        lastEvent = new float[4];
                        lastEvent[0] = event.getX(0);
                        lastEvent[1] = event.getX(1);
                        lastEvent[2] = event.getY(0);
                        lastEvent[3] = event.getY(1);
                        d = rotation(event);
                    }else if(Constant.mode==2){
                        lastEvent = new float[4];
                        lastEvent[0] = event.getX(0);
                        lastEvent[1] = event.getX(1);
                        lastEvent[2] = event.getY(0);
                        lastEvent[3] = event.getY(1);


                        Path path1 = new Path();

                        path1.moveTo(lastEvent[2], lastEvent[3]);
                        path1.lineTo(lastEvent[0], lastEvent[1]);
                        path1.close();

                        MainDrawObject drawObject= new MainDrawObject(drawtext.text,path1,2, event.getX(), event.getY());
                        mainDrawObjects.set(mainDrawObjects.size()-1, drawObject);
                        CanvasView.this.invalidate();
                    }
                break;
                case MotionEvent.ACTION_POINTER_UP:
                    if(Constant.mode==0) {
                        zoomorrotation_mode = NONE;
                        lastEvent = null;
                    }else if(Constant.mode==2){
                        text_dragstatus=false;
                    }
                break;
            }
            return true;
        }



    public void setInputs() {
        this.arraow.putInputs();
        ((MainActivity) getContext()).lengthInput.setText(String.format("%.02f", new Object[]{Float.valueOf(this.arraow.length)}));
        ((MainActivity) getContext()).AngleInput.setText(String.format("%.02f", new Object[]{Float.valueOf(this.arraow.angle)}));
    }

    public void getInputs(int type, int add_or_replace) {  // add or replace; 0: add, 1: replace
        this.arraow.makeArraw(((MainActivity) getContext()).textInput.getText().toString(), Float.valueOf(((MainActivity) getContext()).lengthInput.getText().toString().trim()).floatValue(), Float.valueOf(((MainActivity) getContext()).AngleInput.getText().toString().trim()).floatValue(), type, add_or_replace);
    }


////========================
    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }

    private void midPoint(PointF point, MotionEvent event) {

        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        Log.d("roation===", String.valueOf((float) Math.toDegrees(radians)));
        return (float) Math.toDegrees(radians);

    }

    private void initialzoom(){
         matrix = new Matrix();
         savedMatrix = new Matrix();
         zoomorrotation_mode = NONE;
         start = new PointF();
         mid = new PointF();
         oldDist = 1f;
         d = 0f;
         newRot = 0f;
         lastEvent = null;
    }

    private void addimage(int icon){
        initialzoom();
        x=CanvasView.this.screenW/2; y=CanvasView.this.screenH/2;
        Log.d("width==can==", String.valueOf(CanvasView.this.screenW));
        myBitmap = BitmapFactory.decodeResource(getResources(), icon);
        MainDrawObject drawObject= new MainDrawObject(myBitmap,0, x, y);
        mainDrawObjects.add(drawObject);
        CanvasView.this.invalidate();
    }
    private void addText(String text){
        Constant.mode=2;
        float  x1=CanvasView.this.screenW/2.0f; float y1=CanvasView.this.screenH/2.0f;

        Path path1 = new Path();
        path1.moveTo(0, 0);
        path1.lineTo(screenW, 0);
        path1.close();

        MainDrawObject drawObject= new MainDrawObject(text,path1,2, x1-paint2.measureText(text)/2, y1);
        mainDrawObjects.add(drawObject);
        CanvasView.this.invalidate();
    }

    private void drawlilne(int type, float startx, float endx, float starty, float endy, float angle ){ // 5: horizontal, 6: vertical
        Constant.mode=type;
        MainDrawObject mainDrawObject = new MainDrawObject(new LineModel(startx, starty, endx, endy, angle), type);
        mainDrawObjects.add(mainDrawObject);
        invalidate();
    }

    public void addslash(){  // 0: gl , 1: general text

        final EditText etxtext;
        Button btnadd;
        addtextdialog = new Dialog((MainActivity)getContext());
        addtextdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addtextdialog.setContentView(R.layout.add_text);
        addtextdialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addtextdialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        etxtext=(EditText)addtextdialog.findViewById(R.id.etx_text);
        btnadd=(Button)addtextdialog.findViewById(R.id.addtext);
        btnadd.setText("Add Slash");
        etxtext.setHint("Please enter angle");
        etxtext.setInputType(InputType.TYPE_CLASS_NUMBER);


        btnadd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etxtext.getText().toString().length()==0){
                    Toast.makeText((MainActivity)getContext(), "Please enter a text", Toast.LENGTH_SHORT).show();
                }else{
                    Constant.mode=7;
                    float[] linecoordinate = getcoordinate(screenW/2, screenH/2, Float.parseFloat(etxtext.getText().toString().trim()));
                    drawlilne(7, linecoordinate[0], linecoordinate[1], linecoordinate[2], linecoordinate[3],Float.parseFloat(etxtext.getText().toString().trim()) );
                    addtextdialog.dismiss();
                }

            }
        });
        addtextdialog.show();
    }

    public void addtextdialog(final int type){  // 0: gl , 1: general text
        if(type==1)        Constant.mode=2;

        final EditText etxtext;
        Button btnadd;
        addtextdialog = new Dialog((MainActivity)getContext());
        addtextdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addtextdialog.setContentView(R.layout.add_text);
        addtextdialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addtextdialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        etxtext=(EditText)addtextdialog.findViewById(R.id.etx_text);
        btnadd=(Button)addtextdialog.findViewById(R.id.addtext);
        if(type==0) {
            btnadd.setText("Update GL Text");
            etxtext.setText("GL");
        }
        btnadd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(type==1){
                    if(etxtext.getText().toString().length()==0){
                        Toast.makeText((MainActivity)getContext(), "Please enter a text", Toast.LENGTH_SHORT).show();
                    }else{
                        addText(etxtext.getText().toString());
                        addtextdialog.dismiss();
                    }
                }else if(type==0){
                    mainDrawObjects.get(1).text=etxtext.getText().toString();
                    invalidate();
                    addtextdialog.dismiss();
                }
            }
        });
        addtextdialog.show();
    }

    public void addstatic_arrow(final int type){  // 10: horizontal arrow, 11: vertical arrow
        //this.arraow = new ArrowObj(this);
        if(type == 1){
            arraow.startX=screenW/2-150;
            arraow.endX = screenW/2+150;
            arraow.startY=screenH/2-150;
            arraow.endY=screenH/2+150;

        }
        else if(type == 10){
            Log.d("type===", "10");
            Log.d("type===", String.valueOf(screenW));
            Log.d("type===", String.valueOf(screenH));

            arraow.startX=screenW/2-150;
            arraow.endX = screenW/2+150;
            arraow.startY=screenH/2+100;
            arraow.endY=screenH/2+100;

        }else if(type == 11){
            arraow.startX=screenW/2;
            arraow.endX = screenW/2;
            arraow.startY=screenH/2-150;
            arraow.endY=screenH/2+150;
        }

        ((MainActivity) getContext()).arrowPnl.setVisibility(VISIBLE);
        setInputs();
        ((MainActivity) getContext()).okButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CanvasView.this.getInputs(type,0);
                CanvasView.this.invalidate();
                ((MainActivity) CanvasView.this.getContext()).arrowPnl.setVisibility(GONE);
            }
        });
        invalidate();


    }

    public void selectpdftypedialog(int type){  // 0: block, 1: other
       // Constant.mode=0;
        final GridView gridView ;
        TextView txttitle;
        accepteddialog = new Dialog((MainActivity)getContext());
        accepteddialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        accepteddialog.setContentView(R.layout.selectimagedialog);
        accepteddialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        accepteddialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        txttitle=(TextView)accepteddialog.findViewById(R.id.txt_title);
        gridView=(GridView)accepteddialog.findViewById(R.id.gridview);
        ImageView imvclear=(ImageView)accepteddialog.findViewById(R.id.imv_clear);
        imvclear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                accepteddialog.dismiss();
            }
        });
        if(type==1) txttitle.setText("Select a Component");
        gridView.setAdapter(new BlockimageAdapter(type));
        accepteddialog.show();
    }

    public class BlockimageAdapter extends BaseAdapter {
        int type=0;  //0: block, 1: other
        public BlockimageAdapter(int type) {
            super();
            this.type=type;
        }

        @Override
        public int getCount() {
            if(type==0)
                return bockicons.length;
            else
                return other.length;
        }

        @Override
        public Object getItem(int position) {
            if(type==0)
                return bockicons[position];
            else
                return other[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final CustomHolder holder;
            if (convertView == null) {
                holder = new CustomHolder();

                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.blockitem, parent, false);
                holder.imv_image=(SquareImageView) convertView.findViewById(R.id.imv_image);
                holder.txttitle=(TextView)convertView.findViewById(R.id.txt_title);
                convertView.setTag(holder);
            } else {
                holder = (CustomHolder) convertView.getTag();
            }
            if(type==0){
                holder.imv_image.setImageResource(bockicons[position]);
                holder.txttitle.setText(blocktexts[position]);
            }else{
                holder.imv_image.setImageResource(other[position]);
                holder.txttitle.setVisibility(GONE);
            }
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(type==0){
                        Constant.mode = 0;
                        CanvasView.this.addimage(bockicons[position]);
                        accepteddialog.dismiss();
                    }else {
                        switch (position){
                            case 0: // horizonal line
                                Constant.mode=5;
                                drawlilne(5, 50, 300, screenH/2-50, screenH/2-50,-1);
                                accepteddialog.dismiss();
                                break;
                            case 1: // vertical line
                                Constant.mode=6;
                                drawlilne(6, screenW/2, screenW/2, 100, 250,-1);
                                accepteddialog.dismiss();
                                break;
                            case 2: // slash
                                addslash();
                                accepteddialog.dismiss();
                                break;
                            case 3: // free line
                                Constant.mode=8;
                                accepteddialog.dismiss();
                                break;
                            case 4: // horizon arrow
                                Constant.mode=10;
                                addstatic_arrow(10);
                                accepteddialog.dismiss();
                                break;
                            case 5:  // vertical arrow
                                Constant.mode=11;
                                addstatic_arrow(11);
                                accepteddialog.dismiss();
                                break;

                            case 6: // free arrow
                                Constant.mode=1;
                                addstatic_arrow(1);
                                accepteddialog.dismiss();
                                break;
                            case 7: // text
                                addtextdialog(1);
                                accepteddialog.dismiss();
                                break;


                        }
                    }

                }
            });


            return convertView;
        }
        public class CustomHolder {
            SquareImageView imv_image;
            TextView txttitle;
        }
    }


    //============================= Calculate dimentions ==============
    public float getmiddle_x(float startx, float endx){
        float middle_x=0;
        if(startx > endx) {
            middle_x= endx;
            endx= startx;
            startx= middle_x;
        }
        middle_x= startx+(endx-startx)/2;

        return middle_x;
    }

    public  float getmiddle_y(float starty, float endy){
        float middle_y=0;
        if(starty > endy) {
            middle_y= endy;
            endy= starty;
            starty= middle_y;
        }
        middle_y= starty+(endy-starty)/2;

        return middle_y;
    }

    public int getpickedstatus_forarrow(float x, float y){
        MainDrawObject mainDrawObject = mainDrawObjects.get(mainDrawObjects.size()-1);
        OnearrowObject arrowObj=mainDrawObject.onearrowObject;
        double distance1 = Math.sqrt(Math.pow((arrowObj.startX-x),2) + Math.pow((arrowObj.startY-y),2));
        double distance2 = Math.sqrt(Math.pow((arrowObj.endX-x),2) + Math.pow((arrowObj.endY-y),2));
        float[]middlecoordinate = getmidle_coordinate(arrowObj.startX, arrowObj.startY, arrowObj.endX, arrowObj.endY);
        double distance3 = Math.sqrt(Math.pow((middlecoordinate[0]-x),2) + Math.pow((middlecoordinate[1]-y),2));

        Log.d("distance1===", String.valueOf(distance1));
        Log.d("distance2===", String.valueOf(distance2));
        Log.d("distance3===", String.valueOf(distance3));

        if(distance1<20) return 0;
        else if(distance2<20) return 1;
        else if(distance3<30) return 2;
        else return -1;
    }

    public int getpickedstatus(float x, float y){
        MainDrawObject mainDrawObject = mainDrawObjects.get(mainDrawObjects.size()-1);
        LineModel lineModel = mainDrawObject.lineModel;
        double distance1 = Math.sqrt(Math.pow((lineModel.line_start_x-x),2) + Math.pow((lineModel.line_start_y-y),2));
        double distance2 = Math.sqrt(Math.pow((lineModel.line_end_x-x),2) + Math.pow((lineModel.line_end_y-y),2));
        float[]middlecoordinate = getmidle_coordinate(lineModel.line_start_x, lineModel.line_start_y, lineModel.line_end_x, lineModel.line_end_y);
        double distance3 = Math.sqrt(Math.pow((middlecoordinate[0]-x),2) + Math.pow((middlecoordinate[1]-y),2));

        Log.d("distance1===", String.valueOf(distance1));
        Log.d("distance2===", String.valueOf(distance2));
        Log.d("distance3===", String.valueOf(distance3));

        if(distance1<20) return 0;
        else if(distance2<20) return 1;
        else if(distance3<30) return 2;
        else return -1;
    }


    public float[] getmidle_coordinate(float startx, float starty, float endx, float endy){
        float middle_x=0, middle_y=0;
        middle_x=(startx+endx)/2;
        middle_y=(starty+endy)/2;

        return new float[]{middle_x, middle_y};
    }
    public float[]getcoordinate(float middle_x, float middle_y, float angle){
        float startx, starty, endx, endy;
        startx = middle_x+(float) Math.cos(Math.toRadians(angle))*100;
        starty=middle_y - (float) Math.sin(Math.toRadians(angle))*100 ;

        endx= middle_x-(float) Math.cos(Math.toRadians(angle))*100;;
        endy= middle_y + (float) Math.sin(Math.toRadians(angle))*100 ;

        return new float[]{startx, endx, starty, endy};
    }





}
