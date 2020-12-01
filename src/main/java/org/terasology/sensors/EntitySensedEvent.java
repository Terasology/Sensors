// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.AbstractConsumableEvent;

/**
 * An event executed when an entity is sensed. This will usually involve the entity entering within proximity of a sensor.
 */
public class EntitySensedEvent extends AbstractConsumableEvent {
    EntityRef target = EntityRef.NULL;

    public EntitySensedEvent(EntityRef entity) {
        target = entity;
    }

    /**
     * Returns the sensed entity, which has been detected by a sensor on the target entity.
     *
     * @return the sensed entity
     */
    public EntityRef getEntity() {
        return target;
    }
}
