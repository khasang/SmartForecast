package com.khasang.forecast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khasang.forecast.R;
import com.khasang.forecast.adapters.view_holders.RecyclerEmptyViewHolder;
import com.khasang.forecast.adapters.view_holders.RecyclerItemViewHolder;
import com.khasang.forecast.position.PositionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CopyPasteStd on 29.11.15.
 * Адаптер для выбора карточек городов
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int footerHeight;
    List <String> newCities;

    private enum ItemType {
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

    private List<String> mItemList;
    private final View.OnClickListener mListener;
    private final View.OnLongClickListener mLongListener;

    public RecyclerAdapter(List<String> itemList, View.OnClickListener mListener, View.OnLongClickListener mLongListener) {
        mItemList = itemList;
        this.mListener = mListener;
        this.mLongListener = mLongListener;
        newCities = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == ItemType.CARD_VIEW.number()) {
            final View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.recycler_item, parent, false);
            return new RecyclerItemViewHolder(view, mListener, mLongListener);
        } else if (viewType == ItemType.HEADER.number()) {
            final View view = LayoutInflater.from(context).inflate(R.layout.recycler_header, parent, false);
            return new RecyclerEmptyViewHolder(view);
        } else if (viewType == ItemType.FOOTER.number()) {
            final View view = LayoutInflater.from(context).inflate(R.layout.recycler_footer, parent, false);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = footerHeight;
            view.requestLayout();
            return new RecyclerEmptyViewHolder(view);
        }
        throw new RuntimeException("There is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (!(isPositionHeader(position) || isLastPosition(position))) {
            RecyclerItemViewHolder holder = (RecyclerItemViewHolder) viewHolder;
            String itemText = mItemList.get(position - 1); // we are taking header in to account so all of our items are correctly positioned
            if (newCities.contains(itemText)) {
                holder.markCityAsNew(true);
            } else {
                holder.markCityAsNew(false);
            }
            holder.setItemFavoriteState(PositionManager.getInstance().isFavouriteCity(itemText));
            holder.setItemText(itemText);
        }
    }

    public int getBasicItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    //our new getItemCount() that includes header View
    @Override
    public int getItemCount() {
        return getBasicItemCount() + 1 + 1; // header
    }

    // returns viewType for a given position
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return ItemType.HEADER.number();
        }
        if (isLastPosition(position)) {
            return ItemType.FOOTER.number();
        }
        return ItemType.CARD_VIEW.number();
    }

    // check if given position is a header
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isLastPosition(int position) {
        return position == getItemCount() - 1;
    }

    public void setFooterHeight(int height) {
        footerHeight = height * 4;
    }

    public void addCityToNewLocationsList(String city) {
        newCities.add(city);
    }
}
