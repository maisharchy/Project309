package com.example.androidexample;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class GameListAdapter extends ArrayAdapter<GameListObject> {

    public GameListAdapter(Context context, List<GameListObject> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GameListObject item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.game_list_item, parent, false);
        }

        // Lookup view for data population
        TextView itemGame = convertView.findViewById(R.id.itemGame);
        TextView itemIsAssigned = convertView.findViewById(R.id.itemIsAssigned);

        // Populate the data into the template view using the data object
        itemGame.setText(item.getGame());
        itemIsAssigned.setText(item.getIsAssigned());

        // Return the completed view to render on screen
        return convertView;
    }
}

