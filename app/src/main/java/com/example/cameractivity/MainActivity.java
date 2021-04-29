package com.example.cameractivity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.factor.bouncy.BouncyRecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.rec)
    BouncyRecyclerView recyclerView;
    SQLiteDatabase database;
    DataBaseHelper mDb2;

    @BindView(R.id.cardView)
    CardView cardView;

    @BindView(R.id.indi)
    LinearProgressIndicator indicator;

    @BindView(R.id.search_edt)
    androidx.appcompat.widget.SearchView searchEdit;

    ArrayList<ItemHolder> itemHolders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);*/
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Fresco.initialize(this);
        mDb2 = new DataBaseHelper(this);
        /*String[] strings = new String[]{"one", "two", "three", "four", "five", "six", "sev", "eig"};
        String[] offers = new String[]{"23", "54", "46", "10", "55", "70", "50", "80"};
        String[] s = new String[]{"The Full Cart",
                "Dollar Savings Store",
                "Healthy Treats",
                "Underground Finds Online",
                "Fun Times Online Shop",
                "Privacy Please Online Store",
                "We Care Online Store",
                "Farm to Shelf"};
        mDb2 = new DataBaseHelper(this);
        //DatabaseExec databaseExec = new DatabaseExec(mDb2);

        mDb2.createDataBase();
        mDb2.openDataBase();
        database = mDb2.getWritableDatabase();
        //databaseExec.deleteTable("ShopList");
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < s.length; i++) {
            contentValues.put("name", s[i]);
            contentValues.put("offer", offers[i]);
            contentValues.put("id", i + "");
            contentValues.put("shop_url", "https://github.com/Dixzz/dump/raw/main/" + strings[i] + ".jpeg");
            database.insert("ShopList", null, contentValues);
        }

        contentValues = new ContentValues();
        for (int i = 0; i <= 3; i++) {
            contentValues.put("prod_name", "Something");
            contentValues.put("prod_price", 100);
            contentValues.put("prod_desc", "some desc");
            contentValues.put("prod_parent", "The Full Cart");
            database.insert("ShopProducts", null, contentValues);
        }
        for (int i = 4; i <= 9; i++) {
            contentValues.put("prod_name", "Something");
            contentValues.put("prod_price", 200);
            contentValues.put("prod_desc", "some desc");
            contentValues.put("prod_parent", "Healthy Treats");
            database.insert("ShopProducts", null, contentValues);
        }
        for (int i = 10; i <= 15; i++) {
            contentValues.put("prod_name", "Something");
            contentValues.put("prod_price", 300);
            contentValues.put("prod_desc", "some desc" + i);
            contentValues.put("prod_parent", "Farm to Shelf");
            database.insert("ShopProducts", null, contentValues);
        }
        //database.close();
        mDb2.close();*/

        cardView.postDelayed(() -> {
            int centerX = cardView.getWidth() / 2;
            int endRadius = (int) Math.hypot(cardView.getWidth(), cardView.getHeight());

            Animator reveal = ViewAnimationUtils.createCircularReveal(cardView, centerX, 0,
                    0, endRadius);
            reveal.setDuration(1500);
            reveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    Animator a = ObjectAnimator.ofFloat(findViewById(R.id.container), View.ALPHA, 0f, 1f);
                    a.setStartDelay(700);
                    a.setDuration(1000);
                    a.setInterpolator(new AccelerateDecelerateInterpolator());
                    a.start();
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            reveal.start();
        }, 0);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 101);
        }

        /*ada ada = new ada(list);
        ada.setAnimationEnable(false);
        ada.setAnimationFirstOnly(false);
        ada.setAdapterAnimation(view -> new Animator[0]);
        ada.setAnimationWithDefault(BaseQuickAdapter.AnimationType.AlphaIn);
        recyclerView.setAdapter(ada);*/

        /*List<commonAdapterItems> commonAdapterItems = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            commonAdapterItems.add(new commonAdapterItems(0 + i + "", new ArrayList<>()));
        }*/

        //recyclerView.setAdapter(new commonAdapter(Collections.singletonList(new GroupItems("Deliveries", commonAdapterItems))));
        adapter adapter = new adapter(itemHolders);
        adapter.setOnClickItem(new adapter.onClickItem() {
            @Override
            public void onClick(int p) {
                Intent intent = new Intent(MainActivity.this, ViewItem.class);
                intent.putExtra("shop_name", itemHolders.get(p).name);
                intent.putExtra("shop_image", itemHolders.get(p).shopeImage);
                intent.putExtra("shop_offer", itemHolders.get(p).offer);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        Cursor c = readShopList();
        //c.moveToFirst();

        Log.d("AAA", "" + c.getCount());
        while (c.moveToNext()) {
            itemHolders.add(new ItemHolder(
                    c.getString(c.getColumnIndex("offer")),
                    c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("shop_url"))
            ));
        }
        adapter.notifyDataSetChanged();
        c.close();


        searchEdit.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(MainActivity.this, " ", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    indicator.setIndeterminate(false);
                    indicator.setVisibility(View.INVISIBLE);
                    Cursor c = readShopList();
                    //c.moveToFirst();

                    itemHolders.clear();
                    Log.d("AAA", "" + c.getCount());
                    while (c.moveToNext()) {
                        itemHolders.add(new ItemHolder(
                                c.getString(c.getColumnIndex("offer")),
                                c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("shop_url"))
                        ));
                    }
                    adapter.notifyDataSetChanged();
                    c.close();
                } else if (newText.trim().length() > 0) {
                    Cursor c = readShopList(String.format("SELECT * from ShopList where name IN (SELECT prod_parent FROM ShopProducts WHERE prod_name like '%%%s%%')", newText));
                    //c.moveToFirst();

                    Log.d("AAA", "" + c.getCount());
                    itemHolders.clear();
                    while (c.moveToNext()) {
                        itemHolders.add(new ItemHolder(
                                c.getString(c.getColumnIndex("offer")),
                                c.getString(c.getColumnIndex("name")), c.getString(c.getColumnIndex("shop_url"))
                        ));
                    }
                    adapter.notifyDataSetChanged();
                    indicator.setIndeterminate(true);
                    indicator.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    private Cursor readShopList(String sql) {
        SQLiteDatabase database;
        DataBaseHelper mDb2;
        mDb2 = new DataBaseHelper(this);
        mDb2.createDataBase();
        mDb2.openDataBase();
        database = mDb2.getWritableDatabase();
        try {
            Cursor mCur = database.rawQuery(sql, null);
            return mCur;
        } catch (SQLException mSQLException) {
            mSQLException.printStackTrace();
        }
        mDb2.close();
        database.close();
        return null;
    }

    public Cursor readShopList() {
        mDb2.createDataBase();
        mDb2.openDataBase();
        database = mDb2.getWritableDatabase();
        try {
            String sql = "SELECT * FROM ShopList";
            Cursor mCur = database.rawQuery(sql, null);
            mCur.getCount();//Log.e(TAG, "getVec: " + "Empty");
            return mCur;
        } catch (SQLException mSQLException) {
            mSQLException.printStackTrace();
        }
        mDb2.close();
        database.close();
        return null;
    }

    public static class ItemHolder implements Serializable {
        String offer;
        String name;
        String shopeImage;

        public ItemHolder(String offer, String name, String shopeImage) {
            this.offer = offer;
            this.name = name;
            this.shopeImage = shopeImage;
        }
    }

    /*private static class ada extends BaseQuickAdapter<String, BaseViewHolder> {
        BaseViewHolder baseViewHolder = null;

        public ada(List<String> list) {
            super(android.R.layout.simple_list_item_1, list);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, String s) {
            ((TextView) baseViewHolder.itemView).setText(baseViewHolder.getAdapterPosition() + 1 + "");
            ViewPropertyAnimator b = ((TextView) baseViewHolder.itemView).animate();

            b.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    ((TextView) baseViewHolder.itemView).animate().translationY(0f).setDuration(250).start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            b.setDuration(500);
            b.translationY(30f);
            b.setStartDelay(50);
            b.start();
        }
    }*/


    /*
        private static class commonAdapterItems implements Parcelable {
            private final String title;
            private final List<String> items;

            public commonAdapterItems(String t, List<String> i) {
                this.title = t;
                this.items = i;
            }

            protected commonAdapterItems(Parcel in) {

                title = in.readString();
                byte hasItems = in.readByte();
                int size = in.readInt();
                if (hasItems == 0x01) {
                    items = new ArrayList<String>(size);
                    Class<?> type = (Class<?>) in.readSerializable();
                    in.readList(items, type.getClassLoader());
                } else {
                    items = null;
                }
            }

            private final Creator<commonAdapterItems> CREATOR = new Creator<commonAdapterItems>() {
                @Override
                public commonAdapterItems createFromParcel(Parcel in) {
                    return new commonAdapterItems(in);
                }

                @Override
                public commonAdapterItems[] newArray(int size) {
                    return new commonAdapterItems[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
            }
        }

        private static class GroupItems extends ExpandableGroup<commonAdapterItems> {

            public GroupItems(String title, List<commonAdapterItems> items) {
                super(title, items);
            }
        }

        private static class commonAdapter extends ExpandableRecyclerViewAdapter<commonAdapter.TitleHolder, commonAdapter.ChildHolder> {

            public commonAdapter(List<? extends ExpandableGroup> groups) {
                super(groups);
            }

            @Override
            public TitleHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
                return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
            }

            @Override
            public ChildHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
                return new ChildHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false));
            }

            @Override
            public void onBindChildViewHolder(ChildHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
                ((TextView) holder.itemView).setText(((commonAdapterItems) group.getItems().get(childIndex)).title);

                ViewPropertyAnimator b = holder.itemView.animate();
                b.setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        b.translationY(0f).setDuration(250).start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                b.setDuration(500);
                b.translationY(30f);
                b.start();
            }

            @Override
            public void onBindGroupViewHolder(TitleHolder holder, int flatPosition, ExpandableGroup group) {
                ((TextView) holder.itemView).setText(group.getTitle());
            }

            static class TitleHolder extends GroupViewHolder {
                public TitleHolder(View itemView) {
                    super(itemView);
                }
            }

            static class ChildHolder extends ChildViewHolder {
                public ChildHolder(View itemView) {
                    super(itemView);
                }
            }
        }*/
}