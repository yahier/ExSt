package yahier.exst.widget.risenumber;

/**
 * Created by Administrator on 2016/3/22 0022.
 */
public interface RiseNumberBase {
    public void start();

    public RiseNumberTextView withNumber(float number);

    public RiseNumberTextView withNumber(float number, boolean flag);

    public RiseNumberTextView withNumber(int number);

    public RiseNumberTextView withNumber(int fromNum, int number);

    public RiseNumberTextView setDuration(long duration);

    public void setOnEnd(RiseNumberTextView.EndListener callback);
}
