package corbos.fieldagent.data;

import corbos.fieldagent.entities.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRepository
        extends JpaRepository<Agency, Integer> {

}
