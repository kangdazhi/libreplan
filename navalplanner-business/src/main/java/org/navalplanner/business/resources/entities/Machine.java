package org.navalplanner.business.resources.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.validator.AssertTrue;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Valid;
import org.navalplanner.business.common.Registry;
import org.navalplanner.business.common.exceptions.InstanceNotFoundException;
import org.navalplanner.business.resources.daos.IMachineDAO;

/**
* Entity
*
* @author Javier Moran Rua <jmoran@igalia.com>
* @author Fernando Bellas Permuy <fbellas@udc.es>
*/
public class Machine extends Resource {

    private String code;

    private String name;

    private String description;

    private Set<MachineWorkersConfigurationUnit> configurationUnits = new HashSet<MachineWorkersConfigurationUnit>();

    @Valid
    public Set<MachineWorkersConfigurationUnit> getConfigurationUnits() {
        return Collections.unmodifiableSet(configurationUnits);
    }

    public void addMachineWorkersConfigurationUnit(
            MachineWorkersConfigurationUnit unit) {
        configurationUnits.add(unit);
    }

    public void removeMachineWorkersConfigurationUnit(
            MachineWorkersConfigurationUnit unit) {
        configurationUnits.remove(unit);
    }

    public static Machine createUnvalidated(String code, String name, String description) {

        Machine machine = new Machine();

        machine.code = code;
        machine.name = name;
        machine.description = description;
        machine.setNewObject(true);

        return machine;

    }

    protected Machine() {

    }

    protected Machine(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static Machine create() {
        return create(new Machine());
    }

    @NotEmpty(message="machine code not specified")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @NotEmpty(message="machine name not specified")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return code + " :: " + name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean satisfiesCriterions(Set<Criterion> criterions) {
        ICriterion compositedCriterion = CriterionCompounder.buildAnd(
                new ArrayList<ICriterion>(criterions)).getResult();
        return compositedCriterion.isSatisfiedBy(this);
    }

    @AssertTrue(message="machine code has to be unique. It is already used")
    public boolean checkConstraintUniqueCode() {
        boolean result;
        if (isNewObject()) {
            result = !existsMachineWithTheCode();
        } else {
            result = isIfExistsTheExistentMachineThisOne();
        }
        return result;
    }

    private boolean existsMachineWithTheCode() {
        IMachineDAO machineDAO = Registry.getMachineDAO();
        return machineDAO.existsMachineWithCodeInAnotherTransaction(code);
    }

    private boolean isIfExistsTheExistentMachineThisOne() {
        IMachineDAO machineDAO = Registry.getMachineDAO();
        try {
            Machine machine =
                machineDAO.findUniqueByCodeInAnotherTransaction(code);
            return machine.getId().equals(getId());
        } catch (InstanceNotFoundException e) {
            return true;
        }
    }

    @Override
    protected boolean isCriterionSatisfactionOfCorrectType(
       CriterionSatisfaction c) {

        return super.isCriterionSatisfactionOfCorrectType(c) ||
            c.getResourceType().equals(ResourceEnum.MACHINE);

    }

}
