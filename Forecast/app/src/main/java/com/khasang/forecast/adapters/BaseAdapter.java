package com.khasang.forecast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.khasang.forecast.R;
import com.khasang.forecast.adapters.view_holders.RecyclerEmptyViewHolder;
import com.khasang.forecast.adapters.view_holders.RecyclerItemViewHolder;
import java.util.List;

/**
 * Базовый адаптер для RecyclerView
 *
 * Имеет возможность кастомизировать первый и последний элемент в списке
 */
public class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private List<T> itemList;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private int footerHeight;

    public BaseAdapter(List<T> itemList, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.itemList = itemList;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == ItemType.HEADER.number()) {
          View view = LayoutInflater.from(context).inflate(R.layout.recycler_header, parent, false);
          return new RecyclerEmptyViewHolder(view);
        } else if (viewType == ItemType.FOOTER.number()) {
          View view = LayoutInflater.from(context).inflate(R.layout.recycler_footer, parent, false);
          ViewGroup.LayoutParams params = view.getLayoutParams();
          params.height = footerHeight;
          view.requestLayout();
          return new RecyclerEmptyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(isHeaderPosition(position) || isFooterPosition(position))) {
            RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;

          // we are taking header in to account so all of our items are correctly positioned
          //  String itemText = itemList.get(position - 1);

            holder.markCityAsNew(false);

            //holder.setItemFavoriteState(PositionManager.getInstance().isFavouriteCity(itemText));
            //holder.setItemText(itemText);
        }
    }

    public int getBasicItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1 + 1; // +header, +footer
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return ItemType.HEADER.number();
        }
        if (isFooterPosition(position)) {
            return ItemType.FOOTER.number();
        }
        return ItemType.CARD_VIEW.number();
    }

    private boolean isHeaderPosition(int position) {
        return position == 0;
    }

    private boolean isFooterPosition(int position) {
        return position == getItemCount() - 1;
    }

    public void setFooterHeight(int height) {
        footerHeight = height * 4;
    }

    protected enum ItemType {
        CARD_VIEW {
            @Override
            public int number() {
              return 1;
            }
        },
        HEADER {
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
}
