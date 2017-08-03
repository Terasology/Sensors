package org.terasology.sensors;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.network.Replicate;

public class PhysicalSensorComponent implements Component{
    @Replicate
    public EntityRef sensor = EntityRef.NULL;
    @Replicate
    public boolean activated = false;
}
