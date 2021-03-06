package com.example.architecturetest.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.architecturetest.R;

public class AddNotesActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE=
            "com.example.architecturetest.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION =
            "com.example.architecturetest.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY=
            "com.example.architecturetest.EXTRA_PRIORITY";
    public static final String EXTRA_ID=
            "com.example.architecturetest.ID";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private NumberPicker numberPickerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        init();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        //接收要修改的数据
        Intent intent= getIntent();
        if(intent.hasExtra(EXTRA_ID)){
            setTitle("EditNode");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY,1));
        }else {
            setTitle("Add Notes");
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_notes_menus,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_notes:
                saveNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNotes() {
        String title = editTextTitle.getText().toString();
        String description =editTextDescription.getText().toString();
        int priority =numberPickerPriority.getValue();

        if (title.trim().isEmpty() || description.trim().isEmpty()){
            Toast.makeText(this,"请输入标题或内容",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE,title);
        data.putExtra(EXTRA_DESCRIPTION,description);
        data.putExtra(EXTRA_PRIORITY,priority);

        int id = getIntent().getIntExtra(EXTRA_ID,-1);
        if (id!=-1){
            data.putExtra(EXTRA_ID,id);
        }

        setResult(RESULT_OK,data);
        finish();
    }

    public void init(){
        editTextDescription=findViewById(R.id.text_view_description);
        editTextTitle=findViewById(R.id.edit_text_title);
        numberPickerPriority=findViewById(R.id.note_number_picker);
        numberPickerPriority.setMaxValue(9);
        numberPickerPriority.setMinValue(1);
    }
}