// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;
import org.terasology.gestalt.entitysystem.component.Component;

/**
 * A sensor located physically in the world, which can detect entities when activated.
 */
public class PhysicalSensorComponent implements Component<PhysicalSensorComponent> {

    /**
     * The sensor entity to utilise.
     */
    @Replicate
    public EntityRef sensor = EntityRef.NULL;

    /**
     * If true, then the sensor is capable to detecting entities.
     */
    @Replicate
    public boolean activated = false;

    @Override
    public void copy(PhysicalSensorComponent other) {
        this.sensor = other.sensor;
        this.activated = other.activated;
    }
}
