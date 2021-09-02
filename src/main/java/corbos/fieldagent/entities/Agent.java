package corbos.fieldagent.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class Agent {

    @Id
    @NotBlank(message = "Identifier is required and must be unique.")
    @Size(max = 25, message = "Identifier must be less than 25 characters.")
    private String identifier;
    
    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is required.")
    @Size(max = 25, message = "First name must be less than 25 characters.")
    private String firstName;
    
    @Column(name = "middle_name")
    @Size(max = 25, message = "Middle name must be less than 25 characters.")
    private String middleName;
    
    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is required.")
    @Size(max = 25, message = "Last name must be less than 25 characters.")
    private String lastName;
    
    @Column(name = "picture_url")
    @Size(max = 255, message = "Picture url must be less than 255 characters.")
    private String pictureUrl;
    
    @Column(name = "birth_date", nullable = false)
    @NotNull(message = "Birth date must be between 01/01/1900 and ten years from current date.")
    private LocalDate birthDate;
    
    @Column(nullable = false)
    @NotNull(message = "Height must be between 36 and 96 inches.")
    @Min(value = 36, message = "Height must be between 36 and 96 inches.")
    @Max(value = 96, message = "Height must be between 36 and 96 inches.")
    private int height;
    
    @Column(name = "activation_date", nullable = false)
    @NotNull(message = "Activation date is required.")
    private LocalDate activationDate;
    
    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @ManyToOne
    @JoinColumn(name = "security_clearance_id")
    private SecurityClearance securityClearance;
    
}
