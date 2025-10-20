package jbst.server.iam.postgres.repositories;

import jbst.server.iam.postgres.domain.PostgresDbAnything;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostgresAnythingRepository extends JpaRepository<PostgresDbAnything, String> {

}
