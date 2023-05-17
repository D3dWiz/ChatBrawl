package be.woutzah.chatbrawl.races.types.chateventbased;

import be.woutzah.chatbrawl.races.types.chateventbased.ChatEntry;

import java.util.List;

public class ScrambleWord extends ChatEntry {

    public ScrambleWord(String chatEntry, List<String> chatEntryAnswer, List<Integer> rewardIds, int difficulty) {
        super(chatEntry, chatEntryAnswer, rewardIds);
    }
}
