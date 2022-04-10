package com.example.architecturetest.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.architecturetest.entity.Note;

import java.util.List;
//Dao注解 表示这是个Dao
// 是写SQL语句的，大概是SQL语句跟方法对应，调用方法时相当于使用SQL语句操作数据库，在这里在编码阶段就能检查语句的语法，
@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table order by priority_column DESC")
    LiveData<List<Note>> getAllNotes();

}
