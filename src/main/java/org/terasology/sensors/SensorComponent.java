// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.network.Replicate;

/**
 * A component representing a sensor entity. The sensor entity should contain trigger volumes for the detection of other
 * entities within a specified volume.
 */
public class SensorComponent implements Component {
    /**
     * The entity utilising this sensor.
     */
    @Replicate
    public EntityRef physicalSensor = EntityRef.NULL;
}
