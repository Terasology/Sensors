package org.terasology.sensors;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.network.Replicate;

/**
 * A sensor located physically in the world, which can detect entities when activated.
 */
public class PhysicalSensorComponent implements Component{

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
}
