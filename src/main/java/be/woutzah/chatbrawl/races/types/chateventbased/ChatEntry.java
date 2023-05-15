package be.woutzah.chatbrawl.races.types.chateventbased;

import be.woutzah.chatbrawl.settings.SettingManager;

import java.util.List;

public abstract class ChatEntry {
    protected static List<Integer> rewardIds;
    protected String chatEntry;
    protected List<String> chatEntryAnswerList;
    protected SettingManager settingManager;

    public ChatEntry(String chatEntry, List<String> chatEntryAnswerList, List<Integer> rewardIds) {
        ChatEntry.rewardIds = rewardIds;
        this.chatEntry = chatEntry;
        this.chatEntryAnswerList = chatEntryAnswerList;
    }

    public static List<Integer> getRewardIds() {
        return rewardIds;
    }
    public void setChatEntry(String scrambledWord) {
        this.chatEntry = scrambledWord;
    }

    public String getChatEntry() {
        return chatEntry;
    }

    public String getFirstChatEntryAnswer() {
        return chatEntryAnswerList.get(0);
    }
    public boolean matchesAnswer(String string) {
        return chatEntryAnswerList.stream().anyMatch(s -> s.equals(string));
    }

    public boolean checkAnswer(String answer) {
        return chatEntryAnswerList.stream().anyMatch(s -> s.equals(answer));
    }
}
