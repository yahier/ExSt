package yahier.exst.api.utils.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.stbl.stbl.common.MyApplication;

/**
 * Created by meteorshower on 16/3/3.
 */
public class BasePreferences {

    private SharedPreferences spf;
    private Context context = MyApplication.getStblContext();
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public BasePreferences(String shareName){
        if(spf == null)
            spf = context.getSharedPreferences(shareName, Context.MODE_PRIVATE);
    }

    /************************** Read ************************/

    protected boolean readValue(String name,boolean value){
        return spf.getBoolean(name, value);
    }

    protected float readValue(String name,float value){
        return spf.getFloat(name, value);
    }

    protected int readValue(String name,int value){
        return spf.getInt(name, value);
    }

    protected long readLongValue(String name,long value){
        return spf.getLong(name, value);
    }

    protected String readValue(String name,String value){
        return spf.getString(name, value);
    }

    /************************* Write ************************/
    protected void writeValue(String name,boolean value){
        editor = spf.edit();
        editor.putBoolean(name, value);
        editor.commit();
        closeEditor();
    }

    protected void writeValue(String name,float value){
        editor = spf.edit();
        editor.putFloat(name, value);
        editor.commit();
        closeEditor();
    }

    protected void writeValue(String name,int value){
        editor = spf.edit();
        editor.putInt(name, value);
        editor.commit();
        closeEditor();
    }

    protected void writeValue(String name,long value){
        editor = spf.edit();
        editor.putLong(name, value);
        editor.commit();
        closeEditor();
    }

    protected void writeValue(String name,String value){
        editor = spf.edit();
        editor.putString(name, value);
        editor.commit();
        closeEditor();
    }

    protected void closeEditor(){
        if(editor != null)
            editor.clear();
    }

}
