package com.ecolem_test.tp3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by akawa_000 on 16/07/2015.
 */
public class MessageListAdapter extends ArrayAdapter<Message> {
    public MessageListAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Toast.makeText(getContext(), "TEST", Toast.LENGTH_SHORT).show();

        Message msg = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_list_row, parent, false);
        }

        TextView tvContent = (TextView) convertView.findViewById(R.id.msg_content);
        TextView tvDate = (TextView) convertView.findViewById(R.id.msg_date);
        TextView tvInfo = (TextView) convertView.findViewById(R.id.msg_info);

        tvContent.setText(msg.content);
        tvDate.setText(msg.date);
        tvInfo.setText(msg.info);

        return convertView;
    }
}
