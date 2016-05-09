package com.khasang.forecast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Имеет возможность устанавливать отступы сверху и снизу RecyclerView.
 */
public abstract class HeaderFooterAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> itemList;
    private int headerHeight;
    private int footerHeight;

    public HeaderFooterAdapter(List<T> itemList) {
        this.itemList = itemList;
    }

    public void setHeaderHeight(int height) {
        headerHeight = height;
    }

    public void setFooterHeight(int height) {
        footerHeight = height;
    }

    protected boolean isHeaderPosition(int position) {
        return position == 0;
    }

    protected boolean isFooterPosition(int position) {
        return position == getItemCount() - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == ItemType.HEADER.number()) {
            View view = new View(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, headerHeight);
            view.setLayoutParams(params);
            return new EmptyViewHolder(view);
        } else if (viewType == ItemType.FOOTER.number()) {
            View view = new View(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MATCH_PARENT, footerHeight);
            view.setLayoutParams(params);
            return new EmptyViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1 + 1; // +header, +footer
    }

    private int getBasicItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return ItemType.HEADER.number();
        }
        if (isFooterPosition(position)) {
            return ItemType.FOOTER.number();
        }
        return ItemType.CARD.number();
    }

    private enum ItemType {
        HEADER {
            @Override
            public int number() {
                return 1;
            }
        },
        CARD {
            @Override
            public int number() {
              return 2;
            }
        },
        FOOTER {
            @Override
            public int number() {
              return 3;
            }
        };

        public abstract int number();
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
