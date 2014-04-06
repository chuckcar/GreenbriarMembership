package org.egreenbriar.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.egreenbriar.form.FormDistrict;
import org.egreenbriar.service.BlockCaptainService;
import org.egreenbriar.service.BlockService;
import org.egreenbriar.service.BreadcrumbService;
import org.egreenbriar.service.ChangeService;
import org.egreenbriar.service.HouseService;
import org.egreenbriar.service.OfficierService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DistrictController {

    @Autowired
    private BreadcrumbService breadcrumbService = null;

    @Autowired
    private BlockService blockService = null;

    @Autowired
    private BlockCaptainService blockCaptainService = null;

    @Autowired
    private HouseService houseService = null;

    @Autowired
    private ChangeService changeService = null;

    @Autowired
    private OfficierService officierService = null;

    @RequestMapping(value = "/district/{districtName}", method = RequestMethod.GET)
    public String communityHandler(Model model, @PathVariable String districtName) throws FileNotFoundException, IOException {
        model.addAttribute("blockService", blockService);
        model.addAttribute("blockCaptainService", blockCaptainService);
        model.addAttribute("houseService", houseService);
        model.addAttribute("officierService", officierService);
        model.addAttribute("districtName", districtName);
        model.addAttribute("districtRepresentative", officierService.getDistrictRepresentative(districtName));

        breadcrumbService.clear();
        breadcrumbService.put("Home", "/");
        breadcrumbService.put("Districts", "/districts");
        breadcrumbService.put(districtName, "");
        breadcrumbService.put("Logout", "/j_spring_security_logout");
        model.addAttribute("breadcrumbs", breadcrumbService.getBreadcrumbs());
        return "district";
    }

    // name=last, value=<new_value>
    @RequestMapping(value = "/district/update_representative", method = RequestMethod.POST)
    @ResponseBody
    public String updateRepresentative(@ModelAttribute FormDistrict formDistrict, Model model) throws FileNotFoundException, IOException {
        final String districtName = formDistrict.getPk();
        final String representativeName = formDistrict.getValue();
        String message = String.format("district(%s) old(%s) new(%s)", districtName, officierService.getDistrictRepresentative(districtName), representativeName);
        changeService.logChange("update_representative", message);
        officierService.updateDistrictRepresentative(districtName, formDistrict.getValue());
        officierService.write();
        return representativeName;
    }

    public void setChangeService(ChangeService changeService) {
        this.changeService = changeService;
    }

    public void setOfficierService(OfficierService officierService) {
        this.officierService = officierService;
    }

    public BreadcrumbService getBreadcrumbService() {
        return breadcrumbService;
    }

    public void setBlockService(BlockService blockService) {
        this.blockService = blockService;
    }

    /**
     * @param blockCaptainService the blockCaptainService to set
     */
    public void setBlockCaptainService(BlockCaptainService blockCaptainService) {
        this.blockCaptainService = blockCaptainService;
    }

    /**
     * @param houseService the houseService to set
     */
    public void setHouseService(HouseService houseService) {
        this.houseService = houseService;
    }

}
