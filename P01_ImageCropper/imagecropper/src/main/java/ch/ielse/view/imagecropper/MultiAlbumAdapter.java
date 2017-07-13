package ch.ielse.view.imagecropper;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

class MultiAlbumAdapter extends RecyclerView.Adapter {
    private final List<AlbumPicture> pictureList = new ArrayList<>();
    private final List<AlbumPicture> selectedPictures = new ArrayList<>();
    private final int fetchCount;

    private Callback cb;

    MultiAlbumAdapter(int count) {
        fetchCount = count;
    }

    MultiAlbumAdapter setCallback(Callback callback) {
        cb = callback;
        return this;
    }

    List<String> getSelectedPictureUrls() {
        List<String> selectedPictureUrls = new ArrayList<>();
        for (AlbumPicture albumPicture : selectedPictures) {
            selectedPictureUrls.add(albumPicture.image);
        }
        return selectedPictureUrls;
    }

    static class AlbumPicture {
        String image;
        boolean isSelected;
        ViewHolder viewHolder;

        static AlbumPicture create(String image) {
            AlbumPicture albumPicture = new AlbumPicture();
            albumPicture.image = image;
            return albumPicture;
        }

        void bind(ViewHolder vh) {
            viewHolder = vh;
        }

        void refresh() {
            if (viewHolder != null) viewHolder.refresh(viewHolder.ap);
        }
    }

    void set(@NonNull List<AlbumPicture> dataList) {
        pictureList.clear();
        pictureList.addAll(dataList);
        notifyDataSetChanged();
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tSelected2;
        ImageView iPicture, iSelected;
        AlbumPicture ap;


        ViewHolder(View itemView) {
            super(itemView);
            (iPicture = (ImageView) itemView.findViewById(R.id.i_picture)).setOnClickListener(this);
            iSelected = (ImageView) itemView.findViewById(R.id.i_selected);
            tSelected2 = (TextView) itemView.findViewById(R.id.t_selected_2);
        }

        @Override
        public void onClick(View v) {
            if (v == iPicture) {
                ap.isSelected = !ap.isSelected;
                handleItemStateChanged(this);
            }
        }

        void refresh(@NonNull AlbumPicture data) {
            ap = data;
            ap.bind(this);

            Glide.with(iPicture.getContext()).load(ap.image).into(iPicture);
            iSelected.setVisibility(ap.isSelected ? View.VISIBLE : View.INVISIBLE);
            tSelected2.setVisibility(ap.isSelected ? View.VISIBLE : View.INVISIBLE);
            tSelected2.setText(String.valueOf(selectedPictures.indexOf(ap) + 1));
        }
    }

    private void handleItemStateChanged(ViewHolder vh) {
        if (vh.ap.isSelected) {
            if (selectedPictures.size() >= fetchCount) {
                Toast.makeText(vh.itemView.getContext().getApplicationContext(), "最多选择" + fetchCount + "张图片", Toast.LENGTH_SHORT).show();
                vh.ap.isSelected = false;
            } else {
                if (!selectedPictures.contains(vh.ap)) {
                    selectedPictures.add(vh.ap);
                }
                vh.refresh(vh.ap);

                for (AlbumPicture albumPicture : selectedPictures) {
                    albumPicture.refresh();
                }

                if (cb != null) cb.onItemStateChanged(selectedPictures.size() + "/" + fetchCount);
            }
        } else {
            if (selectedPictures.contains(vh.ap)) {
                selectedPictures.remove(vh.ap);
            }
            vh.refresh(vh.ap);

            for (AlbumPicture albumPicture : selectedPictures) {
                albumPicture.refresh();
            }

            if (cb != null) cb.onItemStateChanged(selectedPictures.size() + "/" + fetchCount);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_album_recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).refresh(pictureList.get(position));
    }

    @Override
    public int getItemCount() {
        return pictureList.size();
    }


    interface Callback {
        void onItemStateChanged(String fetchInfo);
    }
}
