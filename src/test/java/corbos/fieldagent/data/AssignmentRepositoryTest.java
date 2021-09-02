package corbos.fieldagent.data;

import corbos.fieldagent.entities.Agent;
import corbos.fieldagent.entities.Assignment;
import corbos.fieldagent.entities.Country;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class AssignmentRepositoryTest {

    @Autowired
    private AgentRepository agentRepo;

    @Autowired
    private AssignmentRepository assignmentRepo;

    @Autowired
    private CountryRepository countryRepo;

    public AssignmentRepositoryTest() {
    }

    @Test
    public void testFindAll() {
        List<Assignment> all = assignmentRepo.findAll();
        assertEquals(3, all.size());
        for (Assignment a : all) {
            System.out.println(a);
        }
    }

    @Test
    public void testFindByAgentIdentifier() {
        assertEquals(3, assignmentRepo.findByAgentIdentifier("SR-475-4PRAL").size());
    }

    @Test
    public void testCreate() {

        Agent agent = agentRepo.findAll().get(0);
        Country country = countryRepo.findAll().get(0);

        Assignment assignment = new Assignment();
        assignment.setStartDate(LocalDate.of(2020, 1, 1));
        assignment.setProjectedEndDate(LocalDate.of(2020, 3, 15));
        assignment.setNotes("test notes");
        assignment.setAgent(agent);
        assignment.setCountry(country);

        Assignment actual = assignmentRepo.save(assignment);
        assertTrue(actual.getAssignmentId() > 0);

        actual = assignmentRepo.findById(actual.getAssignmentId())
                .orElse(null);

        assertNotNull(actual);

        System.out.println(actual);

        assignmentRepo.deleteById(actual.getAssignmentId());
    }

}
