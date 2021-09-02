/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package corbos.fieldagent.controller;

import corbos.fieldagent.entities.Agency;
import corbos.fieldagent.entities.Agent;
import corbos.fieldagent.entities.Assignment;
import corbos.fieldagent.entities.Country;
import corbos.fieldagent.entities.SecurityClearance;
import corbos.fieldagent.service.LookupService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author briannaschladweiler
 */
@Controller
public class MainController {

    @Autowired
    LookupService service;

    Set<ConstraintViolation<Agent>> addAgentViolations = new HashSet<>();
    Set<ConstraintViolation<Agent>> editAgentViolations = new HashSet<>();
    Set<ConstraintViolation<Assignment>> addAssignmentViolations = new HashSet<>();
    Set<ConstraintViolation<Assignment>> editAssignmentViolations = new HashSet<>();

    @GetMapping("/")
    public String displayAgents(Model model) {
        List<Agent> agents = service.findAllAgents();
        model.addAttribute("agents", agents);

        return "index";
    }

    @GetMapping("addAgent")
    public String loadAddAgentPage(Model model) {
        List<Agency> agencies = service.findAllAgencies();
        model.addAttribute("agencies", agencies);

        List<SecurityClearance> clearances = service.findAllSecurityClearances();
        model.addAttribute("clearances", clearances);

        model.addAttribute("errors", addAgentViolations);

        return "addAgent";
    }

