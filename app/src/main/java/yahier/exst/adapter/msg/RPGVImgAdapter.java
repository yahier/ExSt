package yahier.exst.adapter.msg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.stbl.stbl.R;
import com.stbl.stbl.act.dongtai.AdDongtaiDetailAct;
import com.stbl.stbl.act.dongtai.DongtaiDetailActivity;
import com.stbl.stbl.common.CommonAdapter;
import com.stbl.stbl.common.ImagePagerAct;
import com.stbl.stbl.item.Statuses;

import com.stbl.stbl.item.ad.RPImgInfo;
import com.stbl.stbl.util.Config;
import com.stbl.stbl.util.Device;
import com.stbl.stbl.util.PicassoUtil;

/**
 * @author lenovo 动态九宫格图.动态短微博主页 微博动态详细.社区 都用到了这里.
 *
 */

public class RPGVImgAdapter extends CommonAdapter {
    public final static int gridColumn = 3;
    Context mContext;
    FrameLayout.LayoutParams params;
    RPImgInfo pics;
    int numColumn;
    final static int extraWidthDP = 0;//100 在gridview的paddingRight设置了80dp
    public final static String longCover = "99.jpg";
    boolean isShort4 = false;

    boolean isShowPre = false;
    boolean isShowImg = true;
    boolean isImgExpand = true;

    public final static int typeLong = 1;
    public final static int typeShort = 2;
    public final static int typeShort4 = 3;
    public final static int typeShort1 = 4;//短动态一张图
    int type;
    long statusesId;
    String squareId;
    int itemWidth;
    private boolean isShoppingCircle = false; //购物圈点击图片跳详情

    /**
     * 购物圈 列表上显示的
     *
     * @param mContext
     * @param pics
     * @param numColumn
     */
    public RPGVImgAdapter(Activity mContext, RPImgInfo pics, int numColumn, boolean isShoppingCircle) {
       this(mContext,pics,numColumn);
       this.isShoppingCircle = isShoppingCircle;
    }

    /**
     * 列表上显示的
     *
     * @param mContext
     * @param pics
     * @param numColumn
     */
    public RPGVImgAdapter(Activity mContext, RPImgInfo pics, int numColumn) {
        this.mContext = mContext;
        this.pics = pics;
        this.numColumn = numColumn;
        int gridWidth = Device.getWidth(mContext) - (int) mContext.getResources().getDimension(R.dimen.edge_gap) * 2;// - (int) mContext.getResources().getDimension(R.dimen.list_head_img_width_height) - (int) Device.getDensity(mContext) * extraWidthDP;
        itemWidth = gridWidth / numColumn;
        if (numColumn == 1) {
            params = new FrameLayout.LayoutParams(itemWidth, (itemWidth) * 2 / 3);
        } else {
            params = new FrameLayout.LayoutParams(itemWidth, itemWidth);
        }

    }

    public void setImgExpand(boolean isImgExpand) {
        this.isImgExpand = isImgExpand;
    }

    /**
     * 经过测试，滚动时加载图片并不会导致列表卡顿
     * 如果之前是false.选择设置true则刷新
     *
     * @param isShowImg
     */
    public void setShowImg(boolean isShowImg) {
//        isShowPre = this.isShowImg;
//        this.isShowImg = isShowImg;
//        if (isShowPre == false && this.isShowImg == true){
//            notifyDataSetChanged();
//        }
    }


    public void setStatusesId(long statusesId) {
        this.statusesId = statusesId;
    }

    public void setStatusesId(String squareId) {
        this.squareId = squareId;
    }


    public void setType(int type) {
        this.type = type;
        switch(type) {
            case typeShort4:
                //isShort4 = true;
                break;
            case typeShort1:
                this.numColumn = 1;
                int gridWidth = Device.getWidth() - (int) mContext.getResources().getDimension(R.dimen.edge_gap) * 2;
                params = new FrameLayout.LayoutParams(gridWidth, gridWidth);
            default:
                //isShort4 = false;
                break;
        }

    }

    //重建方法
    public void setType(int type, int numColumn) {
        this.type = type;
        this.numColumn = numColumn;
        int gridWidth = Device.getWidth() - (int) mContext.getResources().getDimension(R.dimen.edge_gap) * 2;
        itemWidth = gridWidth / numColumn;
        if (numColumn == 1) {
            params = new FrameLayout.LayoutParams(itemWidth, (itemWidth) * 2 / 3);
        } else {
            params = new FrameLayout.LayoutParams(itemWidth, itemWidth);
        }

        switch(numColumn) {
            case 1:
                //isShort4 = true;
                break;
            case 2:
                params = new FrameLayout.LayoutParams(itemWidth, itemWidth);
                // LogUtil.logE("1个图");
            default:
                //isShort4 = false;
                break;
        }

    }

