package yahier.exst.utils;

/**
 * Created by meteorshower on 16/3/1.
 */
public class NumUtils {

    public static int getObjToInt(Object obj){
        try{
            return Integer.parseInt(String.valueOf(obj));
        }catch (NumberFormatException e){
            return -100;
        }
    }

    public static boolean getObjToBoolean(Object obj){
        return Boolean.parseBoolean(String.valueOf(obj));
    }

    public static long getObjToLong(Object value){
        try{
            return getStrToLong(String.valueOf(value));
        }catch(NumberFormatException e){
            return -100;
        }
    }

    public static long getStrToLong(String value){
        try{
            return Long.parseLong(value);
        }catch(NumberFormatException e){
            return -100;
        }
    }
}
