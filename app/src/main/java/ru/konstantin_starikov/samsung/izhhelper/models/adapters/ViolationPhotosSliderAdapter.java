package ru.konstantin_starikov.samsung.izhhelper.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;

public class ViolationPhotosSliderAdapter extends
        SliderViewAdapter<ViolationPhotosSliderAdapter.SliderAdapterVH>{

    private Context context;
    private List<String> violationPhotos = new ArrayList<>();

    public ViolationPhotosSliderAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<String> violationPhotos) {
        this.violationPhotos = violationPhotos;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.violationPhotos.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(String violationPhoto) {
        this.violationPhotos.add(violationPhoto);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.violation_photos_slider_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        String sliderItem = violationPhotos.get(position);

        File file = new File(Helper.getFullPathFromDataDirectory(sliderItem, context));
        Picasso.get().load(file).fit().centerCrop().into(viewHolder.violationPhotoImageView);
    }

    @Override
    public int getCount() {
        return violationPhotos.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView violationPhotoImageView;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            violationPhotoImageView = itemView.findViewById(R.id.violationPhotoSlider);
            this.itemView = itemView;
        }
    }
}
