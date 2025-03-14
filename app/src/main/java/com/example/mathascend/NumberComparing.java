package com.example.mathascend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.util.Pair;
import android.app.Dialog;

import java.util.Random;


public class NumberComparing extends AppCompatActivity {
    private TextView dropArea, greaterThan, lessThan,streaks_TextView;
    private TextView num1TextView, num2TextView;
    private int num1, num2, streaks;
    View view1 ;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_comparing);

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        streaks = sharedPreferences.getInt("Current_Streaks_Comparing", 0);

        Pair<Integer, Integer> NumbersGenerated = RandomNumberGenerator();
        num1 = NumbersGenerated.first;
        num2 = NumbersGenerated.second;

        // Initialize views
        streaks_TextView = findViewById(R.id.streaks);
        dropArea = findViewById(R.id.drop_area);
        greaterThan = findViewById(R.id.greater_than);
        lessThan = findViewById(R.id.less_than);
        num1TextView = findViewById(R.id.num1);
        num2TextView = findViewById(R.id.num2);

        num1TextView.setText(String.valueOf(NumbersGenerated.first));
        num2TextView.setText(String.valueOf(NumbersGenerated.second));

        // Set drag listeners
        greaterThan.setOnTouchListener(new DragTouchListener());
        lessThan.setOnTouchListener(new DragTouchListener());
        dropArea.setOnDragListener(new DropListener());

        //when submit button is press
        Button btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateComparison();
            }
        });

        // Display initial streak count
        updateStreakDisplay();
    }

    //Touch event for dragging
    private class DragTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                view.setVisibility(View.INVISIBLE);

                // Retrieve the text from the TextView being dragged
                TextView draggedTextView = (TextView) view;
                String draggedText = draggedTextView.getText().toString();

                // Create ClipData with actual text
                ClipData data = ClipData.newPlainText("DraggedText", draggedText);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                view.startDragAndDrop(data, shadowBuilder, view, 0);
                return true;
            }
            return false;
        }
    }


    // Handle drop event
    private class DropListener implements View.OnDragListener {
        private String previousText = "";
        private View previousView = null;

        @SuppressLint("SetTextI18n")
        @Override
        public boolean onDrag(View v, DragEvent event) {

            View draggedView = (View) event.getLocalState();
            view1 = draggedView;

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:

                case DragEvent.ACTION_DRAG_LOCATION:

                case DragEvent.ACTION_DRAG_EXITED:

                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;

                case DragEvent.ACTION_DROP:
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    if (item != null && item.getText() != null) {
                        String draggedText = item.getText().toString();

                        if (!((TextView) v).getText().toString().isEmpty()) {
                            if (previousView != null) {
                                previousView.setVisibility(View.VISIBLE); // Restore previous item
                            }
                        }

                        // Save current text and view before replacing
                        previousText = ((TextView) v).getText().toString();
                        previousView = draggedView;

                        // Update the box
                        dropArea.setBackgroundResource(R.drawable.placeholder_bg_1_white);
                        ((TextView) v).setText(draggedText);
                        draggedView.setVisibility(View.INVISIBLE);

                    } else {
                        Log.e("DragDrop", "Dropped item is null or has no text!");
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) {
                        draggedView.setVisibility(View.VISIBLE);
                    }
                    return true;

                default:
                    return false;

            }
        }
    }

    public Pair<Integer, Integer> RandomNumberGenerator() {
        Random random = new Random();
        //Generate 2 number from 0 - 99
        int num1 = random.nextInt(100);
        int num2 = random.nextInt(100);
        return new Pair<>(num1, num2);
    }

    private void resetGame () {

        // Generate new numbers
        Pair<Integer, Integer> newNumbers = RandomNumberGenerator();
        num1 = newNumbers.first;
        num2 = newNumbers.second;

        // Update UI
        num1TextView.setText(String.valueOf(num1));
        num2TextView.setText(String.valueOf(num2));
        dropArea.setText("");  //clear the box
        dropArea.setBackgroundResource(R.drawable.text_display_box);

        if(view1 != null){
            view1.setVisibility(View.VISIBLE);
        }
    }


    // Validate comparison
    private void validateComparison() {
        String placeholderText = dropArea.getText().toString().trim();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String message;
        boolean correct = false;

        if (placeholderText.equals(">")) {
            if (num1 > num2) {
                message = num1 + " is greater than " + num2;
                correct = true;
            } else {
                message = num1 + " is not greater than " + num2 + "\nCorrect answer is <";
            }
        } else if (placeholderText.equals("<")) {
            if (num1 < num2) {
                message = num1 + " is less than " + num2;
                correct = true;
            } else {
                message = num1 + " is not less than " + num2 + "\nCorrect answer is >";
            }
        } else {
            Toast.makeText(this, "Please drag and place either > or < in the answer box", Toast.LENGTH_SHORT).show();
            return;
        }

        if (correct) {
            streaks++;
        } else {
            streaks = 0;
        }

        // Update streaks in SharedPreferences
        editor.putInt("Current_Streaks_Comparing", streaks);
        editor.apply();

        // Update streaks display
        updateStreakDisplay();

        // Show result dialog
        showDialog(correct ? "Correct!" : "Incorrect", message);
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
                resetGame();
            }
        });

        dialog.show();
    }

    private void updateStreakDisplay() {

        streaks_TextView.setText(String.valueOf(streaks));
    }

    public void ReturnToMenu(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}






