package com.example.mystringcalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void clickButton(View view) {
        EditText et = findViewById(R.id.editText);
        TextView tw = findViewById(R.id.textView);
        Calculator calc = new Calculator();
        calc.newFormula = et.getText().toString();
        float rez = calc.getResult();
        if (calc.ERROR) {
            String errorText = "ОШИБКА В ФОРМУЛЕ";
            if (calc.DIVISION_BY_ZERO) {
                errorText = errorText + ", ДЕЛЕНИЕ НА НОЛЬ";
            }
            if (calc.EXTRA_DECIMAL_POINT) {
                errorText = errorText + ", ЛИШНЯЯ ДЕСЯТИЧНАЯ ТОЧКА В ЧИСЛЕ";
            }
            if (calc.INVALID_CHARACTER) {
                errorText = errorText + ", НЕВЕРНЫЙ СИМВОЛ";
            }
            if (calc.BRACKETS_MISMATH) {
                errorText = errorText + ", НЕСООТВЕТСТВИЕ СКОБОК";
            }
            tw.setText(errorText);
        } else {
            tw.setText(et.getText().toString().replaceAll("\\s","") + " = " + rez);
        }
    }

    public void clickButtonEnd(View view) {
        moveTaskToBack(true);
        super.onBackPressed();
    }
}
