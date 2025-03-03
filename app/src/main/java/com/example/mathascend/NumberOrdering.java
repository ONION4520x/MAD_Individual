package com.example.mathascend;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberOrdering extends AppCompatActivity {

    private boolean isAscending;
    private TextView question, box1, box2, box3, box4, num1View, num2View, num3View, num4View,streaks_TextView;
    private int  streaks;
    private List<Integer> numbers = new ArrayList<>();
    private List<TextView> numberViews = new ArrayList<>();
    private List<TextView> boxViews = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_ordering);

        question = findViewById(R.id.question);

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        streaks = sharedPreferences.getInt("Current_Streaks_Ordering", 0);

        // Initialize number TextViews
        num1View = findViewById(R.id.num1);
        num2View = findViewById(R.id.num2);
        num3View = findViewById(R.id.num3);
        num4View = findViewById(R.id.num4);

        numberViews.add(num1View);
        numberViews.add(num2View);
        numberViews.add(num3View);
        numberViews.add(num4View);

        // Initialize box TextViews
        box1 = findViewById(R.id.box1);
        box2 = findViewById(R.id.box2);
        box3 = findViewById(R.id.box3);
        box4 = findViewById(R.id.box4);

        boxViews.add(box1);
        boxViews.add(box2);
        boxViews.add(box3);
        boxViews.add(box4);

        isAscending = Math.random() < 0.5; // 50% chance for either question
        updateQuestionText();

        streaks_TextView = findViewById(R.id.streaks);


        // Generate and shuffle numbers
        generateNumbers();

        // Enable drag for number views
        for (TextView numView : numberViews) {
            setDragListener(numView);
        }

        // Enable drag & drop for boxes
        for (TextView boxView : boxViews) {
            setDragListener(boxView);
            setDropListener(boxView);
        }

        // Set validation button
        Button validateButton = findViewById(R.id.btn_submit);
        validateButton.setOnClickListener(v -> validateOrder());

        updateStreakDisplay();
    }

    private void resetGame(){

        for (int i = 0; i < numberViews.size(); i++) {
            numberViews.get(i).setText("");
            numberViews.get(i).setBackgroundResource(R.drawable.placeholder_bg_1_white);

        }

        for (int i = 0; i < boxViews.size(); i++){
            boxViews.get(i).setText("");
            boxViews.get(i).setBackgroundResource(R.drawable.text_display_box);
        }

        generateNumbers();

    }

    private void updateQuestionText() {
        if (isAscending) {
            question.setText("Arrange the numbers in Ascending Order");
        } else {
            question.setText("Arrange the numbers in Descending Order");
        }
    }

    // Generate four random numbers and shuffle them
    private void generateNumbers() {
        numbers.clear();
        for (int i = 1; i <= 4; i++) {
            numbers.add((int) (Math.random() * 100)); // Generate numbers 0-99
        }
        Collections.shuffle(numbers);

        for (int i = 0; i < numberViews.size(); i++) {
            numberViews.get(i).setText(String.valueOf(numbers.get(i)));

        }
    }

    // Set up drag listener for numbers & boxes
    @SuppressLint("ClickableViewAccessibility")
    private void setDragListener(final TextView textView) {
        textView.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData clipData = ClipData.newPlainText("text", textView.getText().toString());
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(textView);
                view.startDragAndDrop(clipData, shadowBuilder, view, 0);
                return true;
            }
            return false;
        });
    }

    // Set up drop listener for boxes
    private void setDropListener(final TextView targetBox) {
        targetBox.setOnDragListener((view, dragEvent) -> {
            View draggedView = (View) dragEvent.getLocalState();
            TextView draggedTextView = (TextView) draggedView;

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    boolean isFromBoxView = boxViews.contains(draggedTextView);

                    if (isFromBoxView) {
                        draggedTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        draggedTextView.setVisibility(View.INVISIBLE);
                    }

                    return true;

                case DragEvent.ACTION_DROP:
                    if (draggedView instanceof TextView) {
                        String draggedText = draggedTextView.getText().toString();
                        String targetText = targetBox.getText().toString();

                        // Check if dragging from numberViews (num1View, num2View, etc.)
                        boolean isFromNumberView = numberViews.contains(draggedTextView);

                        if (!draggedText.equals(targetText)) {
                            draggedTextView.setText(targetText); // Swap values
                            targetBox.setText(draggedText); // Move dragged text into target box

                            // If dragged from a number view, hide it after placing
                            if (isFromNumberView) {
                                draggedTextView.setVisibility(View.INVISIBLE);
                            }
                        }

                        // Reset background if dragged from a box and it becomes empty
                        if (draggedTextView.getText().toString().isEmpty()) {
                            draggedTextView.setBackgroundResource(R.drawable.text_display_box);
                        }

                        // Ensure target box style is updated
                        targetBox.setBackgroundResource(R.drawable.placeholder_bg_1_white);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                  draggedView.setVisibility(View.VISIBLE); // Ensure dragged view stays visible
                    return true;

                default:
                    return false;
            }
        });
    }


    // Validate if numbers are sorted correctly
    private void validateOrder() {
        List<Integer> orderedNumbers = new ArrayList<>();
        SharedPreferences.Editor editor = sharedPreferences.edit();

        for (TextView box : boxViews) {
            if (box.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill all boxes before submitting!", Toast.LENGTH_SHORT).show();
                return;
            }
            orderedNumbers.add(Integer.parseInt(box.getText().toString()));
        }

        List<Integer> correctOrder = new ArrayList<>(orderedNumbers);
        if (isAscending) {
            Collections.sort(correctOrder); // Ascending order
        } else {
            correctOrder.sort(Collections.reverseOrder()); // Descending order
        }

        if (orderedNumbers.equals(correctOrder)) {
            streaks++;
            showDialog("Correct!", "Well done! Play again?");
            resetGame();
        } else {
            streaks = 0;
            showDialog("Incorrect Order!", "Try again!");
        }

        editor.putInt("Current_Streaks_Ordering", streaks);
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
