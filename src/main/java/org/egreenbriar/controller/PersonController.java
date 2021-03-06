package org.egreenbriar.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.egreenbriar.form.FormEmailSearch;
import org.egreenbriar.form.FormHouseNumberSearch;
import org.egreenbriar.form.FormNewPerson;
import org.egreenbriar.form.FormPerson;
import org.egreenbriar.model.House;
import org.egreenbriar.model.Person;
import org.egreenbriar.service.BreadcrumbService;
import org.egreenbriar.service.ChangeService;
import org.egreenbriar.service.HouseService;
import org.egreenbriar.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Component
public class PersonController {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private BreadcrumbService breadcrumbService = null;

    @Autowired
    private PeopleService peopleService = null;

    @Autowired
    private HouseService houseService = null;

    @Autowired
    private ChangeService changeService = null;

    @RequestMapping(value = "/emailsearch", method = RequestMethod.POST)
    public String emailsearch(@ModelAttribute FormEmailSearch form, Model model) {
        
        List<Person> people = peopleService.getPeopleWithEmail(form.getEmail());

        model.addAttribute("people", people);
        model.addAttribute("email", form.getEmail());
        
        breadcrumbService.clear();
        breadcrumbService.put("Home", "/home");
        breadcrumbService.put("Logout", "/j_spring_security_logout");
        model.addAttribute("breadcrumbs", breadcrumbService.getBreadcrumbs());
        
        return "email_search_results";
    }

    @RequestMapping("/people")
    public String people(Model model) {

        final Map<String, Person> sortedPeople = new TreeMap<>();

        for (String personId : peopleService.getPeople()) {
            Person person = peopleService.getPerson(personId);
            String name = person.getLast() + person.getFirst();
            if (false == name.trim().isEmpty()) {
                String key = person.getLast() + person.getFirst() + person.getDistrictName() + person.getBlockName();
                sortedPeople.put(key, person);
            }
        }
        model.addAttribute("people", sortedPeople);

        breadcrumbService.clear();
        breadcrumbService.put("Home", "/home");
        breadcrumbService.put("Logout", "/j_spring_security_logout");
        model.addAttribute("breadcrumbs", breadcrumbService.getBreadcrumbs());

        return "people";
    }

    @RequestMapping("/person/emails")
    public String emails(Model model) {
        Set<String> emails = new TreeSet<>();

        model.addAttribute("peopleService", peopleService);

        for (String personId : peopleService.getPeople()) {
            Person person = peopleService.getPerson(personId);
            if (person.getEmail() != null && !person.getEmail().isEmpty() && person.getEmail().contains("@")) {
                emails.add(person.getEmail());
            }
        }
        model.addAttribute("emails", emails);

        breadcrumbService.clear();
        breadcrumbService.put("Home", "/home");
        breadcrumbService.put("Logout", "/j_spring_security_logout");
        model.addAttribute("breadcrumbs", breadcrumbService.getBreadcrumbs());

        return "emails";
    }

    @RequestMapping("/person/bad_emails")
    public String bad_emails(Model model) {
        model.addAttribute("peopleService", peopleService);

        breadcrumbService.clear();
        breadcrumbService.put("Home", "/home");
        breadcrumbService.put("Logout", "/j_spring_security_logout");
        model.addAttribute("breadcrumbs", breadcrumbService.getBreadcrumbs());

        return "badEmails";
    }

