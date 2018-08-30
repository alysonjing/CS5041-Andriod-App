package com.example.android.idea;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;


public class IdeaAdapter extends ArrayAdapter<Idea> {

    public static final int WRAP_CONTENT_LENGTH = 50;
    public IdeaAdapter(Context context, int resource, List<Idea> objects) {
        super(context, resource, objects);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_idea, null);
        }

        Idea idea = getItem(position);

        if(idea != null) {
            TextView title = (TextView) convertView.findViewById(R.id.list_idea_title);
            TextView date = (TextView) convertView.findViewById(R.id.list_idea_date);
            TextView content = (TextView) convertView.findViewById(R.id.list_idea_content_preview);

            title.setText(idea.getTitle());
            date.setText(idea.getDateTimeFormatted(getContext()));

            int toWrap = WRAP_CONTENT_LENGTH;
            int lineBreakIndex = idea.getContent().indexOf('\n');

            if(idea.getContent().length() > WRAP_CONTENT_LENGTH || lineBreakIndex < WRAP_CONTENT_LENGTH) {
                if(lineBreakIndex < WRAP_CONTENT_LENGTH) {
                    toWrap = lineBreakIndex;
                }
                if(toWrap > 0) {
                    content.setText(idea.getContent().substring(0, toWrap) + "...");
                } else {
                    content.setText(idea.getContent());
                }
            } else {
                content.setText(idea.getContent());
            }
        }

        return convertView;
    }

}

