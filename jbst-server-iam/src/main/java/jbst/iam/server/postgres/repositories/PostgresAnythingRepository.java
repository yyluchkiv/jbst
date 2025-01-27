package jbst.iam.server.postgres.repositories;

import jbst.iam.server.postgres.domain.db.PostgresDbAnything;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostgresAnythingRepository extends JpaRepository<PostgresDbAnything, String> {

}
