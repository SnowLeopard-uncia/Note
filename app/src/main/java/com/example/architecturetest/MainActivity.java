package com.example.architecturetest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTES_REQUEST=1;
    private NoteViewModel noteViewModel;

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ADD_NOTES_REQUEST && resultCode ==RESULT_OK){
            String title=data.getStringExtra(AddNotesActivity.EXTRA_TITLE);
            String description=data.getStringExtra(AddNotesActivity.EXTRA_DESCRIPTION);
            int priority =data.getIntExtra(AddNotesActivity.EXTRA_PRIORITY,1);

            Note note =new Note(title,description,priority);
            noteViewModel.insert(note);
            Toast.makeText(this,"Note saved",Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this,"Notes not saved",Toast.LENGTH_SHORT).show();
        }
    }

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote =findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddNotesActivity.class);
          //  startActivityForResult(intent,ADD_NOTES_REQUEST);
                galleryActivityResultLauncher.launch(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);

        //noteViewModel= ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(getApplication())).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update Recyclerview
                noteAdapter.setNotes(notes);
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT
         | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this,"已删除",Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

    }


//解决startActivityfForResult 的方法
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                    , new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                    //here we will handle the result for intent
                            if(result.getResultCode() == Activity.RESULT_OK){
                                //创建对象，操作，拿数据
                                Intent data = result.getData();
                                String title=data.getStringExtra(AddNotesActivity.EXTRA_TITLE);
                                String description=data.getStringExtra(AddNotesActivity.EXTRA_DESCRIPTION);
                                int priority =data.getIntExtra(AddNotesActivity.EXTRA_PRIORITY,1);

                                Note note =new Note(title,description,priority);
                                noteViewModel.insert(note);
                                Toast.makeText(MainActivity.this,"Note saved",Toast.LENGTH_SHORT).show();
                            }else{
            Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_SHORT).show();
                                //取消
                            }
                        }
                    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
menuInflater.inflate(R.menu.main_menu,menu);
        return true;

    }
}