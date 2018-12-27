package com.targren.autoshutdown;

import com.targren.autoshutdown.util.Chat;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Alternative of the vanilla CommandException which translates messages server-side
 */
class CommandException extends net.minecraft.command.CommandException
{
    public CommandException(String msg, Object... parts)
    {
        super(new TextComponentTranslation(msg, parts).getFormattedText());
    }
}
