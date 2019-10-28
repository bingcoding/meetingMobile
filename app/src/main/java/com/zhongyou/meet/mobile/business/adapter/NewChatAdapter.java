package com.zhongyou.meet.mobile.business.adapter;

import android.content.Intent;
import android.icu.text.UFormat;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.zhongyou.meet.mobile.R;
import com.zhongyou.meet.mobile.business.ViewPagerActivity;
import com.zhongyou.meet.mobile.entities.ChatMesData;
import com.zhongyou.meet.mobile.entities.ChatMsgData;
import com.zhongyou.meet.mobile.persistence.Preferences;
import com.zhongyou.meet.mobile.view.CropSquareTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author golangdorid@gmail.com
 * @date 2019-10-28 17:42.
 */
public class NewChatAdapter extends BaseMultiItemQuickAdapter<ChatMesData.PageDataEntity, BaseViewHolder> {

	public NewChatAdapter(List<ChatMesData.PageDataEntity> data) {
		super(data);
		addItemType(2, R.layout.item_center_big);
		addItemType(1, R.layout.item_right);
		addItemType(0, R.layout.item_left);

	}

	private android.os.Handler handler = new android.os.Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			((View)msg.obj).setVisibility(View.GONE);

		}
	};


	@Override
	protected void convert(BaseViewHolder helper, ChatMesData.PageDataEntity item) {
		if (helper.getItemViewType()==2){
			if(item.getMsgType()==1){
				if(item.getUserId().equals(Preferences.getUserId())){
					helper.setText(R.id.tv_center,"你撤回了一条消息");

					if(System.currentTimeMillis()-item.getReplyTimestamp()<20000){
						helper.getView(R.id.tv_edit).setVisibility(View.VISIBLE);
						Message msg = new Message();
						msg.obj = (TextView)helper.getView(R.id.tv_edit);
						handler.sendMessageDelayed(msg,20000);
						((TextView)helper.getView(R.id.tv_edit)).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								// TODO: 2019-10-28
								//callBack.onEditCallBack(data.get(position).getContent());
							}
						});
					}else {
						((TextView)helper.getView(R.id.tv_edit)).setVisibility(View.GONE);
					}

				}else {
					((TextView)helper.getView(R.id.tv_center)).setText(item.getUserName()+"  撤回了一条消息");
					((TextView)helper.getView(R.id.tv_edit)).setVisibility(View.GONE);
				}
			}else {
				((TextView)helper.getView(R.id.tv_center)).setText(item.getReplyTime());
				((TextView)helper.getView(R.id.tv_edit)).setVisibility(View.GONE);
			}
			return;
		}
		helper.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: 2019-10-28
				//callBack.onClickCallBackFuc();
			}
		});

		if (null==item.getUserName()){
			((TextView)helper.getView(R.id.tv_name)).setText("");
		}else {
			/*Logger.e("helper.getView(R.id.tv_name):   "+helper.getView(R.id.tv_name)==null?"null":"not null");
			Logger.e("item.getUserName():   "+item.getUserName()==null?"null":"not null");*/
		}
//			((TextView)helper.getView(R.id.tv_name)).setText(item.getUserName());
//		}

		if (null==item.getUserLogo()){
			Picasso.with(mContext).load(R.drawable.ico_face).into((ImageView) helper.getView(R.id.mIvHead));
		}else {
			Glide.with(mContext)
					.load(item.getUserLogo())
					.error(R.drawable.ico_face)
					.placeholder(R.drawable.ico_face)
					.skipMemoryCache(true)
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.into((ImageView) helper.getView(R.id.mIvHead));
		}

		if (helper.getItemViewType()==0){
			helper.getView(R.id.tv_content).setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					// TODO: 2019-10-28
					//callBack.onLongContent(view,null,data.get(position).getContent());
					return false;
				}
			});
			helper.getView(R.id.mIvHead).setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					// TODO: 2019-10-28
					//callBack.onLongImgHead(data.get(position).getUserName(),data.get(position).getUserId());
					return false;
				}
			});
		}else {
			helper.getView(R.id.tv_content).setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					// TODO: 2019-10-28
					//callBack.onLongContent(view,data.get(position).getId(),data.get(position).getContent());
					return false;
				}
			});
			helper.getView(R.id.mIvHead).setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					return false;
				}
			});
			if(item.getLocalState()==0){
				helper.getView(R.id.send_bar).setVisibility(View.GONE);
				helper.getView(R.id.send_sate).setVisibility(View.GONE);
				helper.getView(R.id.send_err).setVisibility(View.GONE);
			}else if(item.getLocalState() == 1){
				helper.getView(R.id.send_bar).setVisibility(View.VISIBLE);
				helper.getView(R.id.send_sate).setVisibility(View.GONE);
				helper.getView(R.id.send_err).setVisibility(View.GONE);
			}else if(item.getLocalState() == 2){
				helper.getView(R.id.send_bar).setVisibility(View.GONE);
				helper.getView(R.id.send_sate).setVisibility(View.VISIBLE);
				helper.getView(R.id.send_err).setVisibility(View.VISIBLE);
				helper.getView(R.id.send_sate).setOnClickListener(view -> {
					// TODO: 2019-10-28
					//return callBack.onReSend(data.get(position).getContent(), data.get(position).getType());
				});
			}
		}

		if (helper.getItemViewType()==1){
			Picasso.with(mContext).load(item.getContent())
					.memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_STORE)
					.error(R.drawable.load_error)
					.placeholder(R.drawable.loading)
					.transform(new CropSquareTransformation()).into(helper.getView(R.id.img_pic), new Callback() {
				@Override
				public void onSuccess() {

					helper.getView(R.id.img_pic).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							int pos = 0;
							ArrayList<String> mList = new ArrayList<>();
							for(int i=0; i<getItemCount(); i++){
								if(item.getType()==1){
									mList.add(item.getContent());
									if(item.getContent().equals(item.getContent())){
										pos = mList.size()-1;
									}
								}
							}
							mContext.startActivity(new Intent(mContext, ViewPagerActivity.class).putExtra("imglist",mList)
									.putExtra("pos",pos));
						}
					});

					helper.getView(R.id.img_pic).setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View view) {
							// TODO: 2019-10-28
							//callBack.onLongContent(view,data.get(position).getId(),null);
							return true;
						}
					});
				}

				@Override
				public void onError() {

				}
			});
			((TextView)helper.getView(R.id.tv_content)).setVisibility(View.GONE);
			((ImageView)helper.getView(R.id.img_arrow)).setVisibility(View.GONE);
			((ImageView)helper.getView(R.id.img_pic)).setVisibility(View.VISIBLE);
		}else {
			((TextView)helper.getView(R.id.tv_content)).setText(item.getContent());
			((TextView)helper.getView(R.id.tv_content)).setVisibility(View.VISIBLE);
			((ImageView)helper.getView(R.id.img_arrow)).setVisibility(View.VISIBLE);
			((ImageView)helper.getView(R.id.img_pic)).setVisibility(View.GONE);
		}

	}
}
