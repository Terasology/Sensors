package org.terasology.sensors;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.network.Replicate;

public class SensorComponent implements Component{
    @Replicate
    public EntityRef sensorParent = EntityRef.NULL;

}
