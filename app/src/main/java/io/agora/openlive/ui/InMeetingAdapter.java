package io.agora.openlive.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hezy.guide.phone.BaseApplication;
import com.hezy.guide.phone.R;
import com.hezy.guide.phone.business.ViewPagerActivity;
import com.hezy.guide.phone.entities.ChatMesData;
import com.hezy.guide.phone.utils.helper.ImageHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class InMeetingAdapter extends RecyclerView.Adapter<InMeetingAdapter.ViewHolder> {

    private Context context;
    private List<ChatMesData.PageDataEntity> data;
    private onItemClickInt itemClick;

    public InMeetingAdapter(Context context, List<ChatMesData.PageDataEntity> data, onItemClickInt temItemClick){
        this.context = context;
        this.data = data;
        this.itemClick = temItemClick;


    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inmeeting_chat,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if(data.get(position).getMsgType() == 2){
            return;
        }
        if(data.get(position).getMsgType() == 1){
            holder.imgContent.setVisibility(View.GONE);
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.color_7FBAFF));
            holder.tvContent.setText(" ：["+data.get(position).getUserName()+" 撤回了一条消息]");
        }else if(data.get(position).getType()==0){
            holder.imgContent.setVisibility(View.GONE);
            holder.tvContent.setTextColor(context.getResources().getColor(R.color.white));
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(" : "+data.get(position).getContent());
        }else {
            String url = ImageHelper.getUrlJoinAndThumAndCrop(data.get(position).getContent(),
                    (int)context.getResources().getDimension(R.dimen.my_px_331),
                    (int)context.getResources().getDimension(R.dimen.my_px_196));
//            }

            holder.imgContent.setVisibility(View.VISIBLE);
            holder.imgContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = 0;
                    ArrayList<String> mList = new ArrayList<>();
                    for(int i=0; i<data.size(); i++){
                        if(data.get(i).getType()==1){
                            mList.add(data.get(i).getContent());
                            if(data.get(i).getContent().equals(data.get(position).getContent())){
                                pos = mList.size()-1;
                            }
                        }
                    }
                    context.startActivity(new Intent(context,ViewPagerActivity.class).putExtra("imglist",mList)
                            .putExtra("pos",pos).putExtra("orientation","land"));
                }
            });
            holder.tvContent.setVisibility(View.GONE);
            Picasso.with(BaseApplication.getInstance()).load(url).into(holder.imgContent);
        }

        holder.tvName.setText(data.get(position).getUserName()+"");
        holder.tvAddress.setText(data.get(position).getUserName());
        if(data.get(position).getLocalState()==0){
            holder.sendBar.setVisibility(View.GONE);
            holder.tvState.setVisibility(View.GONE);
        }else if(data.get(position).getLocalState() == 1){
            holder.sendBar.setVisibility(View.VISIBLE);
            holder.tvState.setVisibility(View.GONE);
        }else if(data.get(position).getLocalState() == 2){
            holder.sendBar.setVisibility(View.GONE);
            holder.tvState.setVisibility(View.VISIBLE);
            holder.tvState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    callBack.onReSend(data.get(position).getContent(),data.get(position).getType());
                }
            });
//            holder.tvState.setText("失败");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("这里是点击每一行item的响应事件",""+position+item);
                itemClick.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName, tvAddress, tvContent, tvState;
        private ProgressBar sendBar;
        private ImageView imgContent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_addres);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvState = itemView.findViewById(R.id.send_sate);
            sendBar = itemView.findViewById(R.id.send_bar);
            imgContent = itemView.findViewById(R.id.img_content);

        }
    }

    public interface  onItemClickInt{
        void onItemClick(int pos);
    }
}


