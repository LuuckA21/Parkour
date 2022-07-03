package me.luucka.parkour;

import lombok.Getter;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

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

    private String setAllParameters;

    private String targetWallSign;

    private String waitingInput;

    private String cancelInput;

    private String addedPlayerCommands;

    private String addedConsoleCommands;

    private String clearPlayerCommands;

    private String clearConsoleCommands;

    private String waitBeforeJoin;

    private String waitingCooldownInput;

    private String addedCooldown;

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

    public String setAllParameters() {
        return prefix + setAllParameters;
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

    public String waitBeforeJoin(final String parkour, final long currentDateTime) {
        Instant instant = Instant.ofEpochMilli(currentDateTime);
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        String formatted = DateTimeFormatter.ofPattern("HH:mm:ss").format(datetime);


        return prefix + waitBeforeJoin.replace("{PARKOUR}", parkour).replace("{TIME}", formatted);
    }

    public String waitingCooldownInput() {
        return prefix + waitingCooldownInput;
    }

    public String addedCooldown(final String parkour) {
        return prefix + addedCooldown.replace("{PARKOUR}", parkour);
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
        noPermission = config.getString("no-permission", "");
        noConsole = config.getString("no-console", "");
        reload = config.getString("reload", "");
        commandUsage = config.getString("command-usage", "");
        joinParkour = config.getString("join-parkour", "");
        quitParkour = config.getString("quit-parkour", "");
        completeParkour = config.getString("complete-parkour", "");
        notExists = config.getString("not-exists", "");
        joinDuringSetup = config.getString("join-during-setup", "");
        joinDuringParkour = config.getString("join-during-parkour", "");
        alreadyInParkour = config.getString("already-in-parkour", "");
        alreadyInSetup = config.getString("already-in-setup", "");
        notInParkour = config.getString("not-in-parkour", "");
        enterSetupMode = config.getString("enter-setup-mode", "");
        deleteParkour = config.getString("delete-parkour", "");
        setStart = config.getString("set-start", "");
        setEnd = config.getString("set-end", "");
        setPos1 = config.getString("set-pos1", "");
        setPos2 = config.getString("set-pos2", "");
        save = config.getString("save", "");
        cancel = config.getString("cancel", "");
        setAllParameters = config.getString("set-all-parameters", "");
        targetWallSign = config.getString("target-wall-sign", "");
        waitingInput = config.getString("waiting-input", "");
        cancelInput = config.getString("cancel-input", "");
        addedPlayerCommands = config.getString("added-player-commands", "");
        addedConsoleCommands = config.getString("added-console-commands", "");
        clearPlayerCommands = config.getString("clear-player-commands", "");
        clearConsoleCommands = config.getString("clear-console-commands", "");
        waitBeforeJoin = config.getString("wait-before-join", "");
        waitingCooldownInput = config.getString("waiting-cooldown-input", "");
        addedCooldown = config.getString("added-cooldown", "");
    }

    private String _getPrefix() {
        String prefix = config.getString("prefix", "");
        return prefix.isEmpty() ? "" : prefix + " ";
    }
}
