package corbos.fieldagent.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Agency {

    @Id
    private int agencyId;
    private String name;

}
