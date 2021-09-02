package corbos.fieldagent.data;

import corbos.fieldagent.entities.SecurityClearance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityClearanceRepository
        extends JpaRepository<SecurityClearance, Integer> {

}
