package routing;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

public class UsernamePasswordCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                String username = "_system";
                NameCallback nc = (NameCallback) callback;
                nc.setName(username);
            } else if (callback instanceof WSPasswordCallback) {
                String password = "SSP2019!?";
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                pc.setPassword(password);
            } else {
                throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
            }
        }
    }
}
