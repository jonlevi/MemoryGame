package hwk2.cis350.upenn.edu.memorygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {


    Spinner difficultySpinner;
    String difficulty;
    public static final int MainActivity_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        difficultySpinner = (Spinner) findViewById(R.id.difficultySpinner);
        difficulty = difficultySpinner.getSelectedItem().toString();
        difficultySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                difficulty = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {
                difficulty = "Easy";
            }
        });

    }

    public void onPlayButtonButtonClick(View view) {
        Intent i = new Intent(this, gameActivity.class);
        i.putExtra("DIFFICULTY", difficulty);

        startActivityForResult(i, MainActivity_ID);
    }


}

