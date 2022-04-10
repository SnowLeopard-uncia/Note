package com.example.architecturetest.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
//注解 这个Java实体类映射成一个表
@Entity(tableName = "note_table")
public class Note {

    //类中的属性为表中字段
    //注解表示这个是主码，而且会自增，所以构造函数可以不用把这个加进去
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String text;
    //注解
    @ColumnInfo(name = "priority_column")
    private int priority; //优先级

    public Note(String title, String text, int priority) {
        this.title = title;
        this.text = text;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public int getPriority() {
        return priority;
    }
}
