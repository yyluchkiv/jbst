package jbst.server.iam.base.services;

import jbst.foundation.domain.jwt.JbstJwtUser;

import java.util.List;

public interface UsersService {
    List<JbstJwtUser> findAll();
}
