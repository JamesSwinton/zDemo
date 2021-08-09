package com.zebra.jamesswinton.zdemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.zebra.jamesswinton.zdemo.R;
import com.zebra.jamesswinton.zdemo.data.DownloadableDemos;
import com.zebra.jamesswinton.zdemo.data.DownloadableDemos.DownloadableDemo;
import com.zebra.jamesswinton.zdemo.databinding.AdapterInstallDemoBinding;

public class InstallDemosAdapter extends RecyclerView.Adapter<ViewHolder> {

  // Variables
  private Context mCx;
  private DownloadableDemos mDownloadableDemos;
  private OnDemoCheckChangedListener mOnDemoCheckChangedListener;

  public InstallDemosAdapter(Context cx, DownloadableDemos downloadableDemos,
      OnDemoCheckChangedListener onDemoCheckedChangeListener) {
    this.mCx = cx;
    this.mDownloadableDemos = downloadableDemos;
    this.mOnDemoCheckChangedListener = onDemoCheckedChangeListener;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new InstallDemoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(mCx),
        R.layout.adapter_install_demo, null, false));
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    InstallDemoViewHolder vh = (InstallDemoViewHolder) holder;
    DownloadableDemo downloadableDemo = mDownloadableDemos.getDownloadableDemos().get(position);
    vh.dataBinding.label.setText(downloadableDemo.getName());
    vh.dataBinding.desc.setText(downloadableDemo.getDescription());
    vh.dataBinding.icon.setImageResource(downloadableDemo.getIcon());
    vh.dataBinding.checkbox.setOnCheckedChangeListener((buttonView, isChecked)
        -> mOnDemoCheckChangedListener.onCheckChanged(downloadableDemo, isChecked));
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return mDownloadableDemos.getDownloadableDemos().size() == 0 ? 1 : mDownloadableDemos.getDownloadableDemos().size();
  }

  private class InstallDemoViewHolder extends ViewHolder {
    public AdapterInstallDemoBinding dataBinding;
    public InstallDemoViewHolder(@NonNull AdapterInstallDemoBinding viewBinding) {
      super(viewBinding.getRoot());
      dataBinding = viewBinding;
    }
  }

  public interface OnDemoCheckChangedListener {
    void onCheckChanged(DownloadableDemo downloadableDemo, boolean isChecked);
  }

}
