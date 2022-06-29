package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;

import java.io.File;

public class Messages implements IConfig {

    private final ParkourPlugin plugin;

    private final BaseConfiguration config;

    @Getter
    private String prefix;

    private String noPermission;

    private String noConsole;

    private String reload;

    private String commandUsage;

    private String joinParkour;

    private String quitParkour;

    private String completeParkour;

    private String notExists;

    private String joinDuringSetup;

    private String joinDuringParkour;

    private String alreadyInParkour;

    private String alreadyInSetup;

    private String notInParkour;

    private String enterSetupMode;

    private String deleteParkour;

    private String setStart;

    private String setEnd;

    private String setPos1;

    private String setPos2;

    private String save;

    private String cancel;

    private String setAllLocation;

    private String targetWallSign;

    private String waitingInput;

    private String cancelInput;

    private String addedPlayerCommands;

    private String addedConsoleCommands;

    private String clearPlayerCommands;

    private String clearConsoleCommands;

    public String noPermission() {
        return prefix + noPermission;
    }

    public String noConsole() {
        return prefix + noConsole;
    }

    public String reload() {
        return prefix + reload;
    }

    public String commandUsage(final String usage) {
        return prefix + commandUsage.replace("{COMMAND_USAGE}", usage);
    }

    public String joinParkour(final String parkour) {
        return prefix + joinParkour.replace("{PARKOUR}", parkour);
    }

    public String quitParkour(final String parkour) {
        return prefix + quitParkour.replace("{PARKOUR}", parkour);
    }

    public String completeParkour(final String parkour) {
        return prefix + completeParkour.replace("{PARKOUR}", parkour);
    }

    public String notExists(final String parkour) {
        return prefix + notExists.replace("{PARKOUR}", parkour);
    }

    public String joinDuringSetup() {
        return prefix + joinDuringSetup;
    }

    public String joinDuringParkour() {
        return prefix + joinDuringParkour;
    }

    public String alreadyInParkour() {
        return prefix + alreadyInParkour;
    }

    public String alreadyInSetup() {
        return prefix + alreadyInSetup;
    }

    public String notInParkour() {
        return prefix + notInParkour;
    }

    public String enterSetupMode(final String parkour) {
        return prefix + enterSetupMode.replace("{PARKOUR}", parkour);
    }

    public String deleteParkour(final String parkour) {
        return prefix + deleteParkour.replace("{PARKOUR}", parkour);
    }

    public String setStart(final String parkour) {
        return prefix + setStart.replace("{PARKOUR}", parkour);
    }

    public String setEnd(final String parkour) {
        return prefix + setEnd.replace("{PARKOUR}", parkour);
    }

    public String setPos1(final String parkour) {
        return prefix + setPos1.replace("{PARKOUR}", parkour);
    }

    public String setPos2(final String parkour) {
        return prefix + setPos2.replace("{PARKOUR}", parkour);
    }

    public String save(final String parkour) {
        return prefix + save.replace("{PARKOUR}", parkour);
    }

    public String cancel(final String parkour) {
        return prefix + cancel.replace("{PARKOUR}", parkour);
    }

    public String setAllLocation() {
        return prefix + setAllLocation;
    }

    public String targetWallSign() {
        return prefix + targetWallSign;
    }

    public String waitingInput() {
        return prefix + waitingInput;
    }

    public String cancelInput() {
        return prefix + cancelInput;
    }

    public String addedPlayerCommands(final String parkour) {
        return prefix + addedPlayerCommands.replace("{PARKOUR}", parkour);
    }

    public String addedConsoleCommands(final String parkour) {
        return prefix + addedConsoleCommands.replace("{PARKOUR}", parkour);
    }

    public String clearPlayerCommands(final String parkour) {
        return prefix + clearPlayerCommands.replace("{PARKOUR}", parkour);
    }

    public String clearConsoleCommands(final String parkour) {
        return prefix + clearConsoleCommands.replace("{PARKOUR}", parkour);
    }

