package org.terasology.sensors;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.network.Replicate;

/**
 * A component representing a sensor entity. The sensor entity should contain trigger volumes for the detection of other
 * entities within a specified volume.
 */
public class SensorComponent implements Component{
    /**
     * The entity utilising this sensor.
     */
    @Replicate
    public EntityRef physicalSensor = EntityRef.NULL;
}
