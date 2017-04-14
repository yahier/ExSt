package yahier.exst.act.ad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.StatusesFragmentShoppingCircle;
import com.stbl.stbl.widget.TitleBar;

/**
 * Created by Administrator on 2016/9/23.
 * 商圈管理
 */

public class ShoppingCircleManageAct extends FragmentActivity {
    FragmentManager manager;
    TitleBar bar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_circle_manage_act);

        bar = (TitleBar) findViewById(R.id.bar);
        bar.setTitle("商圈管理");
        bar.setActionText("设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ShoppingCircleManageAct.this, SetBrandAct.class);
                startActivity(intent);

            }
        });

        bar.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        StatusesFragmentShoppingCircle fragment = new StatusesFragmentShoppingCircle();
        Bundle bundle = new Bundle();
        bundle.putInt("type",StatusesFragmentShoppingCircle.typeManage);
        fragment.setArguments(bundle);
        manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.add(R.id.frame, fragment);
        trans.commit();

    }
}