    public Messages(ParkourPlugin plugin) {
        this.plugin = plugin;
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "messages.yml"), "/messages.yml");
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
        prefix = _getPrefix();
        noPermission = _getNoPermission();
        noConsole = _getNoConsole();
        reload = _getReload();
        commandUsage = _getCommandUsage();
        joinParkour = _getJoin();
        quitParkour = _getQuit();
        completeParkour = _getCompleteParkour();
        notExists = _getNotExists();
        joinDuringSetup = _getJoinDuringSetup();
        joinDuringParkour = _getJoinDuringParkour();
        alreadyInParkour = _getAlreadyInParkour();
        alreadyInSetup = _getAlreadyInSetup();
        notInParkour = _getNotInParkour();
        enterSetupMode = _getEnterSetupMode();
        deleteParkour = _getDeleteParkour();
        setStart = _getSetStart();
        setEnd = _getSetEnd();
        setPos1 = _getSetPos1();
        setPos2 = _getSetPos2();
        save = _getSave();
        cancel = _getCancel();
        setAllLocation = _getSetAllLocation();
        targetWallSign = _getTargetWallSign();
        waitingInput = _getWaitingInput();
        cancelInput = _getCancelInput();
        addedPlayerCommands = _getAddedPlayerCommands();
        addedConsoleCommands = _getAddedConsoleCommands();
        clearPlayerCommands = _getClearPlayerCommands();
        clearConsoleCommands = _getClearConsoleCommands();
    }

    private String _getPrefix() {
        String prefix = config.getString("prefix", "");
        return prefix.isEmpty() ? "" : prefix + " ";
    }

    private String _getNoPermission() {
        return config.getString("no-permission", "");
    }

    private String _getNoConsole() {
        return config.getString("no-console", "");
    }

    private String _getReload() {
        return config.getString("reload", "");
    }

    private String _getCommandUsage() {
        return config.getString("command-usage", "");
    }

    private String _getJoin() {
        return config.getString("join-parkour", "");
    }

    private String _getQuit() {
        return config.getString("quit-parkour", "");
    }

    private String _getCompleteParkour() {
        return config.getString("complete-parkour", "");
    }

    private String _getNotExists() {
        return config.getString("not-exists", "");
    }

    private String _getJoinDuringSetup() {
        return config.getString("join-during-setup", "");
    }

    private String _getJoinDuringParkour() {
        return config.getString("join-during-parkour", "");
    }

    private String _getAlreadyInParkour() {
        return config.getString("already-in-parkour", "");
    }

    private String _getAlreadyInSetup() {
        return config.getString("already-in-setup", "");
    }

    private String _getNotInParkour() {
        return config.getString("not-in-parkour", "");
    }

    private String _getEnterSetupMode() {
        return config.getString("enter-setup-mode", "");
    }

    private String _getDeleteParkour() {
        return config.getString("delete-parkour", "");
    }

    private String _getSetStart() {
        return config.getString("set-start", "");
    }

    private String _getSetEnd() {
        return config.getString("set-end", "");
    }

    private String _getSetPos1() {
        return config.getString("set-pos1", "");
    }

    private String _getSetPos2() {
        return config.getString("set-pos2", "");
    }

    private String _getSave() {
        return config.getString("save", "");
    }

    private String _getCancel() {
        return config.getString("cancel", "");
    }

    private String _getSetAllLocation() {
        return config.getString("set-all-location", "");
    }

    private String _getTargetWallSign() {
        return config.getString("target-wall-sign", "");
    }

    private String _getWaitingInput() {
        return config.getString("waiting-input", "");
    }

    private String _getCancelInput() {
        return config.getString("cancel-input", "");
    }

    private String _getAddedPlayerCommands() {
        return config.getString("added-player-commands", "");
    }

    private String _getAddedConsoleCommands() {
        return config.getString("added-console-commands", "");
    }

    private String _getClearPlayerCommands() {
        return config.getString("clear-player-commands", "");
    }

    private String _getClearConsoleCommands() {
        return config.getString("clear-console-commands", "");
    }
}
