/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.db.apis.entities;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;


/**
 * Class required to execute JCP FE when no other Entities are present in the
 * JCP DB FE package.
 * <p>
 * This is required from Spring Boot to initialize the DB environment.
 */
@Entity
@Data
public class EntityPlaceholder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String fieldA;

    @Enumerated(EnumType.STRING)                                // Index 1, 2
    private Type enumField;

    @Lob
    private String largeTextField;


    // Extra profile

    @CreationTimestamp
    private Date registeredAt;


    public static EntityPlaceholder fromJOSPEvent(JOSPPlaceHolder placeHolder) {
        EntityPlaceholder e = new EntityPlaceholder();
        e.id = placeHolder.getId();
        e.fieldA = placeHolder.getFieldA();
        e.enumField = placeHolder.getEnumField();
        e.largeTextField = placeHolder.getLargeTextField();
        return e;
    }

    public static JOSPPlaceHolder toJOSPEvent(EntityPlaceholder entityPlaceholder) {
        return new JOSPPlaceHolder(entityPlaceholder.getId(), entityPlaceholder.getFieldA(), entityPlaceholder.getEnumField(), entityPlaceholder.getLargeTextField());
    }

    private static class JOSPPlaceHolder {

        public JOSPPlaceHolder(long id, String fieldA, Type typeField, String largeTextField) {
        }

        public long getId() {
            return 0;
        }

        ;

        public String getFieldA() {
            return "";
        }

        ;

        public Type getEnumField() {
            return Type.A;
        }

        ;

        public String getLargeTextField() {
            return "";
        }

        ;

    }

    private static enum Type {A, B}
}
