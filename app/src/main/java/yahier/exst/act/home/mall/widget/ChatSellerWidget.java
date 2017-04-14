package yahier.exst.act.home.mall.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;
import com.stbl.stbl.model.GoodsDetail;
import com.stbl.stbl.util.PicassoUtil;

/**
 * Created by meteorshower on 16/4/7.
 */
public class ChatSellerWidget extends RelativeLayout implements View.OnClickListener{

    private ImageView ivAvatar;
    private TextView tvTitle,tvPrice,tvCount;
    private Button btnSend;
    private OnChatSellerSendListener listener;

    public ChatSellerWidget(Context context) {
        super(context);
        init();
    }

    public ChatSellerWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatSellerWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.widget_mall_chat_seller, this);

        ivAvatar = (ImageView)findViewById(R.id.avatar);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvPrice = (TextView)findViewById(R.id.tv_price);
        tvCount = (TextView)findViewById(R.id.tv_count);
        btnSend = (Button)findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
    }

    /** 设置数据 */
    public void setChatSellerData(GoodsDetail goods){
        setVisibility(View.VISIBLE);
        //ivAvatar.setImageResource(R.drawable.test);
        PicassoUtil.load(getContext(), goods.getFimgurl(), ivAvatar);
        tvTitle.setText(goods.getGoodsname());
        tvPrice.setText("￥" + goods.getMaxprice());
        tvCount.setText(getContext().getResources().getString(R.string.mall_sales) +" "+ goods.getAccount().getSalecount());
    }

    @Override
    public void onClick(View v) {
        if(listener != null)
            listener.chatSellerSend();
        //setVisibility(View.GONE);
    }

    public void setOnChatSellerSendListener(OnChatSellerSendListener listener){
        this.listener = listener;
    }

    public interface OnChatSellerSendListener{

        public void chatSellerSend();
    }
}