    public static int gridWidth(Activity mContext) {
        return Device.getWidth(mContext);// - (int) mContext.getResources().getDimension(R.dimen.list_head_img_width_height) - (int) Device.getDensity(mContext) * extraWidthDP;
    }


    @Override
    public int getCount() {
        if (pics == null)
            return 0;
        else if (numColumn == 1) {
            return 1;
        }
//        else if (isShort4 && pics.getPics().size() == 4) {//是4则返回5
//            return pics.getPics().size() + 1;
//        }
        else
            return pics.getMiddlepic().size();
    }

    class CityHolder {
        ImageView item_iv;
        //ImageView item_expand_iv;
    }

    @Override
    public View getView(final int i, View con, ViewGroup parent) {
        CityHolder ho = null;
        if (con == null) {
            ho = new CityHolder();
            con = LayoutInflater.from(mContext).inflate(R.layout.gridview_img, null);
            ho.item_iv = (ImageView) con.findViewById(R.id.img);
            con.setTag(ho);
        } else
            ho = (CityHolder) con.getTag();
        //如果是短动态。4张图

//        if (isShort4) {
//            if (i < 2) {
//                ho.item_iv.setVisibility(View.VISIBLE);
//                String picUrl = pics.getThumbpic() + pics.getPics().get(i);
//                PicassoUtil.loadCropCenter(mContext, picUrl, params.width, params.height, ho.item_iv, R.drawable.dongtai_default);
//            } else if (i == 2) {
//                ho.item_iv.setVisibility(View.INVISIBLE);
//            } else if (i > 2) {
//                ho.item_iv.setVisibility(View.VISIBLE);
//                int j = i - 1;
//                String picUrl = pics.getThumbpic() + pics.getPics().get(j);
//                PicassoUtil.loadCropCenter(mContext, picUrl, params.width, params.height, ho.item_iv, R.drawable.dongtai_default);
//            }
//        } else {
        //       String picUrl = pics.getOriginalpic() + pics.getPics().get(i);  //越界
//        String coverPic = pics.getDefaultpic();
//        //如果是长微博的图。就重新取
//        if (coverPic != null && coverPic.equals(longCover)) {
//            picUrl = pics.getMiddlepic() + coverPic;
//        }
        //PicassoUtil.loadStatusesCropCenter(mContext, picUrl, params.width, params.height, ho.item_iv);


        String middle1 = pics.getMiddlepic().get(i);
        //新的解析
        if (middle1.contains(Config.longWeiboFillMark)) {
            //String middleUrl = middle1.replace(Config.longWeiboFillMark, pics.getPics().get(i));
            //String coverPic = pics.getDefaultpic();
            //if (coverPic != null && coverPic.equals(longCover)) {
            //    middleUrl = middle1.replace(Config.longWeiboFillMark, pics.getDefaultpic());
            //}
            PicassoUtil.loadStatusesCropCenter(mContext, middle1, params.width, params.height, ho.item_iv);
        } else {
            //String picUrl = pics.getMiddlepic() + pics.getPics().get(i);
            //String coverPic = pics.getDefaultpic();
            //如果是长微博的图。就重新取封面
            //if (coverPic != null && coverPic.equals(longCover)) {
            //    picUrl = pics.getMiddlepic() + coverPic;
            //}
            PicassoUtil.loadStatusesCropCenter(mContext, middle1, params.width, params.height, ho.item_iv);
        }


        // }
        //如果是九宫格显示，就显示小图，放大显示就是中
        ho.item_iv.setLayoutParams(params);
        if (!isShoppingCircle)
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;
                switch(type) {
                    case typeLong:
                        intent = new Intent(mContext, DongtaiDetailActivity.class);
                        intent.putExtra("statusesId", statusesId);
                        intent.putExtra(DongtaiDetailActivity.keyType, Statuses.type_long);
                        mContext.startActivity(intent);
                        break;
                    case typeShort:
                        if (isImgExpand) {
                            intent = new Intent(mContext, ImagePagerAct.class);
                            intent.putExtra("index", i);
                            //intent.putExtra("pics", pics);
                        } else {
                            intent = new Intent(mContext, AdDongtaiDetailAct.class);
                            intent.putExtra("statusesId", squareId);
                            mContext.startActivity(intent);
                        }
                        mContext.startActivity(intent);
                        break;
                    case typeShort1:
                        if (isImgExpand) {
                            intent = new Intent(mContext, ImagePagerAct.class);
                            intent.putExtra("index", i);
                            //intent.putExtra("pics", pics);
                        } else {
                            intent = new Intent(mContext, AdDongtaiDetailAct.class);
                            intent.putExtra("statusesId", squareId);
                            mContext.startActivity(intent);
                        }
                        mContext.startActivity(intent);
                        break;
                }
            }
        });
        return con;
    }

    void showExpandImg() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int inSampleSize = calculateInSampleSize(options, width, 300);
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test, options);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
}
