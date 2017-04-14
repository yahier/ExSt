package yahier.exst.widget.titlebar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stbl.stbl.R;

public class NavigationView extends RelativeLayout {
	
	private TextView tvLeft,tvRight;
	private TextView tvTitle,tvMemberNum;
	private ImageView ivRight;

	public NavigationView(Context context) {
		super(context);
		init();
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init(){
		LayoutInflater.from(getContext()).inflate(R.layout.common_top, this);
		
		tvLeft = (TextView) findViewById(R.id.theme_top_banner_left);
		tvTitle = (TextView) findViewById(R.id.theme_top_banner_middle);
		ivRight = (ImageView) findViewById(R.id.theme_top_banner_right);
		tvRight = (TextView)findViewById(R.id.theme_top_tv_right);
		tvMemberNum = (TextView) findViewById(R.id.tv_member_num);
		
		ivRight.setVisibility(View.GONE);
	}
	
	public void setTitleBar(String title){
		tvTitle.setText(title);
	}
	public void setTitleBar(String title,boolean isCenterHorizontal){
		if (isCenterHorizontal){
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
			tvTitle.setLayoutParams(params);
		}
		tvTitle.setText(title);
	}
	
	public void setTitleBar(int resourcesId){
		tvTitle.setText(resourcesId);
	}
	
	public void setClickLeftListener(OnClickListener listener){
		tvLeft.setOnClickListener(listener);
	}
	
	public void setClickRight(int imgResId,OnClickListener listener){
		ivRight.setVisibility(View.VISIBLE);
		ivRight.setImageResource(imgResId);
		ivRight.setOnClickListener(listener);
	}

	public void setTextClickRight(String value, OnClickListener listener){
		tvRight.setVisibility(View.VISIBLE);
		tvRight.setText(value);
		tvRight.setOnClickListener(listener);
	}

	public TextView getTextClickRight(){
		return tvRight;
	}

	public void setTvMemberNum(String memberNum){
		tvMemberNum.setText(memberNum);
		tvMemberNum.setVisibility(View.VISIBLE);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ABOVE,R.id.tv_member_num);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		params.topMargin = 5;
		tvTitle.setLayoutParams(params);
	}
	
}
