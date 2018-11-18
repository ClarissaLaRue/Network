package Server.Handlers;

import Server.*;

public interface Handler {
    Respones getResponse(Request message);
}
