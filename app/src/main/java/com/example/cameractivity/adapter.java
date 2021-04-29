package com.example.cameractivity;

import android.animation.Animator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

class adapter extends RecyclerView.Adapter<adapter.holder> {
    private boolean anim = false;
    private onClickItem onClickItem;
    private final ArrayList<MainActivity.ItemHolder> itemHolders;

    public adapter(ArrayList<MainActivity.ItemHolder> itemHolders) {
        this.itemHolders = itemHolders;
    }

    public void setOnClickItem(adapter.onClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public adapter.holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.prod_item_holder, parent, false);
        return new adapter.holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            onClickItem.onClick(position);
        });
        holder.shopName.setText(itemHolders.get(position).name);
        holder.offer.setText(itemHolders.get(position).offer + "%");
        holder.imageView.setImageURI(itemHolders.get(position).shopeImage);

        if (!anim && position >= 0 && position <= 20) {
            holder.itemView.setAlpha(0f);
            ViewPropertyAnimator b = (holder.itemView).animate();

            b.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    (holder.itemView).animate().alpha(1f).setDuration(250).start();
                    anim = true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            b.setDuration((long) (100 * (position + 1) * .5));
            b.setStartDelay((long) (100 * (position + 1) * 1.25));
            b.alpha(0.5f);
            b.start();
        }
    }

    @Override
    public int getItemCount() {
        return itemHolders.size();
    }

    static class holder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        TextView offer, shopName;

        public holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.shopImage);
            offer = itemView.findViewById(R.id.offerNo);
            shopName = itemView.findViewById(R.id.shopName);
        }
    }

    interface onClickItem {
        void onClick(int p);
    }
}