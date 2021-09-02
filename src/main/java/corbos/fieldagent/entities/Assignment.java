package corbos.fieldagent.entities;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import lombok.Data;

@Entity
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int assignmentId;   
    
    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Invalid Start Date. Must be before Projected and Actual End Dates and assignments cannot overlap for a field agent.")
    private LocalDate startDate;
    
    @Column(name = "projected_end_date", nullable = false)
    @NotNull(message = "Invalid Projected End Date. Must be after Start Date and assignments cannot overlap for a field agent.")
    private LocalDate projectedEndDate;
    
    @Column(name = "actual_end_date")
    @Past(message = "Invalid Actual End Date. Must be after Start Date, cannot be a future date, and assignments cannot overlap for a field agent.")
    private LocalDate actualEndDate;
    
    @Column(name = "notes")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "country_code")
    private Country country;

    @ManyToOne
    @JoinColumn(name = "identifier")
    private Agent agent;

}
