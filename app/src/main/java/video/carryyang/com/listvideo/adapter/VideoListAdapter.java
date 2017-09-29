package video.carryyang.com.listvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import video.carryyang.com.listvideo.DetailActivity;
import video.carryyang.com.listvideo.MainApplication;
import video.carryyang.com.listvideo.R;
import video.carryyang.com.listvideo.utils.AnimationUtils;
import video.carryyang.com.listvideo.videoview.MyTextureView;

/**
 * Created by yangshao on 2017/9/26.
 */

public class VideoListAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<String> urlList = new ArrayList<>();
    private List<String> coverList = new ArrayList<>();


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        RecyclerView.ViewHolder viewHolder = new VideoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final VideoViewHolder mHolder = (VideoViewHolder) holder;
        mHolder.cover.setImageURI(coverList.get(position));
        mHolder.cover.setVisibility(View.VISIBLE);
        mHolder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHolder.tv.setMediaPlayer(MainApplication.getInstance().getMediaPlayer());
                mHolder.tv.setPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mHolder.tv.startPlay();
                    }
                });
                mHolder.tv.setUrl(urlList.get(position)) ;
                AnimationUtils.fadeOut(mHolder.cover);
            }
        });
        mHolder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHolder.tv.pausePlay();
            }
        });

        mHolder.goDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainApplication.getInstance().getMediaPlayer().pause();
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("path", urlList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (coverList == null) {
            return 0;
        } else {
            return coverList.size();
        }
    }

    public VideoListAdapter(Context context) {
        this.context = context;
        coverList.add("http://img7.leappmusic.cc/20160621/a3072b47a-376e-11e6-b9bd-525400475748.gen.webp");
        coverList.add("http://img7.leappmusic.cc/20160621/f20a0db1c-376e-11e6-b9bd-525400475748.crop.webp");
        coverList.add("http://img7.leappmusic.cc/20160621/a3072b47a-376e-11e6-b9bd-525400475748.gen.webp");
        coverList.add("http://img7.leappmusic.cc/20160621/j1e427ee8-376e-11e6-b9bd-525400475748.ori.webp");
        coverList.add("http://img7.leappmusic.cc/20160623/efbac87be-38dc-11e6-99c1-525400475748.ori.webp");
        coverList.add("http://img7.leappmusic.cc/20160623/hee654f64-38dc-11e6-99c1-525400475748.ori.webp");
        urlList.add("http://v7.leappmusic.cc/t720p/20160621/a3072b47a-376e-11e6-b9bd-525400475748.mp4");
        urlList.add("http://v7.leappmusic.cc/t720p/20160621/f20a0db1c-376e-11e6-b9bd-525400475748.mp4");
        urlList.add("http://v7.leappmusic.cc/t720p/20160621/a3072b47a-376e-11e6-b9bd-525400475748.mp4");
        urlList.add("http://v7.leappmusic.cc/t720p/20160621/j1e427ee8-376e-11e6-b9bd-525400475748.mp4");
        urlList.add("http://v7.leappmusic.cc/t720p/20160623/efbac87be-38dc-11e6-99c1-525400475748.mp4");
        urlList.add("http://v7.leappmusic.cc/t720p/20160623/hee654f64-38dc-11e6-99c1-525400475748.mp4");
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        SimpleDraweeView cover;

        @BindView(R.id.play)
        TextView play;

        @BindView(R.id.stop)
        TextView stop;

        @BindView(R.id.tv)
        MyTextureView tv;

        @BindView(R.id.go_detail)
        TextView goDetail;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
