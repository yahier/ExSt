package yahier.exst.act.im.rong;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.home.mall.MallGoodsDetailAct;
import com.stbl.stbl.model.Goods;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.JSONHelper;
import com.stbl.stbl.util.PicassoUtil;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * 商品
 *
 * @author lsy
 *
 */
@ProviderTag(messageContent = GoodsMessage.class, showPortrait = true, showProgress = true, centerInHorizontal = false)
// 会话界面自定义UI注解
public class GoodsMessageProvider extends IContainerItemProvider.MessageProvider<GoodsMessage> {
	Context mContext;

	/**
	 * 初始化View
	 */
	@Override
	public View newView(Context context, ViewGroup group) {
		View view = LayoutInflater.from(context).inflate(R.layout.im_message_goods, null);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(Device.getWidth()*2/3, ActionBar.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(params);
		ViewHolder holder = new ViewHolder();
		holder.view = view;
		holder.imgUrl = (ImageView) view.findViewById(R.id.link4imgLink);
		holder.tvName = (TextView) view.findViewById(R.id.link4tvGoodsTitle);
		holder.tvPrice = (TextView) view.findViewById(R.id.link4tvGoodsPrice);
		holder.tvSaleCount = (TextView) view.findViewById(R.id.link4tvGoodsSale);
		view.setTag(holder);
		mContext = view.getContext();
		return view;
	}

	@Override
	public void bindView(View v, int position, GoodsMessage content, UIMessage message) {
		ViewHolder holder = (ViewHolder) v.getTag();

		// 更改气泡样式
		if (message.getMessageDirection() == Message.MessageDirection.SEND) {
			// 消息方向，自己发送的
			holder.view.setBackgroundResource(R.drawable.im_message_white_bg_right);
		} else {
			// 消息方向，别人发送的
			holder.view.setBackgroundResource(R.drawable.im_message_white_bg_left);
		}

		String str=  JSONHelper.getExParams(content.getEx_params());
		Goods goods = JSONHelper.getObject(str, Goods.class);
		PicassoUtil.load(mContext, goods.getImgurl(), holder.imgUrl);
		holder.tvName.setText(goods.getGoodsname());
		holder.tvPrice.setText("￥" + goods.getMinprice());
		holder.tvSaleCount.setText(mContext.getString(R.string.sale_count_) + goods.getSalecount());
	}

	@Override
	public Spannable getContentSummary(GoodsMessage data) {
		return null;
	}

	@Override
	public void onItemClick(View view, int position, GoodsMessage content, UIMessage message) {
		String str=  JSONHelper.getExParams(content.getEx_params());
		Goods goods = JSONHelper.getObject(str, Goods.class);
		Intent intent = new Intent(mContext,MallGoodsDetailAct.class);
		intent.putExtra("goodsid", Long.valueOf(goods.getGoodsid()));
		mContext.startActivity(intent);
	}

	@Override
	public void onItemLongClick(View view, int position, GoodsMessage content, UIMessage message) {

	}

	class ViewHolder {
		ImageView imgUrl;
		TextView tvName,tvPrice,tvSaleCount;
		View view;

	}

}