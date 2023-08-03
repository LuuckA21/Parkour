package me.luucka.parkour.setting;

import lombok.Getter;
import me.luucka.parkour.ParkourPlugin;
import me.luucka.parkour.config.BaseConfiguration;
import me.luucka.parkour.config.IConfig;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Messages implements IConfig {

    private final BaseConfiguration config;

    @Getter
    private String prefix;

    private String reload,
            commandUsage,
            deleteParkour,
            setLobby,
            noPermission,
            noConsole,
            notExists,
            joinDuringSetup,
            joinDuringParkour,
            alreadyInParkour,
            alreadyInSetup,
            notInParkour,
            parkourJoin,
            parkourLeave,
            parkourCompleted,
            parkourWaitJoin,
            timeDeathsLayout,
            setupEnterMode,
            setupSetStartLoc,
            setupSetEndLoc,
            setupTargetWallSign,
            setupSetPos1,
            setupSetPos2,
            moreOptionsMenuTitle,
            setupAddCompleteCommands,
            setupClearCompleteCommands,
            completeCommandsGuiTitle,
            setupSetCooldown,
            setupResetCooldown,
            setCooldownGuiTitle,
            setupInserValidCooldown,
            setupSave,
            setupSetAllParameters,
            setupCancel,
            checkpointMenuTitle,
            setNewCheckpoint,
            tpToCheckpoint,
            removeCheckpoint,
            updateCheckpoint,
            resetAllCheckpoints;

    private String[] completeSign;

    public String reload() {
        return reload;
    }

    public String commandUsage(final String usage) {
        return commandUsage.replace("{COMMAND_USAGE}", usage);
    }

    public String deleteParkour(final String parkour) {
        return deleteParkour.replace("{PARKOUR}", parkour);
    }

    public String setLobby() {
        return setLobby;
    }

    public String noPermission() {
        return noPermission;
    }

    public String noConsole() {
        return noConsole;
    }

    public String notExists(final String parkour) {
        return notExists.replace("{PARKOUR}", parkour);
    }

    public String joinDuringSetup() {
        return joinDuringSetup;
    }

    public String joinDuringParkour() {
        return joinDuringParkour;
    }

    public String alreadyInParkour() {
        return alreadyInParkour;
    }

    public String alreadyInSetup() {
        return alreadyInSetup;
    }

    public String notInParkour() {
        return notInParkour;
    }

    public String parkourJoin(final String parkour) {
        return parkourJoin.replace("{PARKOUR}", parkour);
    }

    public String parkourLeave(final String parkour) {
        return parkourLeave.replace("{PARKOUR}", parkour);
    }

    public String parkourCompleted(final String parkour) {
        return parkourCompleted.replace("{PARKOUR}", parkour);
    }

    public String parkourWaitJoin(final String parkour, final long time) {
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return parkourWaitJoin.replace("{PARKOUR}", parkour).replace("{TIME}", DateTimeFormatter.ofPattern("HH:mm:ss").format(datetime));
    }

    public String timeDeathsLayout(final long time, final int deaths) {
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime datetime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return timeDeathsLayout.replace("{TIME}", DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(datetime)).replace("{DEATHS}", String.valueOf(deaths));
    }

    public String setupEnterMode(final String parkour) {
        return setupEnterMode.replace("{PARKOUR}", parkour);
    }

    public String setupSetStartLoc(final String parkour) {
        return setupSetStartLoc.replace("{PARKOUR}", parkour);
    }

    public String setupSetEndLoc(final String parkour) {
        return setupSetEndLoc.replace("{PARKOUR}", parkour);
    }

    public String setupTargetWallSign() {
        return setupTargetWallSign;
    }

    public String setupSetPos1(final String parkour) {
        return setupSetPos1.replace("{PARKOUR}", parkour);
    }

    public String setupSetPos2(final String parkour) {
        return setupSetPos2.replace("{PARKOUR}", parkour);
    }

    public String moreOptionsMenuTitle(final String parkour) {
        return moreOptionsMenuTitle.replace("{PARKOUR}", parkour);
    }

    public String setupAddCompleteCommands(final String parkour) {
        return setupAddCompleteCommands.replace("{PARKOUR}", parkour);
    }

    public String setupClearCompleteCommands(final String parkour) {
        return setupClearCompleteCommands.replace("{PARKOUR}", parkour);
    }

    public String completeCommandsGuiTitle() {
        return completeCommandsGuiTitle;
    }

    public String setupSetCooldown(final String parkour) {
        return setupSetCooldown.replace("{PARKOUR}", parkour);
    }

    public String setupResetCooldown(final String parkour) {
        return setupResetCooldown.replace("{PARKOUR}", parkour);
    }

    public String setCooldownGuiTitle() {
        return setCooldownGuiTitle;
    }

    public String setupInserValidCooldown() {
        return setupInserValidCooldown;
    }

    public String setupSave(final String parkour) {
        return setupSave.replace("{PARKOUR}", parkour);
    }

    public String setupSetAllParameters() {
        return setupSetAllParameters;
    }

    public String setupCancel(final String parkour) {
        return setupCancel.replace("{PARKOUR}", parkour);
    }

    public String getCheckpointMenuTitle(final String parkour) {
        return checkpointMenuTitle.replace("{PARKOUR}", parkour);
    }

    public String getSetNewCheckpoint(final String parkour, final int number) {
        return setNewCheckpoint.replace("{PARKOUR}", parkour).replace("{NUMBER}", Integer.toString(number));
    }

    public String getTpToCheckpoint(final String parkour, final int number) {
        return tpToCheckpoint.replace("{PARKOUR}", parkour).replace("{NUMBER}", Integer.toString(number));
    }

    public String getRemoveCheckpoint(final String parkour, final int number) {
        return removeCheckpoint.replace("{PARKOUR}", parkour).replace("{NUMBER}", Integer.toString(number));
    }

    public String getUpdateCheckpoint(final String parkour, final int number) {
        return updateCheckpoint.replace("{PARKOUR}", parkour).replace("{NUMBER}", Integer.toString(number));
    }

    public String getResetAllCheckpoints(final String parkour) {
        return resetAllCheckpoints.replace("{PARKOUR}", parkour);
    }

    public String[] completeSign(final String parkour) {
        final String[] newSign = new String[4];
        for (int i = 0; i < completeSign.length; i++) {
            newSign[i] = completeSign[i].replace("{PARKOUR}", parkour);
        }
        return newSign;
    }

    public Messages(ParkourPlugin plugin) {
        this.config = new BaseConfiguration(new File(plugin.getDataFolder(), "messages.yml"), "/messages.yml");
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        config.load();
        prefix = config.getString("prefix", "");
        reload = config.getString("reload", "").replace("{PREFIX}", prefix);
        commandUsage = config.getString("command-usage", "").replace("{PREFIX}", prefix);
        deleteParkour = config.getString("delete-parkour", "").replace("{PREFIX}", prefix);
        setLobby = config.getString("set-lobby", "").replace("{PREFIX}", prefix);

        noPermission = config.getString("error.no-permission", "").replace("{PREFIX}", prefix);
        noConsole = config.getString("error.no-console", "").replace("{PREFIX}", prefix);
        notExists = config.getString("error.not-exists", "").replace("{PREFIX}", prefix);
        joinDuringSetup = config.getString("error.join-during-setup", "").replace("{PREFIX}", prefix);
        joinDuringParkour = config.getString("error.join-during-parkour", "").replace("{PREFIX}", prefix);
        alreadyInParkour = config.getString("error.already-in-parkour", "").replace("{PREFIX}", prefix);
        alreadyInSetup = config.getString("error.already-in-setup", "").replace("{PREFIX}", prefix);
        notInParkour = config.getString("error.not-in-parkour", "").replace("{PREFIX}", prefix);

        parkourJoin = config.getString("parkour.join", "").replace("{PREFIX}", prefix);
        parkourLeave = config.getString("parkour.leave", "").replace("{PREFIX}", prefix);
        parkourCompleted = config.getString("parkour.completed", "").replace("{PREFIX}", prefix);
        parkourWaitJoin = config.getString("parkour.wait-join", "").replace("{PREFIX}", prefix);
        timeDeathsLayout = config.getString("parkour.time-deaths-layout", "").replace("{PREFIX}", prefix);

        setupEnterMode = config.getString("setup.enter-mode", "").replace("{PREFIX}", prefix);
        setupSetStartLoc = config.getString("setup.set-start-loc", "").replace("{PREFIX}", prefix);
        setupSetEndLoc = config.getString("setup.set-end-loc", "").replace("{PREFIX}", prefix);
        setupTargetWallSign = config.getString("setup.target-wall-sign", "").replace("{PREFIX}", prefix);
        setupSetPos1 = config.getString("setup.set-pos1", "").replace("{PREFIX}", prefix);
        setupSetPos2 = config.getString("setup.set-pos2", "").replace("{PREFIX}", prefix);
        moreOptionsMenuTitle = config.getString("setup.more-options-menu-title", "").replace("{PREFIX}", prefix);
        setupAddCompleteCommands = config.getString("setup.add-complete-commands", "").replace("{PREFIX}", prefix);
        setupClearCompleteCommands = config.getString("setup.clear-complete-commands", "").replace("{PREFIX}", prefix);
        completeCommandsGuiTitle = config.getString("setup.complete-commands-gui-title", "").replace("{PREFIX}", prefix);
        setupSetCooldown = config.getString("setup.set-cooldown", "").replace("{PREFIX}", prefix);
        setupResetCooldown = config.getString("setup.reset-cooldown", "").replace("{PREFIX}", prefix);
        setCooldownGuiTitle = config.getString("setup.set-cooldown-gui-title", "").replace("{PREFIX}", prefix);
        setupInserValidCooldown = config.getString("setup.insert-valid-cooldown-value", "").replace("{PREFIX}", prefix);
        setupSave = config.getString("setup.save", "").replace("{PREFIX}", prefix);
        setupSetAllParameters = config.getString("setup.set-all-parameters", "").replace("{PREFIX}", prefix);
        setupCancel = config.getString("setup.cancel", "").replace("{PREFIX}", prefix);

        checkpointMenuTitle = config.getString("setup.checkpoint-menu-title", "").replace("{PREFIX}", prefix);
        setNewCheckpoint = config.getString("setup.set-new-checkpoint", "").replace("{PREFIX}", prefix);
        tpToCheckpoint = config.getString("setup.tp-to-checkpoint", "").replace("{PREFIX}", prefix);
        removeCheckpoint = config.getString("setup.remove-checkpoint", "").replace("{PREFIX}", prefix);
        updateCheckpoint = config.getString("setup.update-checkpoint", "").replace("{PREFIX}", prefix);
        resetAllCheckpoints = config.getString("setup.reset-all-checkpoints", "").replace("{PREFIX}", prefix);

        completeSign = new String[]{
                config.getString("complete-wall-sign.one", "").replace("{PREFIX}", prefix),
                config.getString("complete-wall-sign.two", "").replace("{PREFIX}", prefix),
                config.getString("complete-wall-sign.three", "").replace("{PREFIX}", prefix),
                config.getString("complete-wall-sign.four", "").replace("{PREFIX}", prefix)
        };
    }
}
