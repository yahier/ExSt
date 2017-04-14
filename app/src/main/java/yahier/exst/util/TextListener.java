package yahier.exst.util;

import android.text.Editable;
import android.text.TextWatcher;

public  abstract class TextListener implements TextWatcher{


	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public abstract void afterTextChanged(Editable arg0);

}
