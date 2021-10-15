/*
 * Copyright (C) 2021 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package de.fraunhofer.iosb.ilt.frostserver.persistence.pgjooq.tables;

import org.jooq.DataType;
import org.jooq.Name;

/**
 *
 * @author hylke
 */
public class StaLinkTableDynamic<J extends Comparable> extends StaLinkTable<J, StaLinkTableDynamic<J>> {

    private final Name tableName;
    private int idFieldIdx;

    public StaLinkTableDynamic(Name tableName, DataType<J> idType) {
        super(idType, tableName, null);
        this.tableName = tableName;
    }

    private StaLinkTableDynamic(Name alias, StaLinkTableDynamic<J> aliased, int idFieldIdx) {
        super(aliased.getIdType(), alias, aliased);
        this.tableName = aliased.getTableName();
        this.idFieldIdx = idFieldIdx;
    }

    public Name getTableName() {
        return tableName;
    }

    @Override
    public StaLinkTableDynamic<J> as(Name as) {
        return new StaLinkTableDynamic<>(as, this, idFieldIdx).initCustomFields();
    }

    @Override
    public StaLinkTableDynamic<J> getThis() {
        return this;
    }

}