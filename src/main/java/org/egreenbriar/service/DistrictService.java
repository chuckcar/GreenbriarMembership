package org.egreenbriar.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import org.egreenbriar.model.District;
import org.egreenbriar.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistrictService {

    @Autowired
    private HouseService houseService = null;

    private final Set<District> districts = new TreeSet<>();

    @PostConstruct
    public void initialize() {
        for (Map.Entry<String, House> entry : houseService.getHouses().entrySet()) {
            House house = entry.getValue();
            addDistrict(house.getDistrictName());
        }
    }

    public int getPercentMembership(final String year) {
        int numHouses = houseService.getHouses().size();
        int numMembers = 0;
        for (Entry<String, House> entry : houseService.getHouses().entrySet()) {
            House house = entry.getValue();
            if (house.memberInYear(year)) {
                numMembers++;
            }
        }
        return (int) (((float) numMembers / (float) numHouses) * 100);
    }

    public int getMembershipCount(final String year) {
        int numHouses = houseService.getHouses().size();
        int numMembers = 0;
        for (Entry<String, House> entry : houseService.getHouses().entrySet()) {
            House house = entry.getValue();
            if (house.memberInYear(year)) {
                numMembers++;
            }
        }
        return numMembers;
    }

    public int getPercentMembership(final String districtName, final String year) {
        int numHouses = 0;
        int numMembers = 0;
        for (Entry<String, House> entry : houseService.getHouses().entrySet()) {
            House house = entry.getValue();
            if (house.getDistrictName().equals(districtName)) {
                numHouses++;
                if (house.memberInYear(year)) {
                    numMembers++;
                }
            }
        }
        return (int) (((float) numMembers / (float) numHouses) * 100);
    }

    public District getDistrict(String name) {
        District rv = null;
        for (District district : getDistricts()) {
            if (district.getName().equals(name)) {
                rv = district;
            }
        }
        return rv;
    }

    public District addDistrict(final String districtName) {
        for (District district : districts) {
            if (district.is(districtName)) {
                return district;
            }
        }
        District district = new District(districtName);
        districts.add(district);
        return district;
    }

    public void setHouseService(HouseService houseService) {
        this.houseService = houseService;
    }

    public Set<District> getDistricts() {
        return districts;
    }
}
