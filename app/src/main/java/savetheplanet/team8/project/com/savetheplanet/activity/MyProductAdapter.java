package savetheplanet.team8.project.com.savetheplanet.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import savetheplanet.team8.project.com.savetheplanet.R;
import savetheplanet.team8.project.com.savetheplanet.model.Product;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.ViewHolder> {

    private List<Product> productsList;
    private Context context;

    public MyProductAdapter(List<Product> productsList, Context context) {
        this.context = context;
        this.productsList = productsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Product p = productsList.get(position);
        holder.name.setText(p.getName());
        holder.description.setText(p.getDescription());
//        holder.location.setText(p.getLocation());
        holder.tags.setText(p.getTag());
        holder.productID.setText(p.getProductID());
        if (!p.getImageUrl().isEmpty()) {
            Picasso.with(context).load(p.getImageUrl()).fit().centerCrop().into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView name, description, location, tags, productID;
        public ImageView productImage;


        ViewHolder(View view) {
            super(view);
            view.setOnCreateContextMenuListener(this);
            name = (TextView) view.findViewById(R.id.name);
            description = (TextView) view.findViewById(R.id.description);
            location = (TextView) view.findViewById(R.id.location);
            tags = (TextView) view.findViewById(R.id.tags);
            productImage = view.findViewById(R.id.productPicture);
            productID = view.findViewById(R.id.productId);


        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem deleteProduct = menu.add(0, v.getId(), 0, "Delete");
            deleteProduct.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context, "Product deleted", Toast.LENGTH_LONG).show();
                    deleteProductByKey(productID.getText().toString());
                    return false;
                }
            });
        }

        private void deleteProductByKey(String key) {
            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("products").child(key);
            databaseReference.removeValue();
            productsList.remove(getAdapterPosition());
            notifyDataSetChanged();

        }
    }
}
