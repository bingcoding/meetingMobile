package io.agora.openlive.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hezy.guide.phone.BaseApplication;
import com.hezy.guide.phone.BuildConfig;
import com.hezy.guide.phone.R;
import com.hezy.guide.phone.business.ViewPagerActivity;
import com.hezy.guide.phone.entities.AudienceVideo;
import com.hezy.guide.phone.entities.ChatMesData;
import com.hezy.guide.phone.utils.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AudienceVideoAdapter extends RecyclerView.Adapter<AudienceVideoAdapter.AudienceVideoViewHolder> {

    private Context context;
    private ArrayList<AudienceVideo> audienceVideos = new ArrayList<AudienceVideo>();

    public AudienceVideoAdapter(Context context) {
        this.context = context;
    }

    public  synchronized void insertItem(AudienceVideo audienceVideo) {
        this.audienceVideos.add(audienceVideos.size(), audienceVideo);
        notifyItemInserted(audienceVideos.size());
    }

    public synchronized void deleteItem(AudienceVideo audienceVideo) {
        int position = audienceVideos.indexOf(audienceVideo);
        this.audienceVideos.remove(audienceVideo);
        notifyItemRemoved(position);
    }

    public void setVolumeByUid(int uid, int volume){
        for (AudienceVideo audienceVideo : audienceVideos) {
            if (audienceVideo.getUid() == uid) {
                Log.v("update_volume", "1--" + audienceVideo.toString());
                audienceVideo.setVolume(volume);
                int position = audienceVideos.indexOf(audienceVideo);
                Log.v("update_volume", "2--" + audienceVideo.toString() + "--position--" + position);
                notifyItemChanged(position, 1);
            } else {
                Log.v("update_volume", "不是更新它");
            }
        }
    }

    public void setMutedStatusByUid(int uid, boolean muted){
        for (AudienceVideo audienceVideo : audienceVideos) {
            if (audienceVideo.getUid() == uid) {
                Log.v("update_mute", "1--" + audienceVideo.toString());
                audienceVideo.setMuted(muted);
                int position = audienceVideos.indexOf(audienceVideo);
                Log.v("update_mute", "2--" + audienceVideo.toString() + "--position--" + position);
                notifyItemChanged(position, 1);
            } else {
                Log.v("update_mute", "不是更新它");
            }
        }
    }

    @Override
    public AudienceVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meeting_audience_video, parent, false);
        return new AudienceVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AudienceVideoViewHolder holder, final int position) {
        final AudienceVideo audienceVideo = audienceVideos.get(position);

        holder.nameText.setText(audienceVideo.getName());
        if (BuildConfig.DEBUG) {
            holder.nameText.setVisibility(View.VISIBLE);
        } else {
            holder.nameText.setVisibility(View.GONE);
        }

        if (audienceVideo.isBroadcaster()) {
            holder.labelText.setVisibility(View.VISIBLE);
        } else {
            holder.labelText.setVisibility(View.GONE);
        }

        if (audienceVideo.isMuted() || audienceVideo.getVolume() <= 100) {
            holder.volumeImage.setVisibility(View.GONE);
        } else {
            holder.volumeImage.setVisibility(View.VISIBLE);
        }

        if (holder.videoLayout.getChildCount() > 0) {
            holder.videoLayout.removeAllViews();
         }
        final SurfaceView surfaceView = audienceVideo.getSurfaceView();
        stripSurfaceView(surfaceView);
        holder.videoLayout.addView(surfaceView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onBindViewHolder(@NonNull AudienceVideoViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            final AudienceVideo audienceVideo = audienceVideos.get(position);

            if (audienceVideo.isMuted() || audienceVideo.getVolume() <= 100) {
                holder.volumeImage.setVisibility(View.GONE);
            } else {
                holder.volumeImage.setVisibility(View.VISIBLE);
            }

        }
    }

    private void stripSurfaceView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }

    @Override
    public int getItemCount() {
        return audienceVideos != null ? audienceVideos.size() : 0;
    }

    public class AudienceVideoViewHolder extends RecyclerView.ViewHolder {

        private TextView labelText, nameText;
        private FrameLayout videoLayout;
        private ImageView volumeImage;

        public AudienceVideoViewHolder(View itemView) {
            super(itemView);
            videoLayout = itemView.findViewById(R.id.audience_video_layout);
            labelText = itemView.findViewById(R.id.label);
            nameText = itemView.findViewById(R.id.name);
            volumeImage = itemView.findViewById(R.id.volume_status);

        }
    }

}

