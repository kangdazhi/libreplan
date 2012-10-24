/*
 * This file is part of LibrePlan
 *
 * Copyright (C) 2009-2010 Fundación para o Fomento da Calidade Industrial e
 *                         Desenvolvemento Tecnolóxico de Galicia
 * Copyright (C) 2010-2011 Igalia, S.L.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.libreplan.business.common.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.libreplan.business.BootstrapOrder;
import org.libreplan.business.calendars.daos.IBaseCalendarDAO;
import org.libreplan.business.calendars.entities.BaseCalendar;
import org.libreplan.business.calendars.entities.CalendarData.Days;
import org.libreplan.business.calendars.entities.Capacity;
import org.libreplan.business.common.daos.IConfigurationDAO;
import org.libreplan.business.common.daos.IEntitySequenceDAO;
import org.libreplan.business.workingday.EffortDuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a default {@link Configuration} with default values. It also creates
 * a default {@link OrderSequence}.
 * @author Manuel Rego Casasnovas <mrego@igalia.com>
 */
@Component
@Scope("singleton")
@BootstrapOrder(-1)
public class ConfigurationBootstrap implements IConfigurationBootstrap {

    private static final String COMPANY_CODE = "COMPANY_CODE";

    private static final String PREFIX = "PREFIX";

    @Autowired
    private IConfigurationDAO configurationDAO;

    @Autowired
    private IBaseCalendarDAO baseCalendarDAO;

    @Autowired
    private IEntitySequenceDAO entitySequenceDAO;

    @Override
    @Transactional
    public void loadRequiredData() {
        loadRequiredDataSequences();

        List<Configuration> list = configurationDAO.list(Configuration.class);
        if (list.isEmpty()) {
            Configuration configuration = Configuration.create();
            configuration.setDefaultCalendar(getDefaultCalendar());
            configuration.setCompanyCode(COMPANY_CODE);
            LDAPConfiguration ldapConfiguration = configuration
                    .getLdapConfiguration();
            if (null == configuration.getLdapConfiguration()) {
                ldapConfiguration = LDAPConfiguration.create();
            }
            configuration.setLdapConfiguration(ldapConfiguration);

            JiraConfiguration jiraConfiguration = configuration
                    .getJiraConfiguration();
            if (null == configuration.getJiraConfiguration()) {
                jiraConfiguration = JiraConfiguration.create();
            }
            configuration.setJiraConfiguration(jiraConfiguration);

            configurationDAO.save(configuration);
        }
    }

    public void loadRequiredDataSequences() {
        Map<EntityNameEnum, List<EntitySequence>> mapSequences = initEntitySequences();
        for (final EntityNameEnum entityName : EntityNameEnum.values()) {
            if ((mapSequences.get(entityName)).isEmpty()) {
                createDefaultEntitySquenceIfNotExist(entityName);
            }
        }
    }

    private Map<EntityNameEnum, List<EntitySequence>> initEntitySequences() {
        Map<EntityNameEnum, List<EntitySequence>> entitySequences = new HashMap<EntityNameEnum, List<EntitySequence>>();
        for (EntityNameEnum entityName : EntityNameEnum.values()) {
            entitySequences.put(entityName, new ArrayList<EntitySequence>());
        }
        for (EntitySequence entitySequence : entitySequenceDAO.getAll()) {
            entitySequences.get(entitySequence.getEntityName()).add(
                    entitySequence);
        }
        return entitySequences;
    }

    private void createDefaultEntitySquenceIfNotExist(EntityNameEnum entityName) {
        String prefix = entityName.toString();
        EntitySequence entitySequence = EntitySequence.create(prefix,
                entityName);
        entitySequence.setActive(true);
        entitySequenceDAO.save(entitySequence);
    }

    private BaseCalendar getDefaultCalendar() {
        BaseCalendar calendar = BaseCalendar.create();

        calendar.setName("Default");
        calendar.setCode(entitySequenceDAO
                .getNextEntityCodeWithoutTransaction(EntityNameEnum.CALENDAR));
        calendar.setCodeAutogenerated(true);

        Capacity eightHours = Capacity.create(EffortDuration.hours(8))
                .overAssignableWithoutLimit();
        Capacity zeroCapacity = Capacity.zero().notOverAssignableWithoutLimit();
        calendar.setCapacityAt(Days.MONDAY, eightHours);
        calendar.setCapacityAt(Days.TUESDAY, eightHours);
        calendar.setCapacityAt(Days.WEDNESDAY, eightHours);
        calendar.setCapacityAt(Days.THURSDAY, eightHours);
        calendar.setCapacityAt(Days.FRIDAY, eightHours);
        calendar.setCapacityAt(Days.SATURDAY, zeroCapacity);
        calendar.setCapacityAt(Days.SUNDAY, zeroCapacity);

        baseCalendarDAO.save(calendar);

        return calendar;
    }
}
