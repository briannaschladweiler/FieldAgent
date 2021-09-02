package corbos.fieldagent.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Country {

    @Id
    private String countryCode;
    private String name;

}
