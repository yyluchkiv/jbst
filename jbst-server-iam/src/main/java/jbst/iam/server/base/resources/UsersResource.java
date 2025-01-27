package jbst.iam.server.base.resources;

import io.swagger.v3.oas.annotations.tags.Tag;
import jbst.iam.domain.jwt.JwtUser;
import jbst.iam.server.base.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Swagger
@Tag(name = "[jbst] Users API")
// Spring
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UsersResource {

    // Services
    private final UsersService usersService;

    @GetMapping("/server")
    public List<JwtUser> findAll() {
        return this.usersService.findAll();
    }
}
