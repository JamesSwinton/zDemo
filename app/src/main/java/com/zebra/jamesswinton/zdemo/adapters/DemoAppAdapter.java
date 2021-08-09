package com.zebra.jamesswinton.zdemo.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.zebra.jamesswinton.zdemo.R;
import com.zebra.jamesswinton.zdemo.data.DemoCategory;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs.DemoApp;
import com.zebra.jamesswinton.zdemo.data.DemoConfigs.DemoCategories;
import com.zebra.jamesswinton.zdemo.databinding.AdapterAppLayoutBinding;
import com.zebra.jamesswinton.zdemo.databinding.AdapterEmptyLayoutBinding;
import com.zebra.jamesswinton.zdemo.databinding.LayoutSingleAppBinding;
import com.zebra.jamesswinton.zdemo.utils.StringUtils;
import java.util.List;

public class DemoAppAdapter extends RecyclerView.Adapter<ViewHolder> {

  // Variables
  private Context mCx;
  private List<DemoCategories> mDemoCategories;

  // View Holder Types
  private static final int APP_LAYOUT_VH = 0;
  private static final int EMPTY_LAYOUT_VH = 1;

  // UI Holdings
  private static final int AppsPerRow = 4;
  private static final int ColumnMargin = 16;

  // Row Params
  private static final LayoutParams RowLayoutParams =
      new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

  // Card Backgrounds
  private static final int[] CardBackgrounds = {
//      R.drawable.gradient_turquoise, R.drawable.gradient_pink, R.drawable.gradient_blue,
//      R.drawable.gradient_orange, R.drawable.gradient_purple, R.drawable.gradient_light_orange
      R.drawable.gradient_6, R.drawable.gradient_4, R.drawable.gradient_3, R.drawable.gradient_2,
      R.drawable.gradient_1, R.drawable.gradient_5, R.drawable.gradient_7
  };

  private static final LayoutParams ColumnLayoutParams =
      new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 10 / AppsPerRow);


  public DemoAppAdapter(Context cx, List<DemoCategories> demoCategories) {
    this.mCx = cx;
    this.mDemoCategories = demoCategories;

    // Setup Row / Column Params
    RowLayoutParams.gravity = Gravity.CENTER;
    ColumnLayoutParams.topMargin = ColumnMargin;
    ColumnLayoutParams.bottomMargin = ColumnMargin;
    ColumnLayoutParams.leftMargin = ColumnMargin;
    ColumnLayoutParams.rightMargin = ColumnMargin;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == EMPTY_LAYOUT_VH) {
      return new EmptyLayoutViewHolder(DataBindingUtil.inflate(LayoutInflater.from(mCx),
          R.layout.adapter_empty_layout, parent, false));
    } else {
      return new AppLayoutViewHolder(DataBindingUtil.inflate(LayoutInflater.from(mCx),
          R.layout.adapter_app_layout, parent, false));
    }
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    if (holder instanceof AppLayoutViewHolder) {
      AppLayoutViewHolder vh = (AppLayoutViewHolder) holder;
      vh.dataBinding.layoutAppRowHolder.removeAllViews();

      DemoCategories demoCategory = mDemoCategories.get(position);
      DemoCategory category = demoCategory.getCategoryEnum();

      List<DemoApp> demoApps = mDemoCategories.get(position).getDemoApps();
      if (demoApps == null || demoApps.isEmpty()) {
        vh.dataBinding.getRoot().setVisibility(View.GONE);
        vh.dataBinding.cardView.setVisibility(View.GONE);
        for (int i = 0; i < vh.dataBinding.cardView.getChildCount(); i++) {
          vh.dataBinding.cardView.getChildAt(i).setVisibility(View.GONE);
        }
      } else {
        vh.dataBinding.getRoot().setVisibility(View.VISIBLE);
        vh.dataBinding.cardView.setVisibility(View.VISIBLE);
        for (int i = 0; i < vh.dataBinding.cardView.getChildCount(); i++) {
          vh.dataBinding.cardView.getChildAt(i).setVisibility(View.VISIBLE);
        }

        vh.dataBinding.groupTitle.setText(StringUtils.toTitleCase(category.name()));
        vh.dataBinding.groupDesc.setText(mDemoCategories.get(position).getCategoryDescription());
        vh.dataBinding.groupIcon.setImageResource(mDemoCategories.get(position).getIcon());
        vh.dataBinding.baseLayout.setBackgroundResource(CardBackgrounds[position % CardBackgrounds.length]);

        // Init Expand Listener
        vh.dataBinding.cardView.setOnClickListener(v -> {
          int appsVisible = vh.dataBinding.layoutAppRowHolder.getVisibility();
          if (appsVisible == View.VISIBLE) {
            vh.dataBinding.layoutAppRowHolder.setVisibility(View.GONE);
            vh.dataBinding.expandButton.setImageResource(R.drawable.ic_expand_more);
          } else {
            vh.dataBinding.layoutAppRowHolder.setVisibility(View.VISIBLE);
            vh.dataBinding.expandButton.setImageResource(R.drawable.ic_expand_less);
          }
        });

        // Create Rows for Apps
        double numberOfRows = Math.ceil((double) demoApps.size() / AppsPerRow);

        // Create Rows
        for (int r = 0; r < numberOfRows; r++) {
          LinearLayout row = new LinearLayout(mCx);
          row.setLayoutParams(RowLayoutParams);
          row.setWeightSum(10);
          row.setTag("row");

          // Create Columns
          for (int c = 0; c < AppsPerRow; c++) {
            // Get Demo App Index
            int index = r == 0 ? c : (r * AppsPerRow) + c;
            if (index >= demoApps.size()) { break; }

            // Build Column Data
            DemoApp demoApp = demoApps.get(index);
            LayoutSingleAppBinding columnBinding = DataBindingUtil.inflate(
                LayoutInflater.from(mCx), R.layout.layout_single_app,
                null, false);
            columnBinding.baseLayout.setLayoutParams(ColumnLayoutParams);
            columnBinding.appIcon.setBackground(demoApp.getAppIcon());
            columnBinding.appLabel.setText(demoApp.getAppLabel());

            // Set Click Listener
            columnBinding.baseLayout.setOnClickListener(
                v -> mCx.startActivity(demoApp.getLaunchIntent()));

            // Add column to row
            row.addView(columnBinding.getRoot());
          }

          vh.dataBinding.layoutAppRowHolder.addView(row, vh.dataBinding.layoutAppRowHolder.getChildCount());
        }
      }
    }
  }

  @Override
  public int getItemViewType(int position) {
    return mDemoCategories == null || mDemoCategories.isEmpty() ? EMPTY_LAYOUT_VH : APP_LAYOUT_VH;
  }

  @Override
  public int getItemCount() {
    return mDemoCategories == null || mDemoCategories.isEmpty() ? 1 : mDemoCategories.size();
  }

  public void updateList(List<DemoCategories> demoCategories) {
    this.mDemoCategories = demoCategories;
    notifyDataSetChanged();
  }

  private class AppLayoutViewHolder extends ViewHolder {
    public AdapterAppLayoutBinding dataBinding;
    public AppLayoutViewHolder(@NonNull AdapterAppLayoutBinding viewBinding) {
      super(viewBinding.getRoot());
      dataBinding = viewBinding;
    }
  }

  private class EmptyLayoutViewHolder extends ViewHolder {
    public AdapterEmptyLayoutBinding dataBinding;
    public EmptyLayoutViewHolder(@NonNull AdapterEmptyLayoutBinding viewBinding) {
      super(viewBinding.getRoot());
      dataBinding = viewBinding;
    }
  }
}