    @PostMapping("addNewAgent")
    public String addNewAgent(HttpServletRequest request, Model model) {
        Agent agent = new Agent();

        String firstName = request.getParameter("firstName");
        String middleName = request.getParameter("middleName");
        String lastName = request.getParameter("lastName");
        String pictureUrl = request.getParameter("pictureUrl");
        Boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));
        int agencyId = Integer.parseInt(request.getParameter("agencyId"));
        int clearanceId = Integer.parseInt(request.getParameter("clearanceId"));

        agent.setFirstName(firstName);
        agent.setMiddleName(middleName);
        agent.setLastName(lastName);
        agent.setPictureUrl(pictureUrl);
        agent.setActive(isActive);
        agent.setAgency(service.findAgencyById(agencyId));
        agent.setSecurityClearance(service.findSecurityClearanceById(clearanceId));

        try {
            LocalDate birthDate = LocalDate.parse(request.getParameter("birthDate"), DateTimeFormatter.ISO_DATE);

            LocalDate afterDate = LocalDate.parse("1900-01-01");
            LocalDate beforeDate = LocalDate.now().minusYears(10);

            if (birthDate.isAfter(afterDate) && birthDate.isBefore(beforeDate)) {
                agent.setBirthDate(birthDate);
            } else {
                agent.setBirthDate(null);
            }
        } catch (DateTimeParseException e) {
            agent.setBirthDate(null);
        }

        try {
            String stringHeight = request.getParameter("height");

            if (stringHeight.equals("")) {
                agent.setHeight(0);
            } else {
                int height = Integer.parseInt(stringHeight);
                agent.setHeight(height);
            }
        } catch (NumberFormatException e) {
            agent.setHeight(0);
        }

        try {
            LocalDate activationDate = LocalDate.parse(request.getParameter("activationDate"), DateTimeFormatter.ISO_DATE);

            LocalDate afterDate = LocalDate.parse("0000-01-01");

            if (activationDate.isAfter(afterDate)) {
                agent.setActivationDate(activationDate);
            } else {
                agent.setActivationDate(null);
            }
        } catch (DateTimeParseException e) {
            agent.setActivationDate(null);
        }

        String identifier = request.getParameter("identifier");
        Agent newAgent = service.findAgentById(identifier);

        if (newAgent == null) {
            agent.setIdentifier(identifier);
        } else {
            agent.setIdentifier(null);
        }

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        addAgentViolations = validate.validate(agent);

        boolean hasErrors;
        if (addAgentViolations.isEmpty()) {
            hasErrors = false;
        } else {
            hasErrors = true;
        }

        if (!hasErrors) {
            service.addUpdateAgent(agent);
            return "redirect:/";
        }

        return "redirect:/addAgent";
    }

    @GetMapping("clearAddAgentErrors")
    public String clearAddAgentErrors() {
        addAgentViolations.clear();
        return "redirect:/";
    }

    @GetMapping("editAgent")
    public String agentDetails(String id, Model model) {
        //Load the Agent into the view & edit details page
        Agent agent = service.findAgentById(id);
        model.addAttribute("agent", agent);

        //Get the isActive value from the Agent, assign on the page
        Boolean isActive = agent.isActive();
        String isActiveString;
        if (isActive == true) {
            isActiveString = "1";
            model.addAttribute("isActive", isActive);
        } else {
            isActiveString = "0";
            model.addAttribute("isActive", isActive);
        }
        
        List<Agency> agencies = service.findAllAgencies();
        model.addAttribute("agencies", agencies);

        List<SecurityClearance> clearances = service.findAllSecurityClearances();
        model.addAttribute("clearances", clearances);

        List<Assignment> assignments = service.findAssignmentByAgentIdentifier(id);
        Collections.sort(assignments, new Comparator<Assignment>() {
            public int compare(Assignment a1, Assignment a2) {
                return a1.getStartDate().compareTo(a2.getStartDate());
            }
        });
        model.addAttribute("assignments", assignments);

        model.addAttribute("errors", editAgentViolations);

        return "editAgent";
    }

    @PostMapping("editAgent")
    public String editAgent(HttpServletRequest request, Model model) {
        Agent agent = new Agent();

        String firstName = request.getParameter("firstName");
        String middleName = request.getParameter("middleName");
        String lastName = request.getParameter("lastName");
        String pictureUrl = request.getParameter("pictureUrl");
        String isActiveString = request.getParameter("isActive");
        Boolean isActive;
        int agencyId = Integer.parseInt(request.getParameter("agencyId"));
        int clearanceId = Integer.parseInt(request.getParameter("clearanceId"));

        agent.setFirstName(firstName);
        agent.setMiddleName(middleName);
        agent.setLastName(lastName);
        agent.setPictureUrl(pictureUrl);
        agent.setAgency(service.findAgencyById(agencyId));
        agent.setSecurityClearance(service.findSecurityClearanceById(clearanceId));
        
        if (isActiveString == null) {
            isActive = false;
        } else {
            isActive = true;
        }
        
        agent.setActive(isActive);

        try {
            LocalDate birthDate = LocalDate.parse(request.getParameter("birthDate"), DateTimeFormatter.ISO_DATE);

            LocalDate afterDate = LocalDate.parse("1900-01-01");
            LocalDate beforeDate = LocalDate.now().minusYears(10);

            if (birthDate.isAfter(afterDate) && birthDate.isBefore(beforeDate)) {
                agent.setBirthDate(birthDate);
            } else {
                agent.setBirthDate(null);
            }
        } catch (DateTimeParseException e) {
            agent.setBirthDate(null);
        }

        try {
            String stringHeight = request.getParameter("height");

            if (stringHeight.equals("")) {
                agent.setHeight(0);
            } else {
                int height = Integer.parseInt(stringHeight);
                agent.setHeight(height);
            }
        } catch (NumberFormatException e) {
            agent.setHeight(0);
        }

        try {
            LocalDate activationDate = LocalDate.parse(request.getParameter("activationDate"), DateTimeFormatter.ISO_DATE);

            LocalDate afterDate = LocalDate.parse("0000-01-01");

            if (activationDate.isAfter(afterDate)) {
                agent.setActivationDate(activationDate);
            } else {
                agent.setActivationDate(null);
            }
        } catch (DateTimeParseException e) {
            agent.setActivationDate(null);
        }
        //MUST ADD THINGS TO MAKE SURE IDENTIFIER HAS TO BE UNIQUE
        String newIdentifier = request.getParameter("newIdentifier");
        String oldIdentifier = request.getParameter("oldIdentifier");
        Agent newAgent = service.findAgentById(newIdentifier);

        if (newIdentifier.equals(oldIdentifier)) {
            agent.setIdentifier(newIdentifier);
        } else {

            if (newAgent == null) {
                agent.setIdentifier(newIdentifier);

            } else {
                agent.setIdentifier(null);
            }
        }

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        editAgentViolations = validate.validate(agent);

        boolean hasErrors;

        if (editAgentViolations.isEmpty()) {
            hasErrors = false;
        } else {
            hasErrors = true;
        }

        if (!hasErrors) {
            service.addUpdateAgent(agent);
            //return "redirect:/";
        }
        if (hasErrors) {
            return "redirect:/editAgent?id=" + oldIdentifier;
        }

        if (newAgent == null) {
            //IF WE ARE CHANGING IDENTIFIER, WE NEED TO TRANSFER ALL ASSIGNMENTS TO NEW ID
            List<Assignment> aList = service.findAssignmentByAgentIdentifier(oldIdentifier);
            System.out.println(aList.size());
            for (Assignment a : aList) {
                a.setAgent(agent);
                service.addUpdateAssignement(a);
            }
            //AND WE NEED TO DELETE OLD ID
            service.deleteAgentById(oldIdentifier);
        }

        //if hasErrors is false:
        if (!hasErrors) {
            //service.addUpdateAgent(agent);
            return "redirect:/";
        }

        //if hasErrors is true:
        return "redirect:/editAgent?id=" + newIdentifier;
    }

    @GetMapping("clearEditAgentErrors")
    public String clearEditAgentErrors() {
        editAgentViolations.clear();
        return "redirect:/";
    }

    @GetMapping("deleteAgentPage")
    public String deleteAgentPage(String id, Model model
    ) {
        Agent agent = service.findAgentById(id);
        model.addAttribute("agent", agent);

        List<Assignment> assignments = service.findAssignmentByAgentIdentifier(id);
        int num = assignments.size();
        model.addAttribute("num", num);

        return "deleteAgent";
    }

    @GetMapping("deleteAgentInfo")
    public String deleteAgentInfo(String id
    ) {
        List<Assignment> assignments = service.findAssignmentByAgentIdentifier(id);

        for (Assignment a : assignments) {
            service.deleteAssignment(a);
        }

        service.deleteAgentById(id);
        return "redirect:/";
    }

    @GetMapping("addAssignment")
    public String loadAddAssignmentPage(String id, Model model
    ) {
        List<Agent> agents = service.findAllAgents();
        model.addAttribute("agents", agents);

        Agent agent = service.findAgentById(id);
        model.addAttribute("foundAgent", agent);

        List<Country> countries = service.findAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("errors", addAssignmentViolations);

        return "addAssignment";
    }

    @PostMapping("addAssignment")
    public String addNewAssignment(HttpServletRequest request
    ) {
        Assignment assignment = new Assignment();

        String agentId = request.getParameter("agentId");
        String countryId = request.getParameter("countryId");

        assignment.setAgent(service.findAgentById(agentId));
        assignment.setCountry(service.findCountryByCode(countryId));

        try {
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"), DateTimeFormatter.ISO_DATE);

            assignment.setStartDate(startDate);

        } catch (DateTimeParseException e) {
            assignment.setStartDate(null);
        }

        try {
            LocalDate projectedEndDate = LocalDate.parse(request.getParameter("projectedEndDate"), DateTimeFormatter.ISO_DATE);

            LocalDate startDate = assignment.getStartDate();

            if (projectedEndDate.isAfter(startDate)) {
                assignment.setProjectedEndDate(projectedEndDate);
            } else {
                assignment.setProjectedEndDate(null);
            }

        } catch (DateTimeParseException e) {
            assignment.setProjectedEndDate(null);
        }

                try {
            LocalDate actualEndDate = LocalDate.parse(request.getParameter("actualEndDate"), DateTimeFormatter.ISO_DATE);

            LocalDate startDate = assignment.getStartDate();

            if (actualEndDate == null) {
                assignment.setActualEndDate(actualEndDate);
            } else if (actualEndDate.isAfter(startDate)) {
                List<Assignment> aList = service.findAssignmentByAgentIdentifier(agentId);
                if (aList.size() > 0) {
                    for (Assignment a : aList) {
                        LocalDate start = a.getStartDate();
                        LocalDate projectedEnd = a.getProjectedEndDate();
                        if (actualEndDate.isAfter(start) && actualEndDate.isBefore(projectedEnd)) {
                            assignment.setActualEndDate(LocalDate.parse("9999-09-09"));
                            break;
                        } else {
                            assignment.setActualEndDate(actualEndDate);
                        }
                    }
                }
            } else {
                assignment.setActualEndDate(LocalDate.parse("9999-09-09"));
            }

        } catch (DateTimeParseException e) {
            assignment.setActualEndDate(null);
        }

        assignment.setNotes(request.getParameter("notes"));

        //Check if start/end dates fall inside an already existing assignment
        //Make sure assignment dates don't overlap
        LocalDate startDate = assignment.getStartDate();
        LocalDate projectedEndDate = assignment.getProjectedEndDate();
        LocalDate actualEndDate = assignment.getActualEndDate();

        List<Assignment> aList = service.findAssignmentByAgentIdentifier(agentId);
        if (aList.size() > 0 && startDate != null && projectedEndDate != null) {
            for (Assignment a : aList) {
                LocalDate start = a.getStartDate();
                LocalDate end = a.getProjectedEndDate();
                if (startDate.isAfter(start) && startDate.isBefore(end)) {
                    startDate = null;
                    break;
                }
                if (projectedEndDate.isAfter(start) && projectedEndDate.isBefore(end)) {
                    projectedEndDate = null;
                    break;
                }
                if (startDate.isBefore(start) && projectedEndDate.isAfter(end)) {
                    startDate = null;
                    projectedEndDate = null;
                    break;
                }
                if (startDate.isAfter(start) && projectedEndDate.isBefore(end)) {
                    startDate = null;
                    projectedEndDate = null;
                    break;
                }
            }
        }

        assignment.setStartDate(startDate);
        assignment.setProjectedEndDate(projectedEndDate);
        assignment.setActualEndDate(actualEndDate);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        addAssignmentViolations = validate.validate(assignment);

        if (addAssignmentViolations.isEmpty()) {
            service.addUpdateAssignement(assignment);
            return "redirect:/editAgent?id=" + agentId;
        }

        return "redirect:/addAssignment?id=" + agentId;
    }

    @GetMapping("clearAddAssignmentErrors")
    public String clearAddAssignmentErrors() {
        addAssignmentViolations.clear();
        return "redirect:/";
    }

    @GetMapping("editAssignment")
    public String assignmentDetails(int id, Model model
    ) {
        Assignment a = service.findAssignmentById(id);
        model.addAttribute("assignment", a);

        Agent agentAssigned = a.getAgent();
        model.addAttribute("foundAgent", agentAssigned);

        List<Agent> agents = service.findAllAgents();
        model.addAttribute("agents", agents);

        List<Country> countries = service.findAllCountries();
        model.addAttribute("countries", countries);

        model.addAttribute("errors", editAssignmentViolations);

        return "editAssignment";
    }

    @PostMapping("editAssignment")
    public String editAssignment(HttpServletRequest request
    ) {
        Assignment assignment = new Assignment();
        String assignmentId = request.getParameter("assignmentId");
        assignment.setAssignmentId(Integer.parseInt(assignmentId));

        String agentId = request.getParameter("agentId");
        String countryId = request.getParameter("countryId");

        assignment.setAgent(service.findAgentById(agentId));
        assignment.setCountry(service.findCountryByCode(countryId));

        try {
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"), DateTimeFormatter.ISO_DATE);

            assignment.setStartDate(startDate);

        } catch (DateTimeParseException e) {
            assignment.setStartDate(null);
        }

        try {
            LocalDate projectedEndDate = LocalDate.parse(request.getParameter("projectedEndDate"), DateTimeFormatter.ISO_DATE);

            LocalDate startDate = assignment.getStartDate();

            if (projectedEndDate.isAfter(startDate)) {
                assignment.setProjectedEndDate(projectedEndDate);
            } else {
                assignment.setProjectedEndDate(null);
            }

        } catch (DateTimeParseException e) {
            assignment.setProjectedEndDate(null);
        }

        try {
            LocalDate actualEndDate = LocalDate.parse(request.getParameter("actualEndDate"), DateTimeFormatter.ISO_DATE);

            LocalDate startDate = assignment.getStartDate();

            if (actualEndDate == null) {
                assignment.setActualEndDate(actualEndDate);
            } else if (actualEndDate.isAfter(startDate)) {
                List<Assignment> aList = service.findAssignmentByAgentIdentifier(agentId);
                if (aList.size() > 0) {
                    for (Assignment a : aList) {
                        LocalDate start = a.getStartDate();
                        LocalDate projectedEnd = a.getProjectedEndDate();
                        if (actualEndDate.isAfter(start) && actualEndDate.isBefore(projectedEnd)) {
                            assignment.setActualEndDate(LocalDate.parse("9999-09-09"));
                            break;
                        } else {
                            assignment.setActualEndDate(actualEndDate);
                        }
                    }
                }
            } else {
                assignment.setActualEndDate(LocalDate.parse("9999-09-09"));
            }

        } catch (DateTimeParseException e) {
            assignment.setActualEndDate(null);
        }

        assignment.setNotes(request.getParameter("notes"));

        //Check if start/end dates fall inside an already existing assignment
        //Make sure assignment dates don't overlap
        LocalDate startDate = assignment.getStartDate();
        LocalDate projectedEndDate = assignment.getProjectedEndDate();
        LocalDate actualEndDate = assignment.getActualEndDate();

        List<Assignment> aList = service.findAssignmentByAgentIdentifier(agentId);
        if (aList.size() > 0 && startDate != null && projectedEndDate != null) {
            for (Assignment a : aList) {
                LocalDate start = a.getStartDate();
                LocalDate end = a.getProjectedEndDate();
                if (startDate.isAfter(start) && startDate.isBefore(end)) {
                    startDate = null;
                    break;
                }
                if (projectedEndDate.isAfter(start) && projectedEndDate.isBefore(end)) {
                    projectedEndDate = null;
                    break;
                }
                if (startDate.isBefore(start) && projectedEndDate.isAfter(end)) {
                    startDate = null;
                    projectedEndDate = null;
                    break;
                }
                if (startDate.isAfter(start) && projectedEndDate.isBefore(end)) {
                    startDate = null;
                    projectedEndDate = null;
                    break;
                }
            }
        }

        assignment.setStartDate(startDate);
        assignment.setProjectedEndDate(projectedEndDate);
        assignment.setActualEndDate(actualEndDate);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        editAssignmentViolations = validate.validate(assignment);

        //Go back to Home only if there were no errors with the form
        if (editAssignmentViolations.isEmpty()) {
            service.addUpdateAssignement(assignment);
            return "redirect:/editAgent?id=" + agentId;
        }

        return "redirect:/editAssignment?id=" + assignmentId;
    }

    @GetMapping("clearEditAssignmentErrors")
    public String clearEditAssignmentErrors(int id
    ) {
        editAssignmentViolations.clear();

        Assignment a = service.findAssignmentById(id);
        Agent agent = a.getAgent();
        String agentId = agent.getIdentifier();
        return "redirect:/editAgent?id=" + agentId;
    }

    @GetMapping("deleteAssignmentPage")
    public String deleteAssignmentPage(int id, Model model
    ) {
        Assignment a = service.findAssignmentById(id);
        model.addAttribute("assignment", a);

        Agent agentAssigned = a.getAgent();
        model.addAttribute("agent", agentAssigned);

        return "deleteAssignment";
    }

    @GetMapping("deleteAssignmentInfo")
    public String deleteAssignmentInfo(int id, Model model
    ) {
        Assignment a = service.findAssignmentById(id);
        Agent agent = a.getAgent();
        String agentId = agent.getIdentifier();

        service.deleteAssignment(a);
        return "redirect:/editAgent?id=" + agentId;
    }

}
