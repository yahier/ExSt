package yahier.exst.act.im;//package com.stbl.stbl.act.im;
//
///**
// * Created by Administrator on 2016/12/8.
// */
//
////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.FragmentManager;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.AnimationSet;
//import android.view.animation.TranslateAnimation;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AbsListView;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//import io.rong.common.RLog;
//import io.rong.eventbus.EventBus;
//import io.rong.imkit.IExtensionClickListener;
//import io.rong.imkit.IPublicServiceMenuClickListener;
//import io.rong.imkit.InputMenu;
//import io.rong.imkit.RongContext;
//import io.rong.imkit.RongExtension;
//import io.rong.imkit.RongIM;
//import io.rong.imkit.fragment.UriFragment;
//import io.rong.imkit.manager.AudioPlayManager;
//import io.rong.imkit.manager.AudioRecordManager;
//import io.rong.imkit.manager.InternalModuleManager;
//import io.rong.imkit.manager.SendImageManager;
//import io.rong.imkit.mention.RongMentionManager;
//import io.rong.imkit.model.ConversationInfo;
//import io.rong.imkit.model.Event;
//import io.rong.imkit.model.GroupUserInfo;
//import io.rong.imkit.model.UIMessage;
//import io.rong.imkit.plugin.IPluginModule;
//import io.rong.imkit.plugin.location.AMapRealTimeActivity;
//import io.rong.imkit.plugin.location.IParticipantChangedListener;
//import io.rong.imkit.plugin.location.IUserInfoProvider;
//import io.rong.imkit.plugin.location.LocationManager;
//import io.rong.imkit.userInfoCache.RongUserInfoManager;
//import io.rong.imkit.utilities.PermissionCheckUtil;
//import io.rong.imkit.utilities.PromptPopupDialog;
//import io.rong.imkit.widget.AutoRefreshListView;
//import io.rong.imkit.widget.SingleChoiceDialog;
//import io.rong.imkit.widget.adapter.MessageListAdapter;
//import io.rong.imlib.CustomServiceConfig;
//import io.rong.imlib.ICustomServiceListener;
//import io.rong.imlib.IRongCallback;
//import io.rong.imlib.RongIMClient;
//import io.rong.imlib.location.RealTimeLocationConstant;
//import io.rong.imlib.model.CSCustomServiceInfo;
//import io.rong.imlib.model.CSGroupItem;
//import io.rong.imlib.model.Conversation;
//import io.rong.imlib.model.CustomServiceMode;
//import io.rong.imlib.model.MentionedInfo;
//import io.rong.imlib.model.Message;
//import io.rong.imlib.model.PublicServiceMenu;
//import io.rong.imlib.model.PublicServiceMenuItem;
//import io.rong.imlib.model.PublicServiceProfile;
//import io.rong.imlib.model.ReadReceiptInfo;
//import io.rong.imlib.model.UserInfo;
//import io.rong.message.FileMessage;
//import io.rong.message.ImageMessage;
//import io.rong.message.LocationMessage;
//import io.rong.message.PublicServiceCommandMessage;
//import io.rong.message.ReadReceiptMessage;
//import io.rong.message.RecallNotificationMessage;
//import io.rong.message.TextMessage;
//import io.rong.message.VoiceMessage;
//import io.rong.push.RongPushClient;
//
//
//
//public class ConversationYaFragment extends UriFragment implements AbsListView.OnScrollListener, IExtensionClickListener, IUserInfoProvider {
//    private static final String TAG = "ConversationFragment";
//    private PublicServiceProfile mPublicServiceProfile;
//    private View mRealTimeBar;
//    private TextView mRealTimeText;
//    private RongExtension mRongExtension;
//    private boolean mEnableMention;
//    private float mLastTouchY;
//    private boolean mUpDirection;
//    private float mOffsetLimit;
//    private CSCustomServiceInfo mCustomUserInfo;
//    private ConversationInfo mCurrentConversationInfo;
//    private String mDraft;
//    private static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
//    private static final int REQUEST_CODE_LOCATION_SHARE = 101;
//    public static final int SCROLL_MODE_NORMAL = 1;
//    public static final int SCROLL_MODE_TOP = 2;
//    public static final int SCROLL_MODE_BOTTOM = 3;
//    private static final int DEFAULT_HISTORY_MESSAGE_COUNT = 30;
//    private static final int DEFAULT_REMOTE_MESSAGE_COUNT = 10;
//    private static final int TIP_DEFAULT_MESSAGE_COUNT = 2;
//    private String mTargetId;
//    private Conversation.ConversationType mConversationType;
//    private boolean mReadRec;
//    private boolean mSyncReadStatus;
//    private int mNewMessageCount;
//    private AutoRefreshListView mList;
//    private Button mUnreadBtn;
//    private ImageButton mNewMessageBtn;
//    private TextView mNewMessageTextView;
//    private MessageListAdapter mListAdapter;
//    private View mMsgListView;
//    private boolean mHasMoreLocalMessages;
//    private int mLastMentionMsgId;
//    private long mSyncReadStatusMsgTime;
//    private boolean mCSneedToQuit = true;
//    private ArrayList<String> mLocationShareParticipants;
//    private boolean robotType = true;
//    private int source = 0;
//    private boolean resolved = true;
//    private boolean committing = false;
//    private long enterTime;
//    private boolean evaluate = true;
//    ICustomServiceListener customServiceListener = new ICustomServiceListener() {
//        public void onSuccess(CustomServiceConfig config) {
//            if(config.isBlack) {
//                ConversationYaFragment.this.onCustomServiceWarning(ConversationYaFragment.this.getString(io.rong.imkit.R.string.rc_blacklist_prompt), false);
//            }
//
//            if(config.robotSessionNoEva) {
//                ConversationYaFragment.this.evaluate = false;
//                ConversationYaFragment.this.mListAdapter.setEvaluateForRobot(true);
//            }
//
//        }
//
//        public void onError(int code, String msg) {
//            ConversationYaFragment.this.onCustomServiceWarning(msg, false);
//        }
//
//        public void onModeChanged(CustomServiceMode mode) {
//            if(ConversationYaFragment.this.mRongExtension != null) {
//                ConversationYaFragment.this.mRongExtension.setExtensionBarMode(mode);
//                if(!mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN) && !mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_HUMAN_FIRST)) {
//                    if(mode.equals(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE)) {
//                        ConversationYaFragment.this.evaluate = false;
//                    }
//                } else {
//                    ConversationYaFragment.this.robotType = false;
//                    ConversationYaFragment.this.evaluate = true;
//                }
//
//            }
//        }
//
//        public void onQuit(String msg) {
//            if(!ConversationYaFragment.this.committing) {
//                ConversationYaFragment.this.onCustomServiceWarning(msg, true);
//            }
//
//        }
//
//        public void onPullEvaluation(String dialogId) {
//            if(!ConversationYaFragment.this.committing) {
//                ConversationYaFragment.this.onCustomServiceEvaluation(true, dialogId, ConversationYaFragment.this.robotType, ConversationYaFragment.this.evaluate);
//            }
//
//        }
//
//        public void onSelectGroup(List<CSGroupItem> groups) {
//            ConversationYaFragment.this.onSelectCustomerServiceGroup(groups);
//        }
//    };
//
//    public ConversationYaFragment() {
//    }
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        InternalModuleManager.getInstance().onLoaded();
//
//        try {
//            this.mEnableMention = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_mentioned_message);
//        } catch (Resources.NotFoundException var5) {
//            RLog.e("ConversationFragment", "rc_enable_mentioned_message not found in rc_config.xml");
//        }
//
//        try {
//            this.mReadRec = this.getResources().getBoolean(io.rong.imkit.R.bool.rc_read_receipt);
//            this.mSyncReadStatus = this.getResources().getBoolean(io.rong.imkit.R.bool.rc_enable_sync_read_status);
//        } catch (Resources.NotFoundException var4) {
//            RLog.e("ConversationFragment", "rc_read_receipt not found in rc_config.xml");
//            var4.printStackTrace();
//        }
//
//        try {
//            this.mCSneedToQuit = RongContext.getInstance().getResources().getBoolean(io.rong.imkit.R.bool.rc_stop_custom_service_when_quit);
//        } catch (Resources.NotFoundException var3) {
//            var3.printStackTrace();
//        }
//
//    }
//
//    /***
//     *  将底部输入扩展栏设置为隐藏
//     */
//    public void setmRongExtensionGone(){
//        this.mRongExtension.setVisibility(View.GONE);
//    }
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(io.rong.imkit.R.layout.rc_fr_conversation, container, false);
//        this.mRongExtension = (RongExtension)view.findViewById(io.rong.imkit.R.id.rc_extension);
//        this.mRongExtension.setExtensionClickListener(this);
//        this.mRongExtension.setFragment(this);
//        this.mOffsetLimit = 70.0F * this.getActivity().getResources().getDisplayMetrics().density;
//        this.mMsgListView = this.findViewById(view, io.rong.imkit.R.id.rc_layout_msg_list);
//        this.mList = (AutoRefreshListView)this.findViewById(this.mMsgListView, io.rong.imkit.R.id.rc_list);
//        this.mList.requestDisallowInterceptTouchEvent(true);
//        this.mList.setMode(AutoRefreshListView.Mode.START);
//        this.mList.setTranscriptMode(2);
//        this.mListAdapter = this.onResolveAdapter(this.getActivity());
//        this.mList.setAdapter(this.mListAdapter);
//        this.mList.setOnRefreshListener(new AutoRefreshListView.OnRefreshListener() {
//            public void onRefreshFromStart() {
//                if(ConversationYaFragment.this.mHasMoreLocalMessages) {
//                    ConversationYaFragment.this.getHistoryMessage(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId, 30, 1);
//                } else {
//                    ConversationYaFragment.this.getRemoteHistoryMessages(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId, 10);
//                }
//
//            }
//
//            public void onRefreshFromEnd() {
//            }
//        });
//        this.mList.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == 2 && ConversationYaFragment.this.mList.getCount() - ConversationYaFragment.this.mList.getHeaderViewsCount() == 0) {
//                    if(ConversationYaFragment.this.mHasMoreLocalMessages) {
//                        ConversationYaFragment.this.getHistoryMessage(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId, 30, 1);
//                    } else if(ConversationYaFragment.this.mList.getRefreshState() != AutoRefreshListView.State.REFRESHING) {
//                        ConversationYaFragment.this.getRemoteHistoryMessages(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId, 10);
//                    }
//
//                    return true;
//                } else {
//                    if(event.getAction() == 1 && ConversationYaFragment.this.mRongExtension.isExtensionExpanded()) {
//                        ConversationYaFragment.this.mRongExtension.collapseExtension();
//                    }
//
//                    return false;
//                }
//            }
//        });
//        if(RongContext.getInstance().getNewMessageState()) {
//            this.mNewMessageTextView = (TextView)this.findViewById(view, io.rong.imkit.R.id.rc_new_message_number);
//            this.mNewMessageBtn = (ImageButton)this.findViewById(view, io.rong.imkit.R.id.rc_new_message_count);
//            this.mNewMessageBtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    ConversationYaFragment.this.mList.smoothScrollToPosition(ConversationYaFragment.this.mList.getCount() + 1);
//                    ConversationYaFragment.this.mNewMessageBtn.setVisibility(8);
//                    ConversationYaFragment.this.mNewMessageTextView.setVisibility(8);
//                    ConversationYaFragment.this.mNewMessageCount = 0;
//                }
//            });
//        }
//
//        if(RongContext.getInstance().getUnreadMessageState()) {
//            this.mUnreadBtn = (Button)this.findViewById(this.mMsgListView, io.rong.imkit.R.id.rc_unread_message_count);
//        }
//
//        this.mList.addOnScrollListener(this);
//        this.mListAdapter.setOnItemHandlerListener(new MessageListAdapter.OnItemHandlerListener() {
//            public boolean onWarningViewClick(int position, final Message data, View v) {
//                if(!ConversationYaFragment.this.onResendItemClick(data)) {
//                    RongIM.getInstance().deleteMessages(new int[]{data.getMessageId()}, new RongIMClient.ResultCallback<Boolean>() {
//
//                        public void onSuccess(Boolean aBoolean) {
//                            if(aBoolean.booleanValue()) {
//                                data.setMessageId(0);
//                                if(data.getContent() instanceof ImageMessage) {
//                                    RongIM.getInstance().sendImageMessage(data, "", "", new RongIMClient.SendImageMessageCallback() {
//                                        public void onAttached(Message message) {
//                                        }
//
//                                        public void onError(Message message, RongIMClient.ErrorCode code) {
//                                        }
//
//                                        public void onSuccess(Message message) {
//                                        }
//
//                                        public void onProgress(Message message, int progress) {
//                                        }
//                                    });
//                                } else if(data.getContent() instanceof LocationMessage) {
//                                    RongIM.getInstance().sendLocationMessage(data, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
//                                } else if(data.getContent() instanceof FileMessage) {
//                                    RongIM.getInstance().sendMediaMessage(data, (String)null, (String)null, (IRongCallback.ISendMediaMessageCallback)null);
//                                } else {
//                                    RongIM.getInstance().sendMessage(data, (String)null, (String)null, new IRongCallback.ISendMessageCallback() {
//                                        public void onAttached(Message message) {
//                                        }
//
//                                        public void onSuccess(Message message) {
//                                        }
//
//                                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                                        }
//                                    });
//                                }
//                            }
//
//                        }
//
//
//                        public void onError(RongIMClient.ErrorCode e) {
//                        }
//                    });
//                }
//
//                return true;
//            }
//
//            public void onReadReceiptStateClick(Message message) {
//                ConversationYaFragment.this.onReadReceiptStateClick(message);
//            }
//        });
//        return view;
//    }
//
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if(scrollState == 1) {
//            this.mRongExtension.collapseExtension();
//        } else if(scrollState == 0) {
//            int last = this.mList.getLastVisiblePosition();
//            if(this.mList.getCount() - last > 2) {
//                this.mList.setTranscriptMode(1);
//            } else {
//                this.mList.setTranscriptMode(2);
//            }
//
//            if(this.mNewMessageBtn != null && last == this.mList.getCount() - 1) {
//                this.mNewMessageCount = 0;
//                this.mNewMessageBtn.setVisibility(8);
//                this.mNewMessageTextView.setVisibility(8);
//            }
//        }
//
//    }
//
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//    }
//
//    public void onResume() {
//        RongPushClient.clearAllPushNotifications(this.getActivity());
//        super.onResume();
//    }
//
//    public final void getUserInfo(String userId, UserInfoCallback callback) {
//        UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(userId);
//        if(userInfo != null) {
//            callback.onGotUserInfo(userInfo);
//        }
//
//    }
//
//    public MessageListAdapter onResolveAdapter(Context context) {
//        return new MessageListAdapter(context);
//    }
//
//    protected void initFragment(Uri uri) {
//        RLog.d("ConversationFragment", "initFragment : " + uri + ",this=" + this);
//        if(uri != null) {
//            String typeStr = uri.getLastPathSegment().toUpperCase();
//            this.mConversationType = Conversation.ConversationType.valueOf(typeStr);
//            this.mTargetId = uri.getQueryParameter("targetId");
//            this.mRongExtension.setConversation(this.mConversationType, this.mTargetId);
//            RongIMClient.getInstance().getTextMessageDraft(this.mConversationType, this.mTargetId, new RongIMClient.ResultCallback<String>() {
//                public void onSuccess(String s) {
//                    ConversationYaFragment.this.mDraft = s;
//                    if(ConversationYaFragment.this.mRongExtension != null) {
//                        EditText editText = ConversationYaFragment.this.mRongExtension.getInputEditText();
//                        editText.setText(s);
//                        editText.setSelection(editText.length());
//                    }
//
//                }
//
//
//                public void onError(RongIMClient.ErrorCode e) {
//                }
//            });
//            this.mCurrentConversationInfo = ConversationInfo.obtain(this.mConversationType, this.mTargetId);
//            RongContext.getInstance().registerConversationInfo(this.mCurrentConversationInfo);
//            this.mRealTimeBar = this.mMsgListView.findViewById(io.rong.imkit.R.id.real_time_location_bar);
//            this.mRealTimeText = (TextView)this.mMsgListView.findViewById(io.rong.imkit.R.id.real_time_location_text);
//            if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.getActivity() != null && this.getActivity().getIntent() != null && this.getActivity().getIntent().getData() != null) {
//                this.mCustomUserInfo = (CSCustomServiceInfo)this.getActivity().getIntent().getParcelableExtra("customServiceInfo");
//            }
//
//            LocationManager.getInstance().bindConversation(this.getActivity(), this.mConversationType, this.mTargetId);
//            LocationManager.getInstance().setUserInfoProvider(this);
//            LocationManager.getInstance().setParticipantChangedListener(new IParticipantChangedListener() {
//                public void onParticipantChanged(List<String> userIdList) {
//                    if(!ConversationYaFragment.this.isDetached()) {
//                        if(userIdList != null) {
//                            if(userIdList.size() == 0) {
//                                ConversationYaFragment.this.mRealTimeBar.setVisibility(8);
//                            } else if(userIdList.size() == 1 && userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
//                                ConversationYaFragment.this.mRealTimeText.setText(ConversationYaFragment.this.getResources().getString(io.rong.imkit.R.string.rc_you_are_sharing_location));
//                                ConversationYaFragment.this.mRealTimeBar.setVisibility(0);
//                            } else if(userIdList.size() == 1 && !userIdList.contains(RongIM.getInstance().getCurrentUserId())) {
//                                ConversationYaFragment.this.mRealTimeText.setText(String.format(ConversationYaFragment.this.getResources().getString(io.rong.imkit.R.string.rc_other_is_sharing_location), new Object[]{ConversationYaFragment.this.getNameFromCache((String)userIdList.get(0))}));
//                                ConversationYaFragment.this.mRealTimeBar.setVisibility(0);
//                            } else {
//                                ConversationYaFragment.this.mRealTimeText.setText(String.format(ConversationYaFragment.this.getResources().getString(io.rong.imkit.R.string.rc_others_are_sharing_location), new Object[]{Integer.valueOf(userIdList.size())}));
//                                ConversationYaFragment.this.mRealTimeBar.setVisibility(0);
//                            }
//                        }
//
//                    }
//                }
//            });
//            this.mRealTimeBar.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId);
//                    if(status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {
//                        PromptPopupDialog intent = PromptPopupDialog.newInstance(ConversationYaFragment.this.getActivity(), "", ConversationYaFragment.this.getResources().getString(io.rong.imkit.R.string.rc_real_time_join_notification));
//                        intent.setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
//                            public void onPositiveButtonClicked() {
//                                LocationManager.getInstance().joinLocationSharing();
//                                Intent intent = new Intent(ConversationYaFragment.this.getActivity(), AMapRealTimeActivity.class);
//                                if(ConversationYaFragment.this.mLocationShareParticipants != null) {
//                                    intent.putStringArrayListExtra("participants", ConversationYaFragment.this.mLocationShareParticipants);
//                                }
//
//                                ConversationYaFragment.this.startActivityForResult(intent, 101);
//                            }
//                        });
//                        intent.show();
//                    } else {
//                        Intent intent1 = new Intent(ConversationYaFragment.this.getActivity(), AMapRealTimeActivity.class);
//                        if(ConversationYaFragment.this.mLocationShareParticipants != null) {
//                            intent1.putStringArrayListExtra("participants", ConversationYaFragment.this.mLocationShareParticipants);
//                        }
//
//                        ConversationYaFragment.this.startActivityForResult(intent1, 101);
//                    }
//
//                }
//            });
//            if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
//                boolean msg = this.getActivity() != null && this.getActivity().getIntent().getBooleanExtra("createIfNotExist", true);
//                int message = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_chatroom_first_pull_message_count);
//                if(msg) {
//                    RongIMClient.getInstance().joinChatRoom(this.mTargetId, message, new RongIMClient.OperationCallback() {
//                        public void onSuccess() {
//                            RLog.i("ConversationFragment", "joinChatRoom onSuccess : " + ConversationYaFragment.this.mTargetId);
//                        }
//
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//                            RLog.e("ConversationFragment", "joinChatRoom onError : " + errorCode);
//                            if(ConversationYaFragment.this.getActivity() != null) {
//                                ConversationYaFragment.this.onWarningDialog(ConversationYaFragment.this.getString(io.rong.imkit.R.string.rc_join_chatroom_failure));
//                            }
//
//                        }
//                    });
//                } else {
//                    RongIMClient.getInstance().joinExistChatRoom(this.mTargetId, message, new RongIMClient.OperationCallback() {
//                        public void onSuccess() {
//                            RLog.i("ConversationFragment", "joinExistChatRoom onSuccess : " + ConversationYaFragment.this.mTargetId);
//                        }
//
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//                            RLog.e("ConversationFragment", "joinExistChatRoom onError : " + errorCode);
//                            if(ConversationYaFragment.this.getActivity() != null) {
//                                ConversationYaFragment.this.onWarningDialog(ConversationYaFragment.this.getString(io.rong.imkit.R.string.rc_join_chatroom_failure));
//                            }
//
//                        }
//                    });
//                }
//            } else if(this.mConversationType != Conversation.ConversationType.APP_PUBLIC_SERVICE && this.mConversationType != Conversation.ConversationType.PUBLIC_SERVICE) {
//                if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
//                    this.enterTime = System.currentTimeMillis();
//                    this.mRongExtension.setExtensionBarMode(CustomServiceMode.CUSTOM_SERVICE_MODE_NO_SERVICE);
//                    RongIMClient.getInstance().startCustomService(this.mTargetId, this.customServiceListener, this.mCustomUserInfo);
//                } else if(this.mEnableMention && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
//                    RongMentionManager.getInstance().createInstance(this.mConversationType, this.mTargetId, this.mListAdapter, this.mRongExtension.getInputEditText());
//                }
//            } else {
//                PublicServiceCommandMessage msg1 = new PublicServiceCommandMessage();
//                msg1.setCommand(PublicServiceMenu.PublicServiceMenuItemType.Entry.getMessage());
//                Message message1 = Message.obtain(this.mTargetId, this.mConversationType, msg1);
//                RongIMClient.getInstance().sendMessage(message1, (String)null, (String)null, new IRongCallback.ISendMessageCallback() {
//                    public void onAttached(Message message) {
//                    }
//
//                    public void onSuccess(Message message) {
//                    }
//
//                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                    }
//                });
//                Conversation.PublicServiceType publicServiceType;
//                if(this.mConversationType == Conversation.ConversationType.PUBLIC_SERVICE) {
//                    publicServiceType = Conversation.PublicServiceType.PUBLIC_SERVICE;
//                } else {
//                    publicServiceType = Conversation.PublicServiceType.APP_PUBLIC_SERVICE;
//                }
//
//                RongIM.getInstance().getPublicServiceProfile(publicServiceType, this.mTargetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
//                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
//                        ArrayList inputMenuList = new ArrayList();
//                        PublicServiceMenu menu = publicServiceProfile.getMenu();
//                        ArrayList items = menu != null?menu.getMenuItems():null;
//                        if(items != null && ConversationYaFragment.this.mRongExtension != null) {
//                            ConversationYaFragment.this.mPublicServiceProfile = publicServiceProfile;
//                            Iterator i$ = items.iterator();
//
//                            while(i$.hasNext()) {
//                                PublicServiceMenuItem item = (PublicServiceMenuItem)i$.next();
//                                InputMenu inputMenu = new InputMenu();
//                                inputMenu.title = item.getName();
//                                inputMenu.subMenuList = new ArrayList();
//                                Iterator i$1 = item.getSubMenuItems().iterator();
//
//                                while(i$1.hasNext()) {
//                                    PublicServiceMenuItem i = (PublicServiceMenuItem)i$1.next();
//                                    inputMenu.subMenuList.add(i.getName());
//                                }
//
//                                inputMenuList.add(inputMenu);
//                            }
//
//                            ConversationYaFragment.this.mRongExtension.setInputMenu(inputMenuList, true);
//                        }
//
//                    }
//
//
//                    public void onError(RongIMClient.ErrorCode e) {
//                    }
//                });
//            }
//        }
//
//        RongIMClient.getInstance().getConversation(this.mConversationType, this.mTargetId, new RongIMClient.ResultCallback<Conversation>() {
//            public void onSuccess(Conversation conversation) {
//                if(conversation != null && ConversationYaFragment.this.getActivity() != null) {
//                    final int unreadCount = conversation.getUnreadMessageCount();
//                    if(unreadCount > 0) {
//                        ConversationYaFragment.this.sendReadReceiptAndSyncUnreadStatus(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId, conversation.getSentTime());
//                    }
//
//                    if(conversation.getMentionedCount() > 0) {
//                        ConversationYaFragment.this.getLastMentionedMessageId(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId);
//                    }
//
//                    if(unreadCount > 10 && ConversationYaFragment.this.mUnreadBtn != null) {
//                        if(unreadCount > 150) {
//                            ConversationYaFragment.this.mUnreadBtn.setText(String.format("%s%s", new Object[]{"150+", ConversationYaFragment.this.getActivity().getResources().getString(io.rong.imkit.R.string.rc_new_messages)}));
//                        } else {
//                            ConversationYaFragment.this.mUnreadBtn.setText(String.format("%s%s", new Object[]{Integer.valueOf(unreadCount), ConversationYaFragment.this.getActivity().getResources().getString(io.rong.imkit.R.string.rc_new_messages)}));
//                        }
//
//                        ConversationYaFragment.this.mUnreadBtn.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//                                ConversationYaFragment.this.mUnreadBtn.setClickable(false);
//                                TranslateAnimation animation = new TranslateAnimation(0.0F, 500.0F, 0.0F, 0.0F);
//                                animation.setDuration(500L);
//                                ConversationYaFragment.this.mUnreadBtn.startAnimation(animation);
//                                animation.setFillAfter(true);
//                                animation.setAnimationListener(new Animation.AnimationListener() {
//                                    public void onAnimationStart(Animation animation) {
//                                    }
//
//                                    public void onAnimationEnd(Animation animation) {
//                                        ConversationYaFragment.this.mUnreadBtn.setVisibility(8);
//                                        if(unreadCount <= 30) {
//                                            if(ConversationYaFragment.this.mList.getCount() < 30) {
//                                                ConversationYaFragment.this.mList.smoothScrollToPosition(ConversationYaFragment.this.mList.getCount() - unreadCount);
//                                            } else {
//                                                ConversationYaFragment.this.mList.smoothScrollToPosition(30 - unreadCount);
//                                            }
//                                        } else if(unreadCount > 30) {
//                                            ConversationYaFragment.this.getHistoryMessage(ConversationYaFragment.this.mConversationType, ConversationYaFragment.this.mTargetId, unreadCount - 30 - 1, 2);
//                                        }
//
//                                    }
//
//                                    public void onAnimationRepeat(Animation animation) {
//                                    }
//                                });
//                            }
//                        });
//                        TranslateAnimation translateAnimation = new TranslateAnimation(300.0F, 0.0F, 0.0F, 0.0F);
//                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
//                        translateAnimation.setDuration(1000L);
//                        alphaAnimation.setDuration(2000L);
//                        AnimationSet set = new AnimationSet(true);
//                        set.addAnimation(translateAnimation);
//                        set.addAnimation(alphaAnimation);
//                        ConversationYaFragment.this.mUnreadBtn.setVisibility(0);
//                        ConversationYaFragment.this.mUnreadBtn.startAnimation(set);
//                        set.setAnimationListener(new Animation.AnimationListener() {
//                            public void onAnimationStart(Animation animation) {
//                            }
//
//                            public void onAnimationEnd(Animation animation) {
//                                ConversationYaFragment.this.getHandler().postDelayed(new Runnable() {
//                                    public void run() {
//                                        TranslateAnimation animation = new TranslateAnimation(0.0F, 700.0F, 0.0F, 0.0F);
//                                        animation.setDuration(700L);
//                                        animation.setFillAfter(true);
//                                        ConversationYaFragment.this.mUnreadBtn.startAnimation(animation);
//                                    }
//                                }, 4000L);
//                            }
//
//                            public void onAnimationRepeat(Animation animation) {
//                            }
//                        });
//                    }
//                }
//
//            }
//
//
//
//            public void onError(RongIMClient.ErrorCode e) {
//            }
//        });
//        this.getHistoryMessage(this.mConversationType, this.mTargetId, 30, 3);
//        if(!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//
//    }
//
//    public boolean onResendItemClick(Message message) {
//        return false;
//    }
//
//    public void onReadReceiptStateClick(Message message) {
//    }
//
//    public void onSelectCustomerServiceGroup(final List<CSGroupItem> groupList) {
//        if(this.getActivity() != null) {
//            ArrayList singleDataList = new ArrayList();
//            singleDataList.clear();
//
//            for(int i = 0; i < groupList.size(); ++i) {
//                if(((CSGroupItem)groupList.get(i)).getOnline()) {
//                    singleDataList.add(((CSGroupItem)groupList.get(i)).getName());
//                }
//            }
//
//            if(singleDataList.size() == 0) {
//                RongIMClient.getInstance().selectCustomServiceGroup(this.mTargetId, (String)null);
//            } else {
//                final SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(this.getActivity(), singleDataList);
//                singleChoiceDialog.setTitle(this.getActivity().getResources().getString(io.rong.imkit.R.string.rc_cs_select_group));
//                singleChoiceDialog.setOnOKButtonListener(new android.content.DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        int selItem = singleChoiceDialog.getSelectItem();
//                        RongIMClient.getInstance().selectCustomServiceGroup(ConversationYaFragment.this.mTargetId, ((CSGroupItem)groupList.get(selItem)).getId());
//                    }
//                });
//                singleChoiceDialog.setOnCancelButtonListener(new android.content.DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        RongIMClient.getInstance().selectCustomServiceGroup(ConversationYaFragment.this.mTargetId, (String)null);
//                    }
//                });
//                singleChoiceDialog.show();
//            }
//        }
//    }
//
//    public void onPause() {
//        if(this.getActivity().isFinishing()) {
//            this.destroyExtension();
//            RongIM.getInstance().clearMessagesUnreadStatus(this.mConversationType, this.mTargetId, (RongIMClient.ResultCallback)null);
//        }
//
//        super.onPause();
//    }
//
//    private void destroyExtension() {
//        String text = this.mRongExtension.getInputEditText().getText().toString();
//        if(TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && TextUtils.isEmpty(this.mDraft) || !TextUtils.isEmpty(text) && !TextUtils.isEmpty(this.mDraft) && !text.equals(this.mDraft)) {
//            RongIMClient.getInstance().saveTextMessageDraft(this.mConversationType, this.mTargetId, text, (RongIMClient.ResultCallback)null);
//            Event.DraftEvent draft = new Event.DraftEvent(this.mConversationType, this.mTargetId, text);
//            RongContext.getInstance().getEventBus().post(draft);
//        }
//
//        this.mRongExtension.onDestroy();
//        this.mRongExtension = null;
//    }
//
//    public void onDestroy() {
//        if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
//            SendImageManager.getInstance().cancelSendingImages(this.mConversationType, this.mTargetId);
//            RongIM.getInstance().quitChatRoom(this.mTargetId, (RongIMClient.OperationCallback)null);
//        }
//
//        if(this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE) && this.mCSneedToQuit) {
//            RongIMClient.getInstance().stopCustomService(this.mTargetId);
//        }
//
//        if(this.mSyncReadStatus && this.mSyncReadStatusMsgTime > 0L && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
//            RongIMClient.getInstance().syncConversationReadStatus(this.mConversationType, this.mTargetId, this.mSyncReadStatusMsgTime, (RongIMClient.OperationCallback)null);
//        }
//
//        if(this.mEnableMention && (this.mConversationType.equals(Conversation.ConversationType.DISCUSSION) || this.mConversationType.equals(Conversation.ConversationType.GROUP))) {
//            RongMentionManager.getInstance().destroyInstance();
//        }
//
//        EventBus.getDefault().unregister(this);
//        AudioPlayManager.getInstance().stopPlay();
//        AudioRecordManager.getInstance().stopRecord();
//        RongContext.getInstance().unregisterConversationInfo(this.mCurrentConversationInfo);
//        LocationManager.getInstance().quitLocationSharing();
//        LocationManager.getInstance().setParticipantChangedListener((IParticipantChangedListener)null);
//        LocationManager.getInstance().setUserInfoProvider((IUserInfoProvider)null);
//        LocationManager.getInstance().unBindConversation();
//        super.onDestroy();
//    }
//
//    public boolean isLocationSharing() {
//        return LocationManager.getInstance().isSharing();
//    }
//
//    public void showQuitLocationSharingDialog(final Activity activity) {
//        PromptPopupDialog.newInstance(activity, this.getString(io.rong.imkit.R.string.rc_ext_warning), this.getString(io.rong.imkit.R.string.rc_real_time_exit_notification), this.getString(io.rong.imkit.R.string.rc_action_bar_ok)).setPromptButtonClickedListener(new PromptPopupDialog.OnPromptButtonClickedListener() {
//            public void onPositiveButtonClicked() {
//                activity.finish();
//            }
//        }).show();
//    }
//
//    public boolean onBackPressed() {
//        if(this.mRongExtension.isExtensionExpanded()) {
//            this.mRongExtension.collapseExtension();
//            return true;
//        } else {
//            return this.mConversationType != null && this.mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)?this.onCustomServiceEvaluation(false, "", this.robotType, this.evaluate):false;
//        }
//    }
//
//    public boolean handleMessage(android.os.Message msg) {
//        return false;
//    }
//
//    public void onWarningDialog(String msg) {
//        if(this.getActivity() != null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
//            builder.setCancelable(false);
//            final AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//            Window window = alertDialog.getWindow();
//            window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_warning);
//            TextView tv = (TextView)window.findViewById(io.rong.imkit.R.id.rc_cs_msg);
//            tv.setText(msg);
//            window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                    FragmentManager fm = ConversationYaFragment.this.getChildFragmentManager();
//                    if(fm.getBackStackEntryCount() > 0) {
//                        fm.popBackStack();
//                    } else {
//                        ConversationYaFragment.this.getActivity().finish();
//                    }
//
//                }
//            });
//        }
//    }
//
//    public void onCustomServiceWarning(String msg, final boolean evaluate) {
//        if(this.getActivity() != null) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
//            builder.setCancelable(false);
//            final AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//            Window window = alertDialog.getWindow();
//            window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_warning);
//            TextView tv = (TextView)window.findViewById(io.rong.imkit.R.id.rc_cs_msg);
//            tv.setText(msg);
//            window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    alertDialog.dismiss();
//                    if(evaluate) {
//                        ConversationYaFragment.this.onCustomServiceEvaluation(false, "", ConversationYaFragment.this.robotType, evaluate);
//                    } else {
//                        FragmentManager fm = ConversationYaFragment.this.getChildFragmentManager();
//                        if(fm.getBackStackEntryCount() > 0) {
//                            fm.popBackStack();
//                        } else {
//                            ConversationYaFragment.this.getActivity().finish();
//                        }
//                    }
//
//                }
//            });
//        }
//    }
//
//    public boolean onCustomServiceEvaluation(boolean isPullEva, final String dialogId, final boolean robotType, boolean evaluate) {
//        if(!evaluate) {
//            return false;
//        } else if(this.getActivity() == null) {
//            return false;
//        } else {
//            long currentTime = System.currentTimeMillis();
//            int interval = 60;
//
//            try {
//                interval = RongContext.getInstance().getResources().getInteger(io.rong.imkit.R.integer.rc_custom_service_evaluation_interval);
//            } catch (Resources.NotFoundException var14) {
//                var14.printStackTrace();
//            }
//
//            if(currentTime - this.enterTime < (long)(interval * 1000) && !isPullEva) {
//                InputMethodManager var15 = (InputMethodManager)this.getActivity().getSystemService("input_method");
//                if(var15 != null && var15.isActive() && this.getActivity().getCurrentFocus() != null && this.getActivity().getCurrentFocus().getWindowToken() != null) {
//                    var15.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 2);
//                }
//
//                FragmentManager var16 = this.getChildFragmentManager();
//                if(var16.getBackStackEntryCount() > 0) {
//                    var16.popBackStack();
//                } else {
//                    this.getActivity().finish();
//                }
//
//                return false;
//            } else {
//                this.committing = true;
//                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
//                builder.setCancelable(false);
//                final AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//                Window window = alertDialog.getWindow();
//                final LinearLayout linearLayout;
//                int i;
//                View child;
//                if(robotType) {
//                    window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_robot_evaluation);
//                    linearLayout = (LinearLayout)window.findViewById(io.rong.imkit.R.id.rc_cs_yes_no);
//                    if(this.resolved) {
//                        linearLayout.getChildAt(0).setSelected(true);
//                        linearLayout.getChildAt(1).setSelected(false);
//                    } else {
//                        linearLayout.getChildAt(0).setSelected(false);
//                        linearLayout.getChildAt(1).setSelected(true);
//                    }
//
//                    for(i = 0; i < linearLayout.getChildCount(); ++i) {
//                        child = linearLayout.getChildAt(i);
//                        child.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//                                v.setSelected(true);
//                                int index = linearLayout.indexOfChild(v);
//                                if(index == 0) {
//                                    linearLayout.getChildAt(1).setSelected(false);
//                                    ConversationYaFragment.this.resolved = true;
//                                } else {
//                                    ConversationYaFragment.this.resolved = false;
//                                    linearLayout.getChildAt(0).setSelected(false);
//                                }
//
//                            }
//                        });
//                    }
//                } else {
//                    window.setContentView(io.rong.imkit.R.layout.rc_cs_alert_human_evaluation);
//                    linearLayout = (LinearLayout)window.findViewById(io.rong.imkit.R.id.rc_cs_stars);
//
//                    for(i = 0; i < linearLayout.getChildCount(); ++i) {
//                        child = linearLayout.getChildAt(i);
//                        if(i < this.source) {
//                            child.setSelected(true);
//                        }
//
//                        child.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//                                int index = linearLayout.indexOfChild(v);
//                                int count = linearLayout.getChildCount();
//                                ConversationYaFragment.this.source = index + 1;
//                                if(!v.isSelected()) {
//                                    while(index >= 0) {
//                                        linearLayout.getChildAt(index).setSelected(true);
//                                        --index;
//                                    }
//                                } else {
//                                    ++index;
//
//                                    while(index < count) {
//                                        linearLayout.getChildAt(index).setSelected(false);
//                                        ++index;
//                                    }
//                                }
//
//                            }
//                        });
//                    }
//                }
//
//                window.findViewById(io.rong.imkit.R.id.rc_btn_cancel).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        ConversationYaFragment.this.committing = false;
//                        alertDialog.dismiss();
//                        FragmentManager fm = ConversationYaFragment.this.getChildFragmentManager();
//                        if(fm.getBackStackEntryCount() > 0) {
//                            fm.popBackStack();
//                        } else {
//                            ConversationYaFragment.this.getActivity().finish();
//                        }
//
//                    }
//                });
//                window.findViewById(io.rong.imkit.R.id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        if(robotType) {
//                            RongIMClient.getInstance().evaluateCustomService(ConversationYaFragment.this.mTargetId, ConversationYaFragment.this.resolved, "");
//                        } else if(ConversationYaFragment.this.source > 0) {
//                            RongIMClient.getInstance().evaluateCustomService(ConversationYaFragment.this.mTargetId, ConversationYaFragment.this.source, (String)null, dialogId);
//                        }
//
//                        alertDialog.dismiss();
//                        ConversationYaFragment.this.committing = false;
//                        FragmentManager fm = ConversationYaFragment.this.getChildFragmentManager();
//                        if(fm.getBackStackEntryCount() > 0) {
//                            fm.popBackStack();
//                        } else {
//                            ConversationYaFragment.this.getActivity().finish();
//                        }
//
//                    }
//                });
//                return true;
//            }
//        }
//    }
//
//    public void onSendToggleClick(View v, String text) {
//        if(!TextUtils.isEmpty(this.mTargetId) && this.mConversationType != null) {
//            if(!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
//                TextMessage textMessage = TextMessage.obtain(text);
//                MentionedInfo mentionedInfo = RongMentionManager.getInstance().onSendButtonClick();
//                if(mentionedInfo != null) {
//                    textMessage.setMentionedInfo(mentionedInfo);
//                }
//
//                Message message = Message.obtain(this.mTargetId, this.mConversationType, textMessage);
//                RongIM.getInstance().sendMessage(message, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
//            } else {
//                RLog.e("ConversationFragment", "text content must not be null");
//            }
//        } else {
//            Log.e("InputProvider", "the conversation hasn\'t been created yet!!!");
//        }
//    }
//
//    public void onImageResult(List<Uri> selectedImages, boolean origin) {
//        SendImageManager.getInstance().sendImages(this.mConversationType, this.mTargetId, selectedImages, origin);
//        if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
//            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:ImgMsg");
//        }
//
//    }
//
//    public void onEditTextClick(EditText editText) {
//    }
//
//    public void onLocationResult(double lat, double lng, String poi, Uri thumb) {
//        LocationMessage locationMessage = LocationMessage.obtain(lat, lng, poi, thumb);
//        Message message = Message.obtain(this.mTargetId, this.mConversationType, locationMessage);
//        RongIM.getInstance().sendLocationMessage(message, (String)null, (String)null, (IRongCallback.ISendMessageCallback)null);
//        if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
//            RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:LBSMsg");
//        }
//
//    }
//
//    public void onSwitchToggleClick(View v, ViewGroup inputBoard) {
//        if(this.robotType) {
//            RongIMClient.getInstance().switchToHumanMode(this.mTargetId);
//        }
//
//    }
//
//    public void onVoiceInputToggleTouch(View v, MotionEvent event) {
//        String[] permissions = new String[]{"android.permission.RECORD_AUDIO"};
//        if(!PermissionCheckUtil.checkPermissions(this.getActivity(), permissions)) {
//            if(event.getAction() == 0) {
//                PermissionCheckUtil.requestPermissions(this, permissions, 100);
//            }
//
//        } else {
//            if(event.getAction() == 0) {
//                AudioPlayManager.getInstance().stopPlay();
//                AudioRecordManager.getInstance().startRecord(v.getRootView(), this.mConversationType, this.mTargetId);
//                this.mLastTouchY = event.getY();
//                this.mUpDirection = false;
//                ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input_hover);
//            } else if(event.getAction() == 2) {
//                if(this.mLastTouchY - event.getY() > this.mOffsetLimit && !this.mUpDirection) {
//                    AudioRecordManager.getInstance().willCancelRecord();
//                    this.mUpDirection = true;
//                    ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input);
//                } else if(event.getY() - this.mLastTouchY > -this.mOffsetLimit && this.mUpDirection) {
//                    AudioRecordManager.getInstance().continueRecord();
//                    this.mUpDirection = false;
//                    ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input_hover);
//                }
//            } else if(event.getAction() == 1 || event.getAction() == 3) {
//                AudioRecordManager.getInstance().stopRecord();
//                ((Button)v).setText(io.rong.imkit.R.string.rc_audio_input);
//            }
//
//            if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE)) {
//                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:VcMsg");
//            }
//
//        }
//    }
//
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if(requestCode == 100 && grantResults[0] != 0) {
//            Toast.makeText(this.getActivity(), this.getResources().getString(io.rong.imkit.R.string.rc_permission_grant_needed), 0).show();
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
//    }
//
//    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {
//    }
//
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//    }
//
//    public void onTextChanged(CharSequence s, int start, int before, int count) {
//        int cursor;
//        int offset;
//        if(count == 0) {
//            cursor = start + before;
//            offset = -before;
//        } else {
//            cursor = start;
//            offset = count;
//        }
//
//        if(!this.mConversationType.equals(Conversation.ConversationType.GROUP) && !this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
//            if(this.mConversationType.equals(Conversation.ConversationType.PRIVATE) && offset != 0) {
//                RongIMClient.getInstance().sendTypingStatus(this.mConversationType, this.mTargetId, "RC:TxtMsg");
//            }
//        } else {
//            RongMentionManager.getInstance().onTextEdit(this.mConversationType, this.mTargetId, cursor, offset, s.toString());
//        }
//
//    }
//
//    public void afterTextChanged(Editable s) {
//    }
//
//    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if(event.getKeyCode() == 67 && event.getAction() == 0) {
//            EditText editText = (EditText)v;
//            int cursorPos = editText.getSelectionStart();
//            RongMentionManager.getInstance().onDeleteClick(this.mConversationType, this.mTargetId, editText, cursorPos);
//        }
//
//        return false;
//    }
//
//    public void onMenuClick(int root, int sub) {
//        if(this.mPublicServiceProfile != null) {
//            PublicServiceMenuItem item = (PublicServiceMenuItem)this.mPublicServiceProfile.getMenu().getMenuItems().get(root);
//            if(sub >= 0) {
//                item = (PublicServiceMenuItem)item.getSubMenuItems().get(sub);
//            }
//
//            if(item.getType().equals(PublicServiceMenu.PublicServiceMenuItemType.View)) {
//                IPublicServiceMenuClickListener msg = RongContext.getInstance().getPublicServiceMenuClickListener();
//                if(msg == null || !msg.onClick(this.mConversationType, this.mTargetId, item)) {
//                    String action = "io.rong.imkit.intent.action.webview";
//                    Intent intent = new Intent(action);
//                    intent.setPackage(this.getActivity().getPackageName());
//                    intent.addFlags(268435456);
//                    intent.putExtra("url", item.getUrl());
//                    this.getActivity().startActivity(intent);
//                }
//            }
//
//            PublicServiceCommandMessage msg1 = PublicServiceCommandMessage.obtain(item);
//            RongIMClient.getInstance().sendMessage(this.mConversationType, this.mTargetId, msg1, (String)null, (String)null, new IRongCallback.ISendMessageCallback() {
//                public void onAttached(Message message) {
//                }
//
//                public void onSuccess(Message message) {
//                }
//
//                public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                }
//            });
//        }
//
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 101 && resultCode == -1 && data != null) {
//            this.mLocationShareParticipants = data.getStringArrayListExtra("participants");
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//            this.mRongExtension.onActivityPluginResult(requestCode, resultCode, data);
//        }
//
//    }
//
//    public void onPluginClicked(IPluginModule pluginModule, int position) {
//    }
//
//    private String getNameFromCache(String targetId) {
//        UserInfo info = RongContext.getInstance().getUserInfoFromCache(targetId);
//        return info == null?targetId:info.getName();
//    }
//
//    public final void onEventMainThread(Event.ReadReceiptRequestEvent event) {
//        RLog.d("ConversationFragment", "ReadReceiptRequestEvent");
//        if((this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
//            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
//                if(((UIMessage)this.mListAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
//                    final UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
//                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
//                    if(readReceiptInfo == null) {
//                        readReceiptInfo = new ReadReceiptInfo();
//                        uiMessage.setReadReceiptInfo(readReceiptInfo);
//                    }
//
//                    if(readReceiptInfo.isReadReceiptMessage() && readReceiptInfo.hasRespond()) {
//                        return;
//                    }
//
//                    readReceiptInfo.setIsReadReceiptMessage(true);
//                    readReceiptInfo.setHasRespond(false);
//                    ArrayList messageList = new ArrayList();
//                    messageList.add(((UIMessage)this.mListAdapter.getItem(i)).getMessage());
//                    RongIMClient.getInstance().sendReadReceiptResponse(event.getConversationType(), event.getTargetId(), messageList, new RongIMClient.OperationCallback() {
//                        public void onSuccess() {
//                            uiMessage.getReadReceiptInfo().setHasRespond(true);
//                        }
//
//                        public void onError(RongIMClient.ErrorCode errorCode) {
//                            RLog.e("ConversationFragment", "sendReadReceiptResponse failed, errorCode = " + errorCode);
//                        }
//                    });
//                    break;
//                }
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(Event.ReadReceiptResponseEvent event) {
//        RLog.d("ConversationFragment", "ReadReceiptResponseEvent");
//        if((this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(event.getConversationType()) && event.getConversationType().equals(this.mConversationType) && event.getTargetId().equals(this.mTargetId)) {
//            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
//                if(((UIMessage)this.mListAdapter.getItem(i)).getUId().equals(event.getMessageUId())) {
//                    UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
//                    ReadReceiptInfo readReceiptInfo = uiMessage.getReadReceiptInfo();
//                    if(readReceiptInfo == null) {
//                        readReceiptInfo = new ReadReceiptInfo();
//                        readReceiptInfo.setIsReadReceiptMessage(true);
//                        uiMessage.setReadReceiptInfo(readReceiptInfo);
//                    }
//
//                    readReceiptInfo.setRespondUserIdList(event.getResponseUserIdList());
//                    int first = this.mList.getFirstVisiblePosition();
//                    int last = this.mList.getLastVisiblePosition();
//                    if(i >= first && i <= last) {
//                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
//                    }
//                    break;
//                }
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(Event.MessageDeleteEvent deleteEvent) {
//        RLog.d("ConversationFragment", "MessageDeleteEvent");
//        if(deleteEvent.getMessageIds() != null) {
//            Iterator i$ = deleteEvent.getMessageIds().iterator();
//
//            while(i$.hasNext()) {
//                long messageId = (long)((Integer)i$.next()).intValue();
//                int position = this.mListAdapter.findPosition(messageId);
//                if(position >= 0) {
//                    this.mListAdapter.remove(position);
//                }
//            }
//
//            this.mListAdapter.notifyDataSetChanged();
//        }
//
//    }
//
//    public final void onEventMainThread(Event.PublicServiceFollowableEvent event) {
//        RLog.d("ConversationFragment", "PublicServiceFollowableEvent");
//        if(event != null && !event.isFollow()) {
//            this.getActivity().finish();
//        }
//
//    }
//
//    public final void onEventMainThread(Event.MessagesClearEvent clearEvent) {
//        RLog.d("ConversationFragment", "MessagesClearEvent");
//        if(clearEvent.getTargetId().equals(this.mTargetId) && clearEvent.getType().equals(this.mConversationType)) {
//            this.mListAdapter.clear();
//            this.mListAdapter.notifyDataSetChanged();
//        }
//
//    }
//
//    public final void onEventMainThread(Event.MessageRecallEvent event) {
//        RLog.d("ConversationFragment", "MessageRecallEvent");
//        if(event.isRecallSuccess()) {
//            RecallNotificationMessage recallNotificationMessage = event.getRecallNotificationMessage();
//            int position = this.mListAdapter.findPosition((long)event.getMessageId());
//            if(position != -1) {
//                ((UIMessage)this.mListAdapter.getItem(position)).setContent(recallNotificationMessage);
//                int first = this.mList.getFirstVisiblePosition();
//                int last = this.mList.getLastVisiblePosition();
//                if(position >= first && position <= last) {
//                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
//                }
//            }
//        } else {
//            Toast.makeText(this.getActivity(), io.rong.imkit.R.string.rc_recall_failed, 0).show();
//        }
//
//    }
//
//    public final void onEventMainThread(Event.RemoteMessageRecallEvent event) {
//        RLog.d("ConversationFragment", "RemoteMessageRecallEvent");
//        int position = this.mListAdapter.findPosition((long)event.getMessageId());
//        int first = this.mList.getFirstVisiblePosition();
//        int last = this.mList.getLastVisiblePosition();
//        if(position >= 0) {
//            UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(position);
//            if(uiMessage.getMessage().getContent() instanceof VoiceMessage) {
//                AudioPlayManager.getInstance().stopPlay();
//            }
//
//            if(uiMessage.getMessage().getContent() instanceof FileMessage) {
//                RongIM.getInstance().cancelDownloadMediaMessage(uiMessage.getMessage(), (RongIMClient.OperationCallback)null);
//            }
//
//            uiMessage.setContent(event.getRecallNotificationMessage());
//            if(position >= first && position <= last) {
//                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(Message msg) {
//        RLog.d("ConversationFragment", "Event message : " + msg.getMessageId() + ", " + msg.getObjectName() + ", " + msg.getSentStatus());
//        if(this.mTargetId.equals(msg.getTargetId()) && this.mConversationType.equals(msg.getConversationType())) {
//            int position = this.mListAdapter.findPosition((long)msg.getMessageId());
//            if(position >= 0) {
//                ((UIMessage)this.mListAdapter.getItem(position)).setMessage(msg);
//                this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
//            } else {
//                this.mListAdapter.add(UIMessage.obtain(msg));
//                this.mListAdapter.notifyDataSetChanged();
//            }
//
//            if(msg.getSenderUserId() != null && msg.getSenderUserId().equals(RongIM.getInstance().getCurrentUserId()) && this.mList.getLastVisiblePosition() - 1 != this.mList.getCount()) {
//                this.mList.smoothScrollToPosition(this.mList.getCount());
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(Event.FileMessageEvent event) {
//        this.onEventMainThread(event.getMessage());
//    }
//
//    public void onEventMainThread(GroupUserInfo groupUserInfo) {
//        RLog.d("ConversationFragment", "GroupUserInfoEvent " + groupUserInfo.getGroupId() + " " + groupUserInfo.getUserId() + " " + groupUserInfo.getNickname());
//        if(groupUserInfo.getNickname() != null && groupUserInfo.getGroupId() != null) {
//            int count = this.mListAdapter.getCount();
//            int first = this.mList.getFirstVisiblePosition();
//            int last = this.mList.getLastVisiblePosition();
//
//            for(int i = 0; i < count; ++i) {
//                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
//                if(uiMessage.getSenderUserId().equals(groupUserInfo.getUserId())) {
//                    uiMessage.setNickName(true);
//                    UserInfo userInfo = uiMessage.getUserInfo();
//                    userInfo.setName(groupUserInfo.getNickname());
//                    uiMessage.setUserInfo(userInfo);
//                    if(i >= first && i <= last) {
//                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
//                    }
//                }
//            }
//
//        }
//    }
//
//    private View getListViewChildAt(int adapterIndex) {
//        int header = this.mList.getHeaderViewsCount();
//        int first = this.mList.getFirstVisiblePosition();
//        return this.mList.getChildAt(adapterIndex + header - first);
//    }
//
//    public final void onEventMainThread(Event.OnMessageSendErrorEvent event) {
//        this.onEventMainThread(event.getMessage());
//    }
//
//    public final void onEventMainThread(Event.OnReceiveMessageEvent event) {
//        Message message = event.getMessage();
//        RLog.i("ConversationFragment", "OnReceiveMessageEvent, " + message.getMessageId() + ", " + message.getObjectName() + ", " + message.getReceivedStatus().toString());
//        Conversation.ConversationType conversationType = message.getConversationType();
//        String targetId = message.getTargetId();
//        if(this.mConversationType.equals(conversationType) && this.mTargetId.equals(targetId)) {
//            if(event.getLeft() == 0 && message.getConversationType().equals(Conversation.ConversationType.PRIVATE) && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE) && message.getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
//                if(this.mReadRec) {
//                    RongIMClient.getInstance().sendReadReceiptMessage(message.getConversationType(), message.getTargetId(), message.getSentTime());
//                } else if(this.mSyncReadStatus) {
//                    RongIMClient.getInstance().syncConversationReadStatus(message.getConversationType(), message.getTargetId(), message.getSentTime(), (RongIMClient.OperationCallback)null);
//                }
//            }
//
//            if(this.mSyncReadStatus) {
//                this.mSyncReadStatusMsgTime = message.getSentTime();
//            }
//
//            if(message.getMessageId() > 0) {
//                Message.ReceivedStatus status = message.getReceivedStatus();
//                status.setRead();
//                message.setReceivedStatus(status);
//                RongIMClient.getInstance().setMessageReceivedStatus(message.getMessageId(), status, (RongIMClient.ResultCallback)null);
//            }
//
//            if(this.mNewMessageBtn != null && this.mList.getCount() - this.mList.getLastVisiblePosition() > 2 && Message.MessageDirection.SEND != message.getMessageDirection() && message.getConversationType() != Conversation.ConversationType.CHATROOM && message.getConversationType() != Conversation.ConversationType.CUSTOMER_SERVICE && message.getConversationType() != Conversation.ConversationType.APP_PUBLIC_SERVICE && message.getConversationType() != Conversation.ConversationType.PUBLIC_SERVICE) {
//                ++this.mNewMessageCount;
//                if(this.mNewMessageCount > 0) {
//                    this.mNewMessageBtn.setVisibility(0);
//                    this.mNewMessageTextView.setVisibility(0);
//                }
//
//                if(this.mNewMessageCount > 99) {
//                    this.mNewMessageTextView.setText("99+");
//                } else {
//                    this.mNewMessageTextView.setText(this.mNewMessageCount + "");
//                }
//            }
//
//            this.onEventMainThread(event.getMessage());
//        }
//
//    }
//
//    public final void onEventBackgroundThread(final Event.PlayAudioEvent event) {
//        this.getHandler().post(new Runnable() {
//            public void run() {
//                ConversationYaFragment.this.handleAudioPlayEvent(event);
//            }
//        });
//    }
//
//    private void handleAudioPlayEvent(Event.PlayAudioEvent event) {
//        RLog.i("ConversationFragment", "PlayAudioEvent");
//        int first = this.mList.getFirstVisiblePosition();
//        int last = this.mList.getLastVisiblePosition();
//        int position = this.mListAdapter.findPosition((long)event.messageId);
//        if(event.continuously && position >= 0) {
//            while(first <= last) {
//                ++position;
//                ++first;
//                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(position);
//                if(uiMessage != null && uiMessage.getContent() instanceof VoiceMessage && uiMessage.getMessageDirection().equals(Message.MessageDirection.RECEIVE) && !uiMessage.getReceivedStatus().isListened()) {
//                    uiMessage.continuePlayAudio = true;
//                    this.mListAdapter.getView(position, this.getListViewChildAt(position), this.mList);
//                    break;
//                }
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(Event.OnReceiveMessageProgressEvent event) {
//        if(this.mList != null) {
//            int first = this.mList.getFirstVisiblePosition();
//
//            for(int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
//                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(first);
//                if(uiMessage.getMessageId() == event.getMessage().getMessageId()) {
//                    uiMessage.setProgress(event.getProgress());
//                    if(this.isResumed()) {
//                        this.mListAdapter.getView(first, this.getListViewChildAt(first), this.mList);
//                    }
//                    break;
//                }
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(UserInfo userInfo) {
//        RLog.i("ConversationFragment", "userInfo " + userInfo.getUserId());
//        int first = this.mList.getFirstVisiblePosition();
//        int last = this.mList.getLastVisiblePosition();
//
//        for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
//            UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
//            if(userInfo.getUserId().equals(uiMessage.getSenderUserId()) && !uiMessage.isNickName()) {
//                if(uiMessage.getConversationType().equals(Conversation.ConversationType.CUSTOMER_SERVICE) && uiMessage.getMessage() != null && uiMessage.getMessage().getContent() != null && uiMessage.getMessage().getContent().getUserInfo() != null) {
//                    uiMessage.setUserInfo(uiMessage.getMessage().getContent().getUserInfo());
//                } else {
//                    uiMessage.setUserInfo(userInfo);
//                }
//
//                if(i >= first && i <= last) {
//                    this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
//                }
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(PublicServiceProfile publicServiceProfile) {
//        RLog.i("ConversationFragment", "publicServiceProfile");
//        int first = this.mList.getFirstVisiblePosition();
//
//        for(int last = this.mList.getLastVisiblePosition(); first <= last; ++first) {
//            UIMessage message = (UIMessage)this.mListAdapter.getItem(first);
//            if(message != null && (TextUtils.isEmpty(message.getTargetId()) || publicServiceProfile.getTargetId().equals(message.getTargetId()))) {
//                this.mListAdapter.getView(first, this.getListViewChildAt(first), this.mList);
//            }
//        }
//
//    }
//
//    public final void onEventMainThread(Event.ReadReceiptEvent event) {
//        RLog.i("ConversationFragment", "ReadReceiptEvent");
//        if(RongContext.getInstance().isReadReceiptConversationType(event.getMessage().getConversationType()) && this.mTargetId.equals(event.getMessage().getTargetId()) && this.mConversationType.equals(event.getMessage().getConversationType()) && event.getMessage().getMessageDirection().equals(Message.MessageDirection.RECEIVE)) {
//            ReadReceiptMessage content = (ReadReceiptMessage)event.getMessage().getContent();
//            long ntfTime = content.getLastMessageSendTime();
//
//            for(int i = this.mListAdapter.getCount() - 1; i >= 0; --i) {
//                UIMessage uiMessage = (UIMessage)this.mListAdapter.getItem(i);
//                if(uiMessage.getMessageDirection().equals(Message.MessageDirection.SEND) && uiMessage.getSentStatus() == Message.SentStatus.SENT && ntfTime >= uiMessage.getSentTime()) {
//                    uiMessage.setSentStatus(Message.SentStatus.READ);
//                    int first = this.mList.getFirstVisiblePosition();
//                    int last = this.mList.getLastVisiblePosition();
//                    if(i >= first && i <= last) {
//                        this.mListAdapter.getView(i, this.getListViewChildAt(i), this.mList);
//                    }
//                }
//            }
//        }
//
//    }
//
//    public MessageListAdapter getMessageAdapter() {
//        return this.mListAdapter;
//    }
//
//    public void getHistoryMessage(Conversation.ConversationType conversationType, String targetId, final int reqCount, final int scrollMode) {
//        this.mList.onRefreshStart(AutoRefreshListView.Mode.START);
//        int last = this.mListAdapter.getCount() == 0?-1:((UIMessage)this.mListAdapter.getItem(0)).getMessageId();
//        RongIMClient.getInstance().getHistoryMessages(conversationType, targetId, last, reqCount, new RongIMClient.ResultCallback<List<Message>>() {
//            public void onSuccess(List<Message> messages) {
//                RLog.i("ConversationFragment", "getHistoryMessage " + messages.size());
//                ConversationYaFragment.this.mHasMoreLocalMessages = messages.size() == reqCount;
//                ConversationYaFragment.this.mList.onRefreshComplete(reqCount, reqCount, false);
//                if(messages.size() > 0) {
//                    Iterator index = messages.iterator();
//
//                    while(index.hasNext()) {
//                        Message message = (Message)index.next();
//                        boolean contains = false;
//
//                        for(int uiMessage = 0; uiMessage < ConversationYaFragment.this.mListAdapter.getCount(); ++uiMessage) {
//                            contains = ((UIMessage) ConversationYaFragment.this.mListAdapter.getItem(uiMessage)).getMessageId() == message.getMessageId();
//                        }
//
//                        if(!contains) {
//                            UIMessage var7 = UIMessage.obtain(message);
//                            ConversationYaFragment.this.mListAdapter.add(var7, 0);
//                        }
//                    }
//
//                    if(scrollMode == 3) {
//                        ConversationYaFragment.this.mList.setTranscriptMode(2);
//                    } else {
//                        ConversationYaFragment.this.mList.setTranscriptMode(0);
//                    }
//
//                    ConversationYaFragment.this.mListAdapter.notifyDataSetChanged();
//                    if(ConversationYaFragment.this.mLastMentionMsgId > 0) {
//                        int var6 = ConversationYaFragment.this.mListAdapter.findPosition((long) ConversationYaFragment.this.mLastMentionMsgId);
//                        ConversationYaFragment.this.mList.smoothScrollToPosition(var6);
//                        ConversationYaFragment.this.mLastMentionMsgId = 0;
//                    } else if(2 == scrollMode) {
//                        ConversationYaFragment.this.mList.setSelection(0);
//                    } else if(scrollMode == 3) {
//                        ConversationYaFragment.this.mList.setSelection(ConversationYaFragment.this.mList.getCount());
//                    } else {
//                        ConversationYaFragment.this.mList.setSelection(messages.size() + 1);
//                    }
//
//                    ConversationYaFragment.this.sendReadReceiptResponseIfNeeded(messages);
//                }
//
//            }
//
//
//            public void onError(RongIMClient.ErrorCode e) {
//                ConversationYaFragment.this.mList.onRefreshComplete(reqCount, reqCount, false);
//                RLog.e("ConversationFragment", "getHistoryMessages " + e);
//            }
//        });
//    }
//
//    public void getRemoteHistoryMessages(Conversation.ConversationType conversationType, String targetId, final int reqCount) {
//        this.mList.onRefreshStart(AutoRefreshListView.Mode.START);
//        if(this.mConversationType.equals(Conversation.ConversationType.CHATROOM)) {
//            this.mList.onRefreshComplete(0, 0, false);
//        } else {
//            long lastTime = this.mListAdapter.getCount() == 0?0L:((UIMessage)this.mListAdapter.getItem(0)).getSentTime();
//            RongIMClient.getInstance().getRemoteHistoryMessages(conversationType, targetId, lastTime, reqCount, new RongIMClient.ResultCallback<List<Message>>() {
//                public void onSuccess(List<Message> messages) {
//                    RLog.i("ConversationFragment", "getRemoteHistoryMessages " + (messages == null?0:messages.size()));
//                    ConversationYaFragment.this.mList.onRefreshComplete(messages == null?0:messages.size(), reqCount, false);
//                    if(messages != null && messages.size() > 0) {
//                        ArrayList remoteList = new ArrayList();
//                        Iterator i$ = messages.iterator();
//
//                        while(i$.hasNext()) {
//                            Message uiMessage = (Message)i$.next();
//                            UIMessage uiMessage1 = UIMessage.obtain(uiMessage);
//                            remoteList.add(uiMessage1);
//                        }
//
//                        List remoteList1 = ConversationYaFragment.this.filterMessage(remoteList);
//                        if(remoteList1 != null && remoteList1.size() > 0) {
//                            i$ = remoteList1.iterator();
//
//                            while(i$.hasNext()) {
//                                UIMessage uiMessage2 = (UIMessage)i$.next();
//                                ConversationYaFragment.this.mListAdapter.add(uiMessage2, 0);
//                            }
//
//                            ConversationYaFragment.this.mList.setTranscriptMode(0);
//                            ConversationYaFragment.this.mListAdapter.notifyDataSetChanged();
//                            ConversationYaFragment.this.mList.setSelection(messages.size() + 1);
//                            ConversationYaFragment.this.sendReadReceiptResponseIfNeeded(messages);
//                        }
//                    }
//
//                }
//
//
//                public void onError(RongIMClient.ErrorCode e) {
//                    RLog.e("ConversationFragment", "getRemoteHistoryMessages " + e);
//                    if(e.equals(RongIMClient.ErrorCode.ROAMING_SERVICE_UNAVAILABLE_CHATROOM)) {
//                        ConversationYaFragment.this.mList.onRefreshComplete(0, reqCount, false);
//                    } else {
//                        ConversationYaFragment.this.mList.onRefreshComplete(reqCount, reqCount, false);
//                    }
//
//                }
//            });
//        }
//    }
//
//    private List<UIMessage> filterMessage(List<UIMessage> srcList) {
//        Object destList;
//        if(this.mListAdapter.getCount() > 0) {
//            destList = new ArrayList();
//
//            for(int i = 0; i < this.mListAdapter.getCount(); ++i) {
//                Iterator i$ = srcList.iterator();
//
//                while(i$.hasNext()) {
//                    UIMessage msg = (UIMessage)i$.next();
//                    if(!((List)destList).contains(msg) && msg.getMessageId() != ((UIMessage)this.mListAdapter.getItem(i)).getMessageId()) {
//                        ((List)destList).add(msg);
//                    }
//                }
//            }
//        } else {
//            destList = srcList;
//        }
//
//        return (List)destList;
//    }
//
//    private void sendReadReceiptAndSyncUnreadStatus(Conversation.ConversationType conversationType, String targetId, long timeStamp) {
//        if(conversationType == Conversation.ConversationType.PRIVATE) {
//            if(this.mReadRec && RongContext.getInstance().isReadReceiptConversationType(Conversation.ConversationType.PRIVATE)) {
//                RongIMClient.getInstance().sendReadReceiptMessage(conversationType, targetId, timeStamp);
//            } else if(this.mSyncReadStatus) {
//                RongIMClient.getInstance().syncConversationReadStatus(conversationType, targetId, timeStamp, (RongIMClient.OperationCallback)null);
//            }
//        } else if(conversationType.equals(Conversation.ConversationType.GROUP) || conversationType.equals(Conversation.ConversationType.DISCUSSION)) {
//            RongIMClient.getInstance().syncConversationReadStatus(conversationType, targetId, timeStamp, (RongIMClient.OperationCallback)null);
//        }
//
//    }
//
//    private void getLastMentionedMessageId(Conversation.ConversationType conversationType, String targetId) {
//        RongIMClient.getInstance().getUnreadMentionedMessages(conversationType, targetId, new RongIMClient.ResultCallback<List<Message>>() {
//            public void onSuccess(List<Message> messages) {
//                if(messages != null && messages.size() > 0) {
//                    ConversationYaFragment.this.mLastMentionMsgId = ((Message)messages.get(0)).getMessageId();
//                    int index = ConversationYaFragment.this.mListAdapter.findPosition((long) ConversationYaFragment.this.mLastMentionMsgId);
//                    RLog.i("ConversationFragment", "getLastMentionedMessageId " + ConversationYaFragment.this.mLastMentionMsgId + " " + index);
//                    if(ConversationYaFragment.this.mLastMentionMsgId > 0 && index >= 0) {
//                        ConversationYaFragment.this.mList.smoothScrollToPosition(index);
//                        ConversationYaFragment.this.mLastMentionMsgId = 0;
//                    }
//                }
//
//            }
//
//
//            public void onError(RongIMClient.ErrorCode e) {
//            }
//        });
//    }
//
//    private void sendReadReceiptResponseIfNeeded(List<Message> messages) {
//        if(this.mReadRec && (this.mConversationType.equals(Conversation.ConversationType.GROUP) || this.mConversationType.equals(Conversation.ConversationType.DISCUSSION)) && RongContext.getInstance().isReadReceiptConversationType(this.mConversationType)) {
//            ArrayList responseMessageList = new ArrayList();
//            Iterator i$ = messages.iterator();
//
//            while(i$.hasNext()) {
//                Message message = (Message)i$.next();
//                ReadReceiptInfo readReceiptInfo = message.getReadReceiptInfo();
//                if(readReceiptInfo != null && readReceiptInfo.isReadReceiptMessage() && !readReceiptInfo.hasRespond()) {
//                    responseMessageList.add(message);
//                }
//            }
//
//            if(responseMessageList.size() > 0) {
//                RongIMClient.getInstance().sendReadReceiptResponse(this.mConversationType, this.mTargetId, responseMessageList, (RongIMClient.OperationCallback)null);
//            }
//        }
//
//    }
//
//    public void onExtensionCollapsed() {
//    }
//
//    public void onExtensionExpanded(int h) {
//        this.mList.setTranscriptMode(2);
//        this.mList.smoothScrollToPosition(this.mList.getCount());
//    }
//}