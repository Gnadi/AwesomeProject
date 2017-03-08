package htl.at.awesomeproject.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import htl.at.awesomeproject.R;
import htl.at.awesomeproject.entity.Product;

/**
 * Created by Gnadlinger on 19-Jan-17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
private List<Product> products;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    public void setProducts(List<Product> products){
        this.products = products;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            Product product = products.get(position);
        holder.name.setText(product.getProductname());
        holder.username.setText(product.getUsername());
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return products.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, username;
        CardView cv;
        public MyViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            name = (TextView) itemView.findViewById(R.id.name);
            username = (TextView) itemView.findViewById(R.id.username);

        }
    }

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }
}
