package com.example.architecturetest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.architecturetest.R;
import com.example.architecturetest.entity.Note;

//本来是RecyclerView.Adapter<NoteAdapter.NoteHolder> ，换
public class NoteAdapter extends ListAdapter<Note,NoteAdapter.NoteHolder> {
//    private List<Note> notes= new ArrayList<>(); 不要这个
    private OnItemClickListener listener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    //这个替换掉了传统的Array List形式的
    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            //为什么不直接比较两个item呢，因为每次变化，观察，刷新都是直接返回的一个List 里面是不同的Java对象
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getText().equals(newItem.getText()) &&
                    oldItem.getPriority()== newItem.getPriority();
        }
    };

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.item_note,parent,false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull NoteAdapter.NoteHolder holder, int position) {

        Note currentNote =getItem(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewDescription.setText(currentNote.getText());
        holder.textViewPriority.setText(String.valueOf(currentNote.getPriority()));
    }

//    @Override
//    public int getItemCount() {
//        return notes.size();
//    }

//    public void setNotes(List<Note> notes){
//        this.notes=notes;
//        //recycler View改变
//        notifyDataSetChanged();
//    }

    //从外面获得note的位置 用于确定滑动删除的
    public Note getNoteAt(int position){
        return getItem(position);
    }

      class NoteHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewPriority;


        public NoteHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
            super(itemView);
            textViewDescription=itemView.findViewById(R.id.text_view_description);
            textViewPriority=itemView.findViewById(R.id.text_view_priority);
            textViewTitle=itemView.findViewById(R.id.text_view_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener!=null && position!=RecyclerView.NO_POSITION){
                        listener.onItemClick(getItem(position));
//                    把数据到
                    }

                }
            });
        }
    }

    //自定义点击接口
    public interface OnItemClickListener{
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
            this.listener=listener;
    }
}
