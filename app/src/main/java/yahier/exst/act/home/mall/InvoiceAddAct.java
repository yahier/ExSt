package yahier.exst.act.home.mall;

import java.util.ArrayList;
import java.util.List;

import com.stbl.stbl.R;
import com.stbl.stbl.common.ThemeActivity;
import com.stbl.stbl.util.WheelString;
import com.stbl.stbl.util.WheelString.OnTimeWheelListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class InvoiceAddAct extends ThemeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_add_act);
        setLabel(getString(R.string.mall_input_invoice));

        final EditText inputHead = (EditText) findViewById(R.id.inputHead);
        final TextView inputType = (TextView) findViewById(R.id.inputType);
        findViewById(R.id.okBtn).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String title = inputHead.getText().toString();
                String type = inputType.getText().toString();
                if (title.trim().length() == 0) {
                    showToast(getString(R.string.mall_please_input_invoice));
                    return;
                }
                Intent it = new Intent();
                it.putExtra("invoiceTitle", title);
                it.putExtra("invoiceType", type);
                setResult(RESULT_OK, it);
                finish();
            }
        });

    }

}
