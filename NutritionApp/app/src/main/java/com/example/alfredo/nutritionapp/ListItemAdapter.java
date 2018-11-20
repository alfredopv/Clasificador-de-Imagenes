package com.example.alfredo.nutritionapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Brandon Navarro on 01/03/2018.
 */

class ListItemViewHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener{
    ItemClickListener itemClickListener;
    TextView  item_description, item_price;


    public ListItemViewHolder(View itemView) {
        super(itemView);
        //itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        item_price = (TextView)itemView.findViewById(R.id.item_price);
        item_description = (TextView)itemView.findViewById(R.id.item_description);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }



    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,0,getAdapterPosition(),"Añadir porcion");


    }
}

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder>{
    CameraActivity cameraActivity;
    List<Ingrediente> aingredientes;
    private Context context;



    public ListItemAdapter(CameraActivity cameraActivity, List<Ingrediente> aingredientes, Context context) {
        this.cameraActivity = cameraActivity;
        this.aingredientes = aingredientes;
        this.context=context;

    }



    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cameraActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item,parent, false);
        return new ListItemViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {

        //Picasso.with(context).load(aingredientes.get(position).getImgurl()).fit().into(holder.imagelist);
        // System.out.println(aingredientes.get(position).getImgurl());
        //holder.item_title.setText("Ingredientes");
        holder.item_description.setText(aingredientes.get(position).nombre);
        holder.item_price.setText(aingredientes.get(position).Cantidad);

    }

    @Override
    public int getItemCount() {
        return aingredientes.size();
    }




}
