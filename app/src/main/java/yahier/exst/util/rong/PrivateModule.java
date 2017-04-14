package yahier.exst.util.rong;

import com.stbl.stbl.act.im.rong.BusinessCardProvider;
import com.stbl.stbl.act.im.rong.CollectProvider;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.RongExtension;
import io.rong.imkit.emoticon.IEmoticonTab;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imlib.model.Conversation;

//消息会话列表 底部的输入类型。当前单聊 群聊 与商家聊 都是同样的显示。
public class PrivateModule extends DefaultExtensionModule {
    private BusinessCardProvider cardProvider;//名片
    private CollectProvider collectProvider; // 收藏

    @Override
    public void onAttachedToExtension(RongExtension extension) {
        cardProvider = new BusinessCardProvider(extension.getContext());
        collectProvider = new CollectProvider(extension.getContext());
        super.onAttachedToExtension(extension);
    }

    @Override
    public void onDetachedFromExtension() {
        if (cardProvider != null) {
            cardProvider = null;
        }

        if (collectProvider != null) {
            collectProvider = null;
        }

        super.onDetachedFromExtension();
    }

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModules =  new ArrayList<>();//super.getPluginModules(conversationType);
        ImagePlugin image = new ImagePlugin();
        pluginModules.add(image);
        pluginModules.add(cardProvider);
        pluginModules.add(collectProvider);

        return pluginModules;
    }

    @Override
    public List<IEmoticonTab> getEmoticonTabs() {
        return super.getEmoticonTabs();
    }
}