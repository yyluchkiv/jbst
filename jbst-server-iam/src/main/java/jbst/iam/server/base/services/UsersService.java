package jbst.iam.server.base.services;

import jbst.iam.domain.jwt.JwtUser;

import java.util.List;

public interface UsersService {
    List<JwtUser> findAll();
}
