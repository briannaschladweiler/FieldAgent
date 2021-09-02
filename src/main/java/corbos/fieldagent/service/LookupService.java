package corbos.fieldagent.service;

import corbos.fieldagent.data.AgencyRepository;
import corbos.fieldagent.data.AgentRepository;
import corbos.fieldagent.data.AssignmentRepository;
import corbos.fieldagent.data.CountryRepository;
import corbos.fieldagent.data.SecurityClearanceRepository;
import corbos.fieldagent.entities.Agency;
import corbos.fieldagent.entities.Agent;
import corbos.fieldagent.entities.Assignment;
import corbos.fieldagent.entities.Country;
import corbos.fieldagent.entities.SecurityClearance;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LookupService {

    private final AgencyRepository agencyRepo;
    private final CountryRepository countryRepo;
    private final SecurityClearanceRepository securityRepo;
    private final AgentRepository agentRepo;
    private final AssignmentRepository assignmentRepo;

    public LookupService(AgencyRepository agencyRepo,
            CountryRepository countryRepo,
            SecurityClearanceRepository securityRepo,
            AgentRepository agentRepo,
            AssignmentRepository assignmentRepo) {
        this.agencyRepo = agencyRepo;
        this.countryRepo = countryRepo;
        this.securityRepo = securityRepo;
        this.agentRepo = agentRepo;
        this.assignmentRepo = assignmentRepo;
    }

    //Agency Methods:
    public List<Agency> findAllAgencies() {
        return agencyRepo.findAll();
    }

    public Agency findAgencyById(int agencyId) {
        return agencyRepo.findById(agencyId)
                .orElse(null);
    }

    //Country Methods:
    public List<Country> findAllCountries() {
        return countryRepo.findAll();
    }

    public Country findCountryByCode(String countryCode) {
        return countryRepo.findById(countryCode)
                .orElse(null);
    }

    //Security Methods:
    public List<SecurityClearance> findAllSecurityClearances() {
        return securityRepo.findAll();
    }

    public SecurityClearance findSecurityClearanceById(int securityClearanceId) {
        return securityRepo.findById(securityClearanceId)
                .orElse(null);
    }

    //Agent Methods:
    public List<Agent> findAllAgents() {
        return agentRepo.findAll();
    }

    public Agent findAgentById(String agentId) {
        return agentRepo.findById(agentId).orElse(null);
    }

    public Agent addUpdateAgent(Agent a) {
        return agentRepo.save(a);
    }

    public void deleteAgentById(String agentId) {
        agentRepo.deleteById(agentId);
    }
    
    //Assignment Methods:
    public List<Assignment> findAllAssignments() {
        return assignmentRepo.findAll();
    }

    public List<Assignment> findAssignmentByAgentIdentifier(String agentIdentifier) {
        return assignmentRepo.findByAgentIdentifier(agentIdentifier);
    }

    public Assignment findAssignmentById(int id) {
        return assignmentRepo.findById(id).orElse(null);
    }

    public Assignment addUpdateAssignement(Assignment a) {
        return assignmentRepo.save(a);
    }

    public void deleteAssignment(Assignment a) {
        assignmentRepo.delete(a);
    }
}
