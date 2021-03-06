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
package org.libreplan.business.orders.entities;

import java.util.Set;

import org.libreplan.business.requirements.entities.CriterionRequirement;

/**
 * Marks an entity that could have assigned {@link CriterionRequirement}.
 *
 * @author Manuel Rego Casasnovas <mrego@igalia.com>
 */
public interface ICriterionRequirable {

    Set<CriterionRequirement> getCriterionRequirements();

    void addCriterionRequirement(CriterionRequirement requirement);

    void removeCriterionRequirement(CriterionRequirement requirement);

}
