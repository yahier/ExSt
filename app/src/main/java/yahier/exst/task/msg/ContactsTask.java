package yahier.exst.task.msg;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stbl.base.library.task.HttpResponse;
import com.stbl.base.library.task.Task;
import com.stbl.stbl.item.UserItem;
import com.stbl.stbl.model.mgs.SortModel;
import com.stbl.stbl.util.ContactLocaleUtils;
import com.stbl.stbl.util.ContactLocaleUtils.FullNameStyle;
import com.stbl.stbl.util.ContactsUtils;
import com.stbl.stbl.util.LogUtil;
import com.stbl.stbl.util.Method;
import com.stbl.stbl.util.OkHttpHelper;
import com.stbl.stbl.util.PinyinComparator;
import com.stbl.stbl.util.SimpleTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ContactsTask {

    public static SimpleTask<ArrayList<SortModel>> getFriendList(
            final int grouptype, final int relationflag, final int hasself) {
        return new SimpleTask<ArrayList<SortModel>>() {

            @Override
            protected void call() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("grouptype", grouptype);
                    if (relationflag != 0) {
                        json.put("relationflag", relationflag);
                    }
                    json.put("hasself", hasself);
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getAppContacts, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }

                    LogUtil.logE("ContactsTask result", response.result);
                    List<UserItem> userList = JSONArray.parseArray(response.result, UserItem.class);
                    LogUtil.logE("ContactsTask size", userList.size());
                    ArrayList<SortModel> modelList = new ArrayList<SortModel>();
                    for (UserItem item : userList) {
                        String name = item.getAlias();
                        if (TextUtils.isEmpty(name)) {
                            name = item.getNickname();
                        }
                        String sortKey = ContactLocaleUtils.getIntance().getSortKey(name, FullNameStyle.CHINESE);
                        SortModel model = new SortModel();
                        model.name = name;
                        model.sortKey = sortKey;
                        model.user = item;
                        // 优先使用系统sortkey取,取不到再使用工具取
                        String sortLetters = ContactsUtils
                                .getSortLetterBySortKey(sortKey);
                        if (sortLetters == null) {
                            sortLetters = ContactsUtils.getSortLetter(name);
                        }
                        model.sortLetters = sortLetters;
                        model.sortToken = ContactsUtils.parseSortKey(sortKey);

                        //默认保存昵称的索引信息
                        model.nick = item.getNickname();
                        String nickSortKey = ContactLocaleUtils.getIntance().getSortKey(model.nick, FullNameStyle.CHINESE);
                        model.nickSortKey = nickSortKey;
                        // 优先使用系统sortkey取,取不到再使用工具取
                        String nickSortLetters = ContactsUtils
                                .getSortLetterBySortKey(nickSortKey);
                        if (nickSortLetters == null) {
                            nickSortLetters = ContactsUtils.getSortLetter(model.nick);
                        }
                        model.nickSortLetters = nickSortLetters;
                        model.nickSortToken = ContactsUtils.parseSortKey(nickSortKey);

                        modelList.add(model);
                    }
                    Collections.sort(modelList, new PinyinComparator());
                    onCompleted(modelList);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    /**
     * @return
     */
    public static SimpleTask<ArrayList<SortModel>> getPhoneContactsList() {
        return new SimpleTask<ArrayList<SortModel>>() {

            @Override
            public void call() {
                ArrayList<SortModel> contactsList = ContactsUtils
                        .getContactsList();
                if (contactsList == null) {
                    onError("无权限获取通讯录");
                    return;
                }
                if (contactsList.size() == 0) {
                    onCompleted(contactsList);
                    return;
                }
                ArrayList<JSONObject> phoneList = new ArrayList<>();
                for (SortModel model : contactsList) {
                    JSONObject obj = new JSONObject();
                    obj.put("contactname", model.name);
                    obj.put("telphone", model.number);
                    phoneList.add(obj);
                }
                try {
                    JSONObject json = new JSONObject();
                    json.put("phonenums", phoneList);
                    HttpResponse syncRes = OkHttpHelper.getInstance().post(Method.uploadPhoneNumbers, json);
                    Log.i("contacts", syncRes.result);

                    JSONObject json2 = new JSONObject();
                    json2.put("grouptype", 2); //不分组
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getPhoneNumbers, json2);

                    if (response.error != null) {
                        onCompleted(contactsList);
                        return;
                    }
                    List<UserItem> userList = JSONArray.parseArray(
                            response.result, UserItem.class);
                    for (SortModel model : contactsList) {
                        for (UserItem item : userList) {
                            if (item.getTelphone().equals(model.number)) {
                                model.user = item;
                                break;
                            }
                        }
                    }
                    onCompleted(contactsList);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static SimpleTask<ArrayList<SortModel>> search(
            final ArrayList<SortModel> contactsList, final String str) {
        return new SimpleTask<ArrayList<SortModel>>() {

            @Override
            protected void call() {
                ArrayList<SortModel> filterList = new ArrayList<SortModel>();// 过滤后的list
                // if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
                if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式
                    // 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
                    for (SortModel model : contactsList) {
                        boolean isFilter = false;
                        if (model.name != null) {
                            if (model.name.contains(str)) {
                                //if (!filterList.contains(model)) {
                                isFilter = true;
                                //}
                            }
                        }
                        if (!isFilter && !model.name.equals(model.nick) && model.nick != null) {
                            if (model.nick.contains(str)) {
                                //if (!filterList.contains(model)) {
                                isFilter = true;
                                //}
                            }
                        }
                        if (isFilter) {
                            filterList.add(model);
                        }
                    }
                } else {
                    for (SortModel model : contactsList) {
                        boolean isFilter = false;
                        if (model.name != null) {
                            // 姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                            if (model.name.toLowerCase(Locale.CHINESE)
                                    .contains(str.toLowerCase(Locale.CHINESE))
                                    || model.sortKey
                                    .toLowerCase(Locale.CHINESE)
                                    .replace(" ", "")
                                    .contains(
                                            str.toLowerCase(Locale.CHINESE))
                                    || model.sortToken.simpleSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))
                                    || model.sortToken.wholeSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter && !model.name.equals(model.nick) && model.nick != null) {
                            if (model.nick.toLowerCase(Locale.CHINESE)
                                    .contains(str.toLowerCase(Locale.CHINESE))
                                    || model.nickSortKey
                                    .toLowerCase(Locale.CHINESE)
                                    .replace(" ", "")
                                    .contains(
                                            str.toLowerCase(Locale.CHINESE))
                                    || model.nickSortToken.simpleSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))
                                    || model.nickSortToken.wholeSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))) {
                                isFilter = true;
                            }
                        }
                        if (isFilter) {
                            //if (!filterList.contains(model)) {  //因为SortModel重写了equals方法，同名的相等
                            filterList.add(model);
                            //}
                        }
                    }
                }
                onCompleted(filterList);
            }
        };
    }

    public static SimpleTask<HashMap<String, Object>> getFriendCategory() {
        return new SimpleTask<HashMap<String, Object>>() {

            @Override
            protected void call() {
                try {
                    JSONObject json = new JSONObject();
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getFriendTypeSimple, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    JSONObject data = JSONObject.parseObject(response.result);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    if (data.containsKey("masterview")) {
                        UserItem master = JSONObject.parseObject(
                                data.getString("masterview"), UserItem.class);
                        map.put("masterview", master);
                    }
                    map.put("tudicount", data.getIntValue("tudicount"));
                    map.put("friendcount", data.getIntValue("friendcount"));
                    onCompleted(map);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


    /**
     * 获取邀请短信
     *
     * @return
     */
    public static SimpleTask<HashMap<String, Object>> getInviteMsg() {
        return new SimpleTask<HashMap<String, Object>>() {

            @Override
            protected void call() {
                try {
                    JSONObject json = new JSONObject();
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getInviteApplyMsg, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }
                    JSONObject data = JSONObject.parseObject(response.result);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("smstext", data.getString("smstext"));
                    onCompleted(map);

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }

    public static Task<ArrayList<SortModel>> getContactsList(
            final int grouptype, final int relationflag, final int hasself) {
        return new Task<ArrayList<SortModel>>() {

            @Override
            protected void call() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("grouptype", grouptype);
                    if (relationflag != 0) {
                        json.put("relationflag", relationflag);
                    }
                    json.put("hasself", hasself);
                    HttpResponse response = OkHttpHelper.getInstance().post(Method.getAppContacts, json);
                    if (response.error != null) {
                        onError(response.error);
                        return;
                    }

                    LogUtil.logE("ContactsTask result", response.result);
                    List<UserItem> userList = JSONArray.parseArray(response.result, UserItem.class);
                    LogUtil.logE("ContactsTask size", userList.size());
                    ArrayList<SortModel> modelList = new ArrayList<SortModel>();
                    for (UserItem item : userList) {
                        String name = item.getAlias();
                        if (TextUtils.isEmpty(name)) {
                            name = item.getNickname();
                        }
                        String sortKey = ContactLocaleUtils.getIntance().getSortKey(name, FullNameStyle.CHINESE);
                        SortModel model = new SortModel();
                        model.name = name;
                        model.sortKey = sortKey;
                        model.user = item;
                        // 优先使用系统sortkey取,取不到再使用工具取
                        String sortLetters = ContactsUtils
                                .getSortLetterBySortKey(sortKey);
                        if (sortLetters == null) {
                            sortLetters = ContactsUtils.getSortLetter(name);
                        }
                        model.sortLetters = sortLetters;
                        model.sortToken = ContactsUtils.parseSortKey(sortKey);

                        //默认保存昵称的索引信息
                        model.nick = item.getNickname();
                        String nickSortKey = ContactLocaleUtils.getIntance().getSortKey(model.nick, FullNameStyle.CHINESE);
                        model.nickSortKey = nickSortKey;
                        // 优先使用系统sortkey取,取不到再使用工具取
                        String nickSortLetters = ContactsUtils
                                .getSortLetterBySortKey(nickSortKey);
                        if (nickSortLetters == null) {
                            nickSortLetters = ContactsUtils.getSortLetter(model.nick);
                        }
                        model.nickSortLetters = nickSortLetters;
                        model.nickSortToken = ContactsUtils.parseSortKey(nickSortKey);

                        modelList.add(model);
                    }
                    Collections.sort(modelList, new PinyinComparator());
                    onSuccess(modelList);
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }
        };
    }


    public static Task<ArrayList<SortModel>> searchContacts(
            final ArrayList<SortModel> contactsList, final String str) {
        return new Task<ArrayList<SortModel>>() {

            @Override
            protected void call() {
                ArrayList<SortModel> filterList = new ArrayList<SortModel>();// 过滤后的list
                // if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
                if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式
                    // 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
                    for (SortModel model : contactsList) {
                        boolean isFilter = false;
                        if (model.name != null) {
                            if (model.name.contains(str)) {
                                //if (!filterList.contains(model)) {
                                isFilter = true;
                                //}
                            }
                        }
                        if (!isFilter && !model.name.equals(model.nick) && model.nick != null) {
                            if (model.nick.contains(str)) {
                                //if (!filterList.contains(model)) {
                                isFilter = true;
                                //}
                            }
                        }
                        if (isFilter) {
                            filterList.add(model);
                        }
                    }
                } else {
                    for (SortModel model : contactsList) {
                        boolean isFilter = false;
                        if (model.name != null) {
                            // 姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                            if (model.name.toLowerCase(Locale.CHINESE)
                                    .contains(str.toLowerCase(Locale.CHINESE))
                                    || model.sortKey
                                    .toLowerCase(Locale.CHINESE)
                                    .replace(" ", "")
                                    .contains(
                                            str.toLowerCase(Locale.CHINESE))
                                    || model.sortToken.simpleSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))
                                    || model.sortToken.wholeSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter && !model.name.equals(model.nick) && model.nick != null) {
                            if (model.nick.toLowerCase(Locale.CHINESE)
                                    .contains(str.toLowerCase(Locale.CHINESE))
                                    || model.nickSortKey
                                    .toLowerCase(Locale.CHINESE)
                                    .replace(" ", "")
                                    .contains(
                                            str.toLowerCase(Locale.CHINESE))
                                    || model.nickSortToken.simpleSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))
                                    || model.nickSortToken.wholeSpell.toLowerCase(
                                    Locale.CHINESE).contains(
                                    str.toLowerCase(Locale.CHINESE))) {
                                isFilter = true;
                            }
                        }
                        if (isFilter) {
                            //if (!filterList.contains(model)) {  //因为SortModel重写了equals方法，同名的相等
                            filterList.add(model);
                            //}
                        }
                    }
                }
                onSuccess(filterList);
            }
        };
    }

}
