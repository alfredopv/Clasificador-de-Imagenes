package com.example.alfredo.nutritionapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by Brandon Navarro on 01/03/2018.
 */

class ListItemViewHolder2 extends RecyclerView.ViewHolder implements  View.OnClickListener,View.OnCreateContextMenuListener{
    ItemClickListener itemClickListener;
    TextView HC_t, Kcal_t, LP_t,PS_t, Producto_t;
    ImageView imagelist;




    public ListItemViewHolder2(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        Producto_t = (TextView)itemView.findViewById(R.id.producto_a);
        Kcal_t = (TextView)itemView.findViewById(R.id.kcal_a);
        HC_t = (TextView)itemView.findViewById(R.id.hc_a);
        PS_t = (TextView)itemView.findViewById(R.id.ps_a);
        LP_t = (TextView) itemView.findViewById(R.id.lp_a);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0,0,getAdapterPosition(),"DELETE");


    }
}

public class ListItemAdapter2 extends RecyclerView.Adapter<ListItemViewHolder2>{
    RegistrosActivity mainActivity;
    List<Alimento> alimentoList;
    private Context context;



    public ListItemAdapter2(RegistrosActivity mainActivity, List<Alimento> alimentoList, Context context) {
        this.mainActivity = mainActivity;
        this.alimentoList = alimentoList;
        this.context=context;

    }



    @Override
    public ListItemViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item2,parent, false);
        return new ListItemViewHolder2(view);

    }


    @Override
    public void onBindViewHolder(ListItemViewHolder2 holder, int position) {

        //Picasso.with(context).load(productList.get(position).getImgurl()).fit().into(holder.imagelist);
        holder.Producto_t.setText(alimentoList.get(position).getProducto());
        holder.Kcal_t.setText("Calorias totales: "+alimentoList.get(position).getKcal());
        holder.LP_t.setText("LP: "+String.valueOf(alimentoList.get(position).getLP()));
        holder.PS_t.setText("PS : "+String.valueOf(alimentoList.get(position).getPS()));
        holder.HC_t.setText("HC : "+String.valueOf(alimentoList.get(position).getHC()));


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                /*
                mainActivity.name.setText(productList.get(position).getName());
                mainActivity.description.setText(productList.get(position).getDescription());
                mainActivity.price.setText("Price: "+productList.get(position).getPrice());
                mainActivity.isUpdate= true;
                mainActivity.idUpdate = productList.get(position).getId();
                */
            }
        });





    }

    @Override
    public int getItemCount() {
        return alimentoList.size();
    }




}
