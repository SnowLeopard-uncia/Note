package com.example.architecturetest;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.architecturetest.dao.NoteDao;
import com.example.architecturetest.entity.Note;

@Database(entities = {Note.class},version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    //用dao去访问数据库操作方法
    public abstract NoteDao noteDao();

    //单例模式创建数据库
    //单例模式 synchronized每次只允许一个线程来访问这个方法。这样，当两个不同的线程试图同时访问这个方法时，你就不会意外地创建这个数据库的两个实例
    //synchronized可以避免这个问题
    //创建了唯一的一个数据库实例，然后我们可以从外部调用这个方法，并获得这个实例
    public static synchronized NoteDatabase getInstance(Context context){

        if (instance ==null){
//            用builder创建不能直接new ，因为这是抽象类
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,"note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull  SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{

        private NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase db) {
            noteDao= db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title1","description1",1));
            noteDao.insert(new Note("Title2","description2",2));
            noteDao.insert(new Note("Title3","description3",3));
            return null;
        }
    }

}
