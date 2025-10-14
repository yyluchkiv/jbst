package jbst.server.iam.base.services;

import jbst.foundation.domain.jwt.JwtUser;

import java.util.List;

public interface UsersService {
    List<JwtUser> findAll();
}
