package com.example.mathascend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;


public class NumberComposing extends AppCompatActivity {

    private TextView streakText, box1, box2, answerBox, arithmetic, question;
    private TextView num1, num2, num3, num4;
    private Button btnSubmit;
    private int targetNumber, streaks;;
    private int[] numbers = new int[4];
    private int selectedNum1 = -1, selectedNum2 = -1;
    private boolean isAddition = true;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_composing);

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        streaks = sharedPreferences.getInt("Current_Streaks", 0);

        streakText = findViewById(R.id.streaks_composing);
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        answerBox = findViewById(R.id.answerbox);
        arithmetic = findViewById(R.id.arithmetic);
        question = findViewById(R.id.question);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        btnSubmit = findViewById(R.id.btn_submit);

        generateNumbers();
        setNumberListeners();
        updateStreakDisplay();
        btnSubmit.setOnClickListener(v -> checkAnswer());
    }

    private void generateNumbers() {
        Random random = new Random();
        targetNumber = random.nextInt(94) + 5; // Target number between 5 and 25

        HashSet<Integer> uniqueNumbers = new HashSet<>();


        isAddition = random.nextBoolean();
        arithmetic.setText(isAddition ? "+" : "-");

        int numA, numB;

        if (isAddition) {
            // Generate a valid pair for addition
            do {
                numA = random.nextInt(targetNumber - 1) + 1;
                numB = targetNumber - numA;
            } while (numA == numB || uniqueNumbers.contains(numA) || uniqueNumbers.contains(numB));
        } else {
            // Generate a valid pair for subtraction
            do {
                numA = random.nextInt(15) + 5; // Ensure numA is always larger
                numB = random.nextInt(numA - 1) + 1; // Ensure numB is smaller
                targetNumber = numA - numB; // Adjust target number based on subtraction
            } while (numA == numB || uniqueNumbers.contains(numA) || uniqueNumbers.contains(numB));
        }

        uniqueNumbers.add(numA);
        uniqueNumbers.add(numB);

        // Generate two more unique random numbers
        while (uniqueNumbers.size() < 4) {
            int num = random.nextInt(20) + 1; // Generate a number between 1 and 20
            if (!uniqueNumbers.contains(num)) {
                uniqueNumbers.add(num);
            }
        }


        // Convert to list and shuffle
        ArrayList<Integer> numberList = new ArrayList<>(uniqueNumbers);
        Collections.shuffle(numberList);

        // Assign shuffled numbers to TextViews
        numbers[0] = numberList.get(0);
        numbers[1] = numberList.get(1);
        numbers[2] = numberList.get(2);
        numbers[3] = numberList.get(3);

        question.setText("Make " + targetNumber + " using two numbers");
        num1.setText(String.valueOf(numbers[0]));
        num2.setText(String.valueOf(numbers[1]));
        num3.setText(String.valueOf(numbers[2]));
        num4.setText(String.valueOf(numbers[3]));
    }

    private void setNumberListeners() {
        View.OnClickListener numberClickListener = v -> {
            TextView selected = (TextView) v;
            int value = Integer.parseInt(selected.getText().toString());

            if (selectedNum1 == -1) {
                selectedNum1 = value;
                box1.setText(String.valueOf(value));
                box1.setBackgroundResource(R.drawable.placeholder_bg_1_white);

            } else if (selectedNum2 == -1) {
                selectedNum2 = value;
                box2.setText(String.valueOf(value));
                box2.setBackgroundResource(R.drawable.placeholder_bg_1_white);

            }
        };

        num1.setOnClickListener(numberClickListener);
        num2.setOnClickListener(numberClickListener);
        num3.setOnClickListener(numberClickListener);
        num4.setOnClickListener(numberClickListener);

    }

    private void checkAnswer() {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (selectedNum1 == -1 || selectedNum2 == -1) {
            //invalid ans
            answerBox.setText("?");
            return;
        }

        int result = isAddition ? (selectedNum1 + selectedNum2) : (selectedNum1 - selectedNum2);
        answerBox.setText(String.valueOf(result));

        if (result == targetNumber) {
            showDialog("Correct!", "Well done! Play again?");
            answerBox.setBackgroundResource(R.drawable.composing_ansbox_correct);
            streaks++;


        } else {
            showDialog("Incorrect Order!", "Try again!");
            answerBox.setBackgroundResource(R.drawable.composing_ansbox_incorrect);
            answerBox.setTextColor(Color.WHITE);
            streaks = 0;
        }

        editor.putInt("Current_Streaks", streaks);
        editor.apply();
        updateStreakDisplay();
    }

    private void showDialog(String t, String d){
        Dialog dialog = new Dialog(this, R.style.Theme_CustomDialog);
        dialog.setContentView(R.layout.number_comparing_dialog_box);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.title);
        TextView result = dialog.findViewById(R.id.Result);
        Button btnClose = dialog.findViewById(R.id.btn_Next);

        title.setText(t);
        result.setText(d);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                resetSelection();
            }
        });

        dialog.show();
    }

    private void updateStreakDisplay() {

        streakText.setText(String.valueOf(streaks));
    }

    private void resetSelection() {
        generateNumbers();
        selectedNum1 = -1;
        selectedNum2 = -1;
        box1.setText("");
        box2.setText("");
        answerBox.setText("");
        box1.setBackgroundResource(R.drawable.text_display_box);
        box2.setBackgroundResource(R.drawable.text_display_box);
        answerBox.setBackgroundResource(R.drawable.text_display_box);
    }

    public void ReturnToMenu(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}

