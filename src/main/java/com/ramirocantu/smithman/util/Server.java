package com.ramirocantu.smithman.util;

import com.ramirocantu.smithman.Smithman;
import com.ramirocantu.smithman.Config;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.Logger;
import java.io.IOException;

/**
 * Static utility class for server functions
 */
public class Server
{
    // Safe to declare here. This class should only ever be loaded if any of its methods
    // are called, which by then these should be available.
    static final MinecraftServer SERVER = Smithman.server;
    static final Logger          LOGGER = Smithman.LOGGER;

    /** Kicks all players from the server with given reason, then shuts server down */
    public static void shutdown(String reason) throws IOException {
        ITextComponent message = new TextComponentString(reason);
        reason = message.getFormattedText();

        /** checks for cloudEnabled and redirects to a http post */
        if (Config.cloudEnabled)
        {
            httpShutdown(reason);
            return;
        }

        //for ( Object value : SERVER.getPlayerList().getPlayerList().toArray() )
        for ( Object value : SERVER.getPlayerList().getPlayers().toArray() )
        {
            EntityPlayerMP player = (EntityPlayerMP) value;
            player.connection.disconnect(message);
            //player.connection.kickPlayerFromServer(reason);
        }

        LOGGER.debug("Shutdown initiated because: %s", reason);
        SERVER.initiateShutdown();


    }

    private static void httpShutdown(String reason) throws IOException {
        ITextComponent message = new TextComponentString(reason);
        reason = message.getFormattedText();


        //for ( Object value : SERVER.getPlayerList().getPlayerList().toArray() )
        for ( Object value : SERVER.getPlayerList().getPlayers().toArray() )
        {
            EntityPlayerMP player = (EntityPlayerMP) value;
            player.connection.disconnect(message);
            //player.connection.kickPlayerFromServer(reason);
        }

        LOGGER.debug("VM Shutdown initiated because: %s", reason);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(Config.cloudFunctionTrigger);
        String json = "{\"name\":\"Smithman\"}";
        request.setEntity(new StringEntity(json));
        LOGGER.debug("Sent HTTP Request to GCP");
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() == 200) httpClient.close();
    }
    /** Checks if any non-fake player is present on the server */
    public static boolean hasRealPlayers()
    {
        for ( Object value : SERVER.getPlayerList().getPlayers().toArray() )
            if (value instanceof EntityPlayerMP)
            if ( !(value instanceof FakePlayer) )
                return true;
        return false;
    }
}
