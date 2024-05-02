package com.projeto.interdisciplinar.services;

import com.sun.security.auth.UserPrincipal;
import java.security.Principal;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

  @Override
  protected Principal determineUser(
      ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
    var id = request.getURI().toString().split("\\?")[1].split("\\=")[1];

    if (id == null)
      try {
        throw new BadRequestException("ID Invalido!");
      } catch (BadRequestException e) {
        e.printStackTrace();
      }

    var user = new UserPrincipal(id);
    return user;
  }

}