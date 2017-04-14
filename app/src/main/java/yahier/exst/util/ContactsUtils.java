package yahier.exst.util;

import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;

import com.stbl.stbl.common.MyApplication;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.model.mgs.SortToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ContactsUtils {

    public static ArrayList<SortModel> getContactsList() {
        ArrayList<SortModel> contactsList = null;
        Cursor cursor = null;
        try {
            cursor = MyApplication
                    .getContext()
                    .getContentResolver()
                    .query(Phone.CONTENT_URI,
                            new String[]{Phone.DISPLAY_NAME, Phone.NUMBER,
                                    "sort_key"}, null, null,
                            "sort_key COLLATE LOCALIZED ASC");
            if (cursor == null) {
                return contactsList;
            }
            contactsList = new ArrayList<SortModel>();
            if (cursor.getCount() == 0) {
                return contactsList;
            }
            int PHONES_NUMBER_INDEX = cursor.getColumnIndex(Phone.NUMBER);
            int PHONES_DISPLAY_NAME_INDEX = cursor
                    .getColumnIndex(Phone.DISPLAY_NAME);
            int SORT_KEY_INDEX = cursor.getColumnIndex("sort_key");

            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                phoneNumber = phoneNumber.trim();
                phoneNumber = StringUtil.formatMobile(phoneNumber);
                if (phoneNumber.equals(SharedUser.getPhone())) {
                    continue;
                }
                String contactName = cursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);
                String sortKey = ContactLocaleUtils.getIntance()
                        .getSortKey(contactName,
                                ContactLocaleUtils.FullNameStyle.CHINESE);
                SortModel model = new SortModel();
                model.name = contactName;
                model.number = phoneNumber;
                model.sortKey = sortKey;
                // 优先使用系统sortkey取,取不到再使用工具取
                String sortLetters = ContactsUtils
                        .getSortLetterBySortKey(sortKey);
                if (sortLetters == null) {
                    sortLetters = ContactsUtils.getSortLetter(contactName);
                }
                model.sortLetters = sortLetters;
                model.sortToken = ContactsUtils.parseSortKey(sortKey);
                contactsList.add(model);
            }
            Collections.sort(contactsList, new PinyinComparator());
        } catch (Exception e) {
            e.printStackTrace();
            return contactsList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contactsList;
    }

    /**
     * 名字转拼音,取首字母
     *
     * @param name
     * @return
     */
    public static String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }
        // 汉字转换成拼音
        String pinyin = CharacterParser.getInstance().getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 取sort_key的首字母
     *
     * @param sortKey
     * @return
     */
    public static String getSortLetterBySortKey(String sortKey) {
        if (sortKey == null || "".equals(sortKey.trim())) {
            return null;
        }
        String letter = "#";
        // 汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1)
                .toUpperCase(Locale.CHINESE);
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    public static final String chReg = "[\\u4E00-\\u9FA5]+";// 中文字符串匹配

    // private static final String chReg="[^\\u4E00-\\u9FA5]";//除中文外的字符匹配

    /**
     * 解析sort_key,封装简拼,全拼
     *
     * @param sortKey
     * @return
     */
    public static SortToken parseSortKey(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            // 其中包含的中文字符
            String[] enStrs = sortKey.replace(" ", "").split(chReg);
            for (int i = 0, length = enStrs.length; i < length; i++) {
                if (enStrs[i].length() > 0) {
                    // 拼接简拼
                    token.simpleSpell += enStrs[i].charAt(0);
                    token.wholeSpell += enStrs[i];
                }
            }
        }
        return token;
    }

}
