package be.woutzah.chatbrawl.files;

public enum ConfigType {
    MAIN("config.yml"),
    LANGUAGE("language.yml"),
    REWARDS("rewards/rewards.yml"),
    TYPERACE("races/typerace.yml"),
    BLOCKRACE("races/blockrace.yml"),
    FISHRACE("races/fishrace.yml"),
    FOODRACE("races/foodrace.yml"),
    HUNTRACE("races/huntrace.yml"),
    QUIZRACE("races/quizrace.yml"),
    CRAFTRACE("races/craftrace.yml"),
    SCRAMBLERACE("races/scramblerace.yml"),
    LEADERBOARD("leaderboards/leaderboard.yml");

    private final String path;

    ConfigType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
