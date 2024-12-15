package com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.ArgumentParser;
import com.vk.dwzkf.tglib.botcore.context.CommandContext;
import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.DefaultContextFiller;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
@Component
public class CommandDataFiller extends DefaultContextFiller {
    public static final String[] EMPTY_ARGS = new String[0];
    private ArgumentParser argumentParser = new ArgumentParser();
    Pattern commandPattern = Pattern.compile("(?<command>/[^@\\s]+)((@)(?<botname>\\S+))?(( )(?<args>.*))?");

    @Override
    public void fill(MessageContext ctx, MessageFillerChain chain) {
        String rawMessage = ctx.getRawData();
        Matcher matcher = commandPattern.matcher(rawMessage);
        if (!matcher.find()) {
            chain.fillNext(ctx);
            return;
        }
        CommandContext commandContext = ctx.getCommandContext();
        int commandEndIdx = matcher.group("command").length();
        ctx.setCommandEndIdx(commandEndIdx);

        String rawCommand = matcher.group("command");
        String botQualifier = matcher.group("botname");
        String rawArgs = matcher.group("args");
        String command = rawCommand.substring(1);
        boolean hasArgs = rawArgs != null;

        if (botQualifier != null) {
            commandContext.setBotQualifier(botQualifier);
        }

        commandContext.setRawCommand(rawCommand);
        commandContext.setRawArgs(rawArgs);

        commandContext.setCommand(command);
        commandContext.setCommandArgs(hasArgs ? argumentParser.parse(rawArgs) : EMPTY_ARGS);

        ctx.setHasCommand(true);
        commandContext.setHasArgs(hasArgs);
        chain.fillNext(ctx);
    }
}
