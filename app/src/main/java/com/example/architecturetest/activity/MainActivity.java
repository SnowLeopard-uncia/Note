package com.example.architecturetest.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.architecturetest.adapter.NoteAdapter;
import com.example.architecturetest.R;
import com.example.architecturetest.entity.Note;
import com.example.architecturetest.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTES_REQUEST=1;
    public static final int EDIT_NOTES_REQUEST=2;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //添加按钮
        FloatingActionButton buttonAddNote =findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNotesActivity.class);
          //  startActivityForResult(intent,ADD_NOTES_REQUEST);
                insertActivityResultLauncher.launch(intent);
            }
        });

        //recycler View
        RecyclerView recyclerView = findViewById(R.id.main_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        NoteAdapter noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);

        //noteViewModel= ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel = new ViewModelProvider(this, new ViewModelProvider
                .AndroidViewModelFactory(getApplication())).get(NoteViewModel.class);

//owner 是LifecycleOwner，定义：一个有Android生命周期的类。这里的this是Activity
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //MainActivity里liveData的形式观察
                // update Recyclerview 观察变化，有变化就调用setNote方法 自动更新
//                noteAdapter.setNotes(notes);
                noteAdapter.submitList(notes); //用这个监听数据
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

        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this,AddNotesActivity.class);
                //数据库要根据主码来确定更新哪个
                intent.putExtra(AddNotesActivity.EXTRA_ID,note.getId());
                intent.putExtra(AddNotesActivity.EXTRA_TITLE,note.getTitle());
                intent.putExtra(AddNotesActivity.EXTRA_DESCRIPTION,note.getText());
                intent.putExtra(AddNotesActivity.EXTRA_PRIORITY,note.getPriority());
                updateActivityResultLauncher.launch(intent);
            }
        });
    }


//解决startActivityfForResult 的方法
    private ActivityResultLauncher<Intent> insertActivityResultLauncher =
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
                            }
                            else{
            Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_SHORT).show();
                                //取消
                            }
                        }
                    });

    private ActivityResultLauncher<Intent> updateActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                    , new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            //here we will handle the result for intent
                            if(result.getResultCode() == Activity.RESULT_OK){
                                //创建对象，操作，拿数据
                                Intent data = result.getData();
                                int id = data.getIntExtra(AddNotesActivity.EXTRA_ID,-1);
                                if (id==-1){
                                    Toast.makeText(MainActivity.this,"can't update",Toast.LENGTH_SHORT).show();

                                }
                                String title=data.getStringExtra(AddNotesActivity.EXTRA_TITLE);
                                String description=data.getStringExtra(AddNotesActivity.EXTRA_DESCRIPTION);
                                int priority =data.getIntExtra(AddNotesActivity.EXTRA_PRIORITY,1);

                                Note note =new Note(title,description,priority);
                                //没有这句就找不到主码，就无法更新
                                note.setId(id);
                                noteViewModel.update(note);
                                Toast.makeText(MainActivity.this,"Note update",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_SHORT).show();
                                //取消
                            }
                        }
                    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
menuInflater.inflate(R.menu.main_menu,menu);
//就是删除全部内容的，点击弹出来菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(MainActivity.this,"已删除全部笔记",
                        Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
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
}