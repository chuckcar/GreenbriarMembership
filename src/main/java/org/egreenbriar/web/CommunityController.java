package org.egreenbriar.web;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.egreenbriar.service.StreetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Component
public class CommunityController {

    @Value("${membership.csv.file}")
    String membershipFile = null;

    @Value("${blockcaptain.csv.file}")
    String captainFile = null;

    @Autowired
    private StreetService streetReader = null;
    
    @RequestMapping("/community")
    public String communityHandler(Model model) throws FileNotFoundException, IOException {
        model.addAttribute("streets", streetReader.getStreets());
        return "community";
    }

    /**
     * @return the streetReader
     */
    public StreetService getStreetReader() {
        return streetReader;
    }

    /**
     * @param streetReader the streetReader to set
     */
    public void setStreetReader(StreetService streetReader) {
        this.streetReader = streetReader;
    }
}
