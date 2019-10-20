package com.zhan.fedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity  {

    EditText AngleInput;

    LinearLayout arrowPnl;

    CanvasView canvas;
    EditText lengthInput;
    Button okButton, undobutton,btn_redo,btn_gl,   blockbutton, cancelbutton, btn_other;
    public EditText textInput;

    LinearLayout lytinclude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        undobutton=(Button)findViewById(R.id.btn_undo);
        btn_redo=(Button)findViewById(R.id.btn_redo);
        btn_gl=(Button)findViewById(R.id.btn_gl);
        blockbutton=(Button)findViewById(R.id.btn_boock);




        arrowPnl = (LinearLayout) findViewById(R.id.relativeLayout1);
        textInput = (EditText) findViewById(R.id.editText1);
        okButton = (Button) findViewById(R.id.button1);
        btn_other = (Button) findViewById(R.id.btn_other);
        cancelbutton = (Button) findViewById(R.id.button2);
        lengthInput = (EditText) findViewById(R.id.editText2);
        AngleInput = (EditText) findViewById(R.id.editText3);

        lytinclude=(LinearLayout)findViewById(R.id.lyt_include);
        this.canvas = new CanvasView(this, null);
        lytinclude.addView(canvas);
        canvas.updateta();

    }

    public void hidekeybaord(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
