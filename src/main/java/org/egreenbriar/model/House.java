package org.egreenbriar.model;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class House implements Comparable {

    private String uuid;
    private String houseNumber = null;
    private String streetName = null;
    private final Set<Person> people = new TreeSet<>();
    private final Set<Membership> years = new TreeSet<>();

    public House(final String houseNumber, final String streetName) {
        this.uuid = UUID.randomUUID().toString();
        this.houseNumber = houseNumber;
        this.streetName = streetName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.getHouseNumber() != null ? this.getHouseNumber().hashCode() : 0);
        hash = 67 * hash + (this.getStreetName() != null ? this.getStreetName().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "House{" + "houseNumber=" + getHouseNumber() + ", streetName=" + getStreetName() + ", people=" + getPeople() + ", years=" + getYears() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final House other = (House) obj;
        if ((this.getHouseNumber() == null) ? (other.getHouseNumber() != null) : !this.houseNumber.equals(other.houseNumber)) {
            return false;
        }
        return !((this.getStreetName() == null) ? (other.getStreetName() != null) : !this.streetName.equals(other.streetName));
    }

    @Override
    public int compareTo(Object o) {
        int rv = -1;
        if (o != null) {
            House that = (House)o;
            if (this.equals(that)) {
                rv = 0;
            } else {
                rv = this.getHouseNumber().compareTo(that.getHouseNumber());
                if (rv == 0) {
                    rv = this.getStreetName().compareTo(that.getStreetName());
                }
            }
        }
        return rv;
    }

    public Person addPerson(final String last, final String first, final String phone, final String email, final String comment) {
        if (last.isEmpty() && first.isEmpty()) {
            return Person.EMPTY;
        }
        Person rv = null;
        for (Person person : getPeople()) {
            if (person.is(last, first)) {
                rv = person;
                break;
            }
        }
        if (rv == null) {
            rv = new Person(last, first, phone, email, comment);
            getPeople().add(rv);
        }
        return rv;
    }

    boolean isAt(String houseNumber, String streetName) {
        return this.getHouseNumber().equals(houseNumber) && this.getStreetName().equals(streetName);
    }

    public void addYear(Membership year) {
        getYears().add(year);
    }

    /**
     * @return the houseNumber
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * @param houseNumber the houseNumber to set
     */
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * @return the streetName
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * @param streetName the streetName to set
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * @return the people
     */
    public Set<Person> getPeople() {
        return people;
    }

    /**
     * @return the years
     */
    public Set<Membership> getYears() {
        return years;
    }

    public boolean memberIn(Membership year) {
        return years.contains(year);
    }

    public String memberInYear2012Style() {
        return memberInYear("2012") ? "year_button" : "year_button negate";
    }
    
    public String memberInYear2013Style() {
        return memberInYear("2013") ? "year_button" : "year_button negate";
    }
    
    public String memberInYear2014Style() {
        return memberInYear("2014") ? "button" : "button negate";
    }
    
    public boolean memberInYear(String year) {
        boolean rv = false;
        if (year != null) {
            switch (year) {
                case "2012":
                    rv = years.contains(Membership.YEAR_2012);
                    break;
                case "2013":
                    rv = years.contains(Membership.YEAR_2013);
                    break;
            }
        }
        return rv;
    }

    public boolean notMemberInYear(String year) {
        return !memberInYear(year);
    }

    public String getUuid() {
        return uuid;
    }

    public void toggle2014Membership() {
        if (memberInYear("2014")) {
            getYears().remove(Membership.YEAR_2014);
        } else {
            getYears().add(Membership.YEAR_2014);
        }
    }
}