    @RequestMapping("/person/editform/{personId}")
    public String editPerson(Model model, @PathVariable String personId) {
        Person person = peopleService.getPerson(personId);
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));

        House house = houseService.getHouse(person.getHouseNumber(), person.getStreetName());

        model.addAttribute("blockName", person.getBlockName());
        model.addAttribute("districtName", person.getDistrictName());
        model.addAttribute("houseId", house.getId());
        model.addAttribute("peopleService", peopleService);
        model.addAttribute("person", person);

        breadcrumbService.clear();
        breadcrumbService.put("Home", "/home");
        breadcrumbService.put("Logout", "/j_spring_security_logout");
        model.addAttribute("breadcrumbs", breadcrumbService.getBreadcrumbs());

        return "editpersonform";
    }

    // name=last, value=<new_value>
    @RequestMapping(value = "/person/update", method = RequestMethod.POST)
    public String updatePerson(@ModelAttribute FormNewPerson form, Model model) throws FileNotFoundException, IOException {

        Person person = peopleService.getPerson(form.getPersonId());
        person.setLast(form.getLastName().replaceAll(",", "/"));
        person.setFirst(form.getFirstName().replaceAll(",", "/"));
        person.setPhone(form.getPhone());
        person.setEmail(form.getEmail());
        person.setComment(form.getComments().replaceAll(",", ";"));
        person.setUnlisted(form.getUnlisted().equals("1"));
        person.setNoDirectory(form.getNodirectory().equals("1"));
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));

        String personFormat = "house(%s) first(%s) last(%s) phone(%s) email(%s) comments(%s)";
        String message = String.format(personFormat, form.getHouseId(), person.getFirst(), person.getLast(), person.getPhone(), person.getEmail(), person.getComment());
        changeService.logChange("newperson", message);

        peopleService.updatePerson(person);
        peopleService.write();

        String blockName = form.getBlockName();
        model.addAttribute("blockName", blockName);

        return "redirect:/block";
    }

    @RequestMapping("/person/delete/{personId}")
    public String deletePerson(Model model, @PathVariable String personId) throws FileNotFoundException, IOException {
        Person person = peopleService.getPerson(personId);

        StringBuilder buffer = new StringBuilder();
        buffer.append(String.format("person(%s)", personId));
        buffer.append(String.format(" district(%s)", person.getDistrictName()));
        buffer.append(String.format(" block(%s)", person.getBlockName()));
        buffer.append(String.format(" houseNumber(%s)", person.getHouseNumber()));
        buffer.append(String.format(" streetName(%s)", person.getStreetName()));
        buffer.append(String.format(" first(%s)", person.getFirst()));
        buffer.append(String.format(" last(%s)", person.getLast()));
        buffer.append(String.format(" phone(%s)", person.getPhone()));
        buffer.append(String.format(" email(%s)", person.getEmail()));
        buffer.append(String.format(" comment(%s)", person.getComment()));
        buffer.append(String.format(" unlisted(%s)", person.isUnlisted()));
        buffer.append(String.format(" comment(%s)", person.getComment()));
        buffer.append(String.format(" deletedby(%s)", person.getUpdatedBy()));
        buffer.append(String.format(" deletedat(%s)", sdf.format(new Date())));

        changeService.logChange("deletePerson", buffer.toString());
        peopleService.deletePerson(person);
        peopleService.write();
        model.addAttribute("blockName", person.getBlockName());

        return "redirect:/block";
    }

    @RequestMapping("/person/toggle_listed/{personId}")
    @ResponseBody
    public String toggleListed(Model model, @PathVariable String personId) throws FileNotFoundException, IOException {
        Person person = peopleService.getPerson(personId);
        String message = String.format("person(%s) current(%s)", personId, person.isListed());
        changeService.logChange("toggle_listed", message);
        person.toggleListed();
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));
        peopleService.write();
        return person.listed();
    }

    @RequestMapping("/person/toggle_directory/{personId}")
    @ResponseBody
    public String toggleDirectory(Model model, @PathVariable String personId) throws FileNotFoundException, IOException {
        Person person = peopleService.getPerson(personId);
        String message = String.format("person(%s) current(%s)", personId, person.isNoDirectory());
        changeService.logChange("toggle_directory", message);
        person.toggleDirectory();
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));
        peopleService.write();
        return person.directory();
    }

    // name=last, value=<new_value>
    @RequestMapping(value = "/person/update_last", method = RequestMethod.POST)
    @ResponseBody
    public String updateLast(@ModelAttribute FormPerson formPerson, Model model) throws FileNotFoundException, IOException {
        String personId = formPerson.getPk();
        Person person = peopleService.getPerson(personId);
        String lastName = formPerson.getValue().replaceAll(",", "&");
        String message = String.format("person(%s) old(%s) new(%s)", personId, person.getLast(), lastName);
        changeService.logChange("update_last", message);
        person.setLast(lastName);
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));
        peopleService.write();
        return person.getLast();
    }

    // name=last, value=<new_value>
    @RequestMapping(value = "/person/update_first", method = RequestMethod.POST)
    @ResponseBody
    public String updateFirst(@ModelAttribute FormPerson formPerson, Model model) throws FileNotFoundException, IOException {
        String personId = formPerson.getPk();
        Person person = peopleService.getPerson(personId);
        String firstName = formPerson.getValue().replaceAll(",", "&");
        String message = String.format("person(%s) old(%s) new(%s)", personId, person.getFirst(), firstName);
        changeService.logChange("update_first", message);
        person.setFirst(firstName);
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));
        peopleService.write();
        return person.getFirst();
    }

    // name=last, value=<new_value>
    @RequestMapping(value = "/person/update_phone", method = RequestMethod.POST)
    @ResponseBody
    public String updatePhone(@ModelAttribute FormPerson formPerson, Model model) throws FileNotFoundException, IOException {
        String personId = formPerson.getPk();
        Person person = peopleService.getPerson(personId);
        String message = String.format("person(%s) old(%s) new(%s)", personId, person.getPhone(), formPerson.getValue());
        changeService.logChange("update_phone", message);
        person.setPhone(formPerson.getValue());
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));
        peopleService.write();
        return person.getPhone();
    }

    // name=last, value=<new_value>
    @RequestMapping(value = "/person/update_email", method = RequestMethod.POST)
    @ResponseBody
    public String updateEmail(@ModelAttribute FormPerson formPerson, Model model) throws FileNotFoundException, IOException {
        String personId = formPerson.getPk();
        Person person = peopleService.getPerson(personId);
        String message = String.format("person(%s) old(%s) new(%s)", personId, person.getEmail(), formPerson.getValue());
        changeService.logChange("update_email", message);
        person.setEmail(formPerson.getValue());
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));
        peopleService.write();
        return person.getEmail();
    }

    // name=last, value=<new_value>
    @RequestMapping(value = "/person/update_comment", method = RequestMethod.POST)
    @ResponseBody
    public String updateComment(@ModelAttribute FormPerson formPerson, Model model) throws FileNotFoundException, IOException {
        String personId = formPerson.getPk();
        Person person = peopleService.getPerson(personId);
        String comment = formPerson.getValue().replaceAll(",", ";");
        String message = String.format("person(%s) old(%s) new(%s)", personId, person.getComment(), comment);
        changeService.logChange("update_comment", message);
        person.setComment(comment);
        person.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        person.setDateUpdated(sdf.format(new Date()));
        peopleService.write();
        return person.getComment();
    }

    public void setChangeService(ChangeService changeService) {
        this.changeService = changeService;
    }

    public void setPeopleService(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    public void setBreadcrumbService(BreadcrumbService breadcrumbService) {
        this.breadcrumbService = breadcrumbService;
    }

    public HouseService getHouseService() {
        return houseService;
    }

    public void setHouseService(HouseService houseService) {
        this.houseService = houseService;
    }

}
