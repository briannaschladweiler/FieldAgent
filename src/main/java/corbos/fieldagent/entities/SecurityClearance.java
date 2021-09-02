package corbos.fieldagent.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class SecurityClearance {

    @Id
    private int securityClearanceId;
    private String name;

}
