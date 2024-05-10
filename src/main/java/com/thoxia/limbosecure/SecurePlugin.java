package com.thoxia.limbosecure;

import com.thoxia.limbosecure.backend.RequestHandler;
import com.thoxia.limbosecure.logging.Logger;
import com.thoxia.limbosecure.twofa.TwoFAManager;

public interface SecurePlugin {

    String PERMISSIONS_UPDATED_CHANNEL_NAME = "limbosecure:updatepermission";

    int VELOCITY_METRIC_ID = 21854;
    int BUNGEE_METRIC_ID = 21855;

    void setupServer();

    void connectPlayer(Object obj);

    void markVerified(String playerName);

    TwoFAManager getTwoFAManager();

    RequestHandler getRequestHandler();

    Logger getSecureLogger();

}
