package com.vk.dwzkf.tg_notifications.bot_core.context.filler_chain.fillers;

import com.vk.dwzkf.tglib.botcore.context.MessageContext;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.MessageFillerChain;
import com.vk.dwzkf.tglib.botcore.context.filler_chain.fillers.CommandDataFiller;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

/**
 * @author Roman Shageev
 * @since 11.10.2023
 */
public class CommandDataFillerTest {
    MessageFillerChain messageFillerChain = new MessageFillerChain();
    {
        messageFillerChain.setFillers(new LinkedList<>());
    }

    @Test
    public void testNoArgsCommandResolved() {
        CommandDataFiller commandDataFiller = new CommandDataFiller();
        MessageContext messageContext = new MessageContext();
        messageContext.setRawData("/command");
        commandDataFiller.fill(messageContext, messageFillerChain);
        Assert.assertEquals( "command", messageContext.getCommand());
        Assert.assertEquals("/command", messageContext.getRawCommand());
        Assert.assertNull(messageContext.getRawArgs());
        Assert.assertArrayEquals(new String[0], messageContext.getCommandArgs());
    }

    @Test
    public void testEmptyArgCommandResolved() {
        CommandDataFiller commandDataFiller = new CommandDataFiller();
        MessageContext messageContext = new MessageContext();
        messageContext.setRawData("/command ");
        commandDataFiller.fill(messageContext, messageFillerChain);
        Assert.assertEquals("", messageContext.getRawArgs());
        Assert.assertArrayEquals(new String[]{""}, messageContext.getCommandArgs());
    }

    @Test
    public void testArgumentResolved() {
        CommandDataFiller commandDataFiller = new CommandDataFiller();
        MessageContext messageContext = new MessageContext();
        messageContext.setRawData("/command z  c");
        commandDataFiller.fill(messageContext, messageFillerChain);
        Assert.assertEquals("z  c", messageContext.getRawArgs());
        Assert.assertArrayEquals(new String[]{"z", "", "c"}, messageContext.getCommandArgs());
    }

    @Test
    public void testNoCommand() {
        CommandDataFiller commandDataFiller = new CommandDataFiller();
        MessageContext messageContext = new MessageContext();
        messageContext.setRawData("c/ommand");
        commandDataFiller.fill(messageContext, messageFillerChain);
        Assert.assertFalse(messageContext.hasCommand());
        Assert.assertFalse(messageContext.hasArgs());
        Assert.assertNull(messageContext.getCommand());
        Assert.assertNull(messageContext.getRawCommand());
        Assert.assertNull(messageContext.getRawArgs());
        Assert.assertArrayEquals(null, messageContext.getCommandArgs());
    }
}