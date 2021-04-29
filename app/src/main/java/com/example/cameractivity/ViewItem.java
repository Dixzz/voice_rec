package com.example.cameractivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewItem extends AppCompatActivity {

    @BindView(R.id.shareBtn)
    View shareBtn;

    @BindView(R.id.rec2)
    RecyclerView rec2;

    @BindView(R.id.imageView)
    SimpleDraweeView imageView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_product);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.item_title)).setText(getIntent().getStringExtra("shop_name"));
        ((TextView) findViewById(R.id.offer)).setText(getIntent().getStringExtra("shop_offer") + "% offer");
        ArrayList<MainActivity.ItemHolder> itemHolder = new ArrayList<>();
        if (getIntent().getStringExtra("shop_name") != null) {
            //Toast.makeText(this, "" + getIntent().getStringExtra("shop_name"), Toast.LENGTH_SHORT).show();
        }
        if (getIntent().getStringExtra("shop_image") != null)
            imageView.setImageURI(getIntent().getStringExtra("shop_image"));
        else
            imageView.setImageDrawable(getDrawable(R.drawable.placeholder));

        shareBtn.setOnClickListener(view -> {
            Snackbar.make(shareBtn, "Saving image", Snackbar.LENGTH_SHORT).show();
            new Multi3().start();
        });

        adapter adapter = new adapter(itemHolder);

        rec2.setAdapter(adapter);
        rec2.setLayoutManager(new LinearLayoutManager(this));


        String sql = String.format("SELECT * from ShopProducts where prod_parent='%s'", getIntent().getStringExtra("shop_name"));


        Cursor c = readShopList(sql);
        c.moveToFirst();
        Log.d("AAA", "" + c.getCount());

        Log.d("AAA", "" + c.getCount());
        while (c.moveToNext()) {
            itemHolder.add(new MainActivity.ItemHolder(
                    c.getString(c.getColumnIndex("prod_price")),
                    c.getString(c.getColumnIndex("prod_name")), c.getString(c.getColumnIndex("prod_desc"))
            ));
        }
        adapter.notifyDataSetChanged();
        c.close();
    }

    private Cursor readShopList(String sql) {
        SQLiteDatabase database;
        DataBaseHelper mDb2;
        mDb2 = new DataBaseHelper(this);
        mDb2.createDataBase();
        mDb2.openDataBase();
        database = mDb2.getWritableDatabase();
        try {
            return database.rawQuery(sql, null);
        } catch (SQLException mSQLException) {
            mSQLException.printStackTrace();
        }
        mDb2.close();
        database.close();
        return null;
    }

    public void openMap(View view) {
        this.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(String.format("http://maps.google.com/maps?daddr=%s,%s", 19.1633704, 72.9464829))));
    }

    private static class adapter extends RecyclerView.Adapter<adapter.holder> {
        ArrayList<MainActivity.ItemHolder> itemHolder;

        public adapter(ArrayList<MainActivity.ItemHolder> itemHolder) {
            this.itemHolder = itemHolder;
        }

        @NonNull
        @Override
        public adapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new adapter.holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_products, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull adapter.holder holder, int position) {
            holder.pos.setText(position + 1 + "");
            holder.title.setText(itemHolder.get(position).name);
            holder.desc.setText(itemHolder.get(position).shopeImage);
            holder.price.setText("₹" + itemHolder.get(position).offer);
        }

        @Override
        public int getItemCount() {
            return itemHolder.size();
        }

        static class holder extends RecyclerView.ViewHolder {
            TextView pos, title, desc, price;

            public holder(@NonNull View itemView) {
                super(itemView);
                pos = itemView.findViewById(R.id.positionI);
                title = itemView.findViewById(R.id.titlee);
                desc = itemView.findViewById(R.id.desc);
                price = itemView.findViewById(R.id.price);
            }
        }
    }

    class Multi3 extends Thread {
        public void run() {
            System.out.println("thread is running...");
            try {
                URL url = new URL(getIntent().getStringExtra("shop_image"));
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                final String dir = getCacheDir() + "/picFolder/";
                //final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
                File newdir = new File(dir);
                newdir.mkdirs();

                String file = dir + "temp.jpeg";//camera nahi hua app open hua with white screen hello world ha ik red b nhi hua?no sensor dek if working
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                    try (FileOutputStream out = new FileOutputStream(newfile.getAbsoluteFile())) {
                        image.compress(Bitmap.CompressFormat.JPEG, 50, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                        String imgBitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), image, "title", null);
                        Uri imgUri = Uri.parse(imgBitmapPath);

                        //Uri imgUri = Uri.parse(newfile.getAbsolutePath());
                        Log.d("AAA", "" + imgUri);
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.setType("text/plain");
                        whatsappIntent.setPackage("com.whatsapp");
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Sending photo of shop " + getIntent().getStringExtra("shop_name"));
                        whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                        whatsappIntent.setType("image/*");
                        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivity(whatsappIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /*Uri outputFileUri = Uri.fromFile(newfile);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);*/
                } catch (IOException ignored) {
                }
                Log.d("AA", "" + image);
                //Toast.makeText(Vithis, "" + image, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }

    public void finis(View v) {
        finish();
    }
}
