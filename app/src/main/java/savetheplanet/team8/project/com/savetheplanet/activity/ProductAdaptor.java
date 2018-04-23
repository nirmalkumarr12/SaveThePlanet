package savetheplanet.team8.project.com.savetheplanet.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.model.Product;


/**
 * Created by DhineshKumarPC on 4/22/2018.
 */

public class ProductAdaptor extends RecyclerView.Adapter<ProductAdaptor.MyViewHolder>{
    private List<Product> productsList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description, location,tags;
        public ImageView productImage;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
            location = (TextView) view.findViewById(R.id.location);
            tags = (TextView) view.findViewById(R.id.tags);
            productImage = view.findViewById(R.id.productPicture);
        }
    }


    public ProductAdaptor(List<Product> productsList, Context context) {
        this.productsList = productsList;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product p = productsList.get(position);
        holder.name.setText(p.getName());
        holder.description.setText(p.getDescription());
//        holder.location.setText(p.getLocation());
        holder.tags.setText(p.getTag());
        if (! p.getImageUrl().isEmpty()) {
            Picasso.with(context).load(p.getImageUrl()).fit().centerCrop().into(holder.productImage);
        }


    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
}
