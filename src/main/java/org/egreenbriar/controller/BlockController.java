package org.egreenbriar.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.egreenbriar.form.FormBlock;
import org.egreenbriar.model.Block;
import org.egreenbriar.service.BlockCaptainService;
import org.egreenbriar.service.BlockService;
import org.egreenbriar.service.BreadcrumbService;
import org.egreenbriar.service.ChangeService;
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
public class BlockController {

    @Autowired
    private BreadcrumbService breadcrumbService = null;
    
    @Autowired
    private BlockService blockService = null;
    
    @Autowired
    private BlockCaptainService blockCaptainService = null;

    @Autowired
    private ChangeService changeService = null;

    @RequestMapping(value="/block/{blockName}", method=RequestMethod.GET)
    public String communityHandler(Model model, @PathVariable String blockName) throws FileNotFoundException, IOException {
        Block block = blockService.getBlock(blockName);
        model.addAttribute("block", block);
        
        breadcrumbService.clear();
        breadcrumbService.put("Home", "/");
        breadcrumbService.put("Districts", "/districts");
        breadcrumbService.put(block.getDistrictName(), "/district/" + block.getDistrictName());
        breadcrumbService.put(blockName, "");
        breadcrumbService.put("Logout", "/j_spring_security_logout");        
        model.addAttribute("breadcrumbs", breadcrumbService.getBreadcrumbs());

        return "block";
    }

    // name=last, value=<new_value>
    @RequestMapping(value="/block/update_captain", method = RequestMethod.POST)
    @ResponseBody
    public void updateCaptain(@ModelAttribute FormBlock formBlock, Model model) throws FileNotFoundException, IOException {
        String captainName = blockCaptainService.getCaptainName(formBlock.getPk());
        String message = String.format("block(%s) old(%s) new(%s)", formBlock.getPk(), captainName, formBlock.getValue());
        changeService.logChange("update_captain", message);
        blockCaptainService.update(formBlock.getPk(), formBlock.getValue());
    }
    
    public void setBlockCaptainService(BlockCaptainService blockCaptainService) {
        this.blockCaptainService = blockCaptainService;
    }

    public void setChangeService(ChangeService changeService) {
        this.changeService = changeService;
    }

    /**
     * @param blockService the blockService to set
     */
    public void setBlockService(BlockService blockService) {
        this.blockService = blockService;
    }

}
