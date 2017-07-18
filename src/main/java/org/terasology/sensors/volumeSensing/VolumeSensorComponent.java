package org.terasology.sensors.volumeSensing;

import java.util.List;

import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.geom.Quat4f;
import org.terasology.network.Replicate;
import org.terasology.physics.CollisionGroup;
import org.terasology.physics.StandardCollisionGroup;

import com.google.common.collect.Lists;

public class VolumeSensorComponent implements Component{
    @Replicate
    public EntityRef sensor = EntityRef.NULL;
    @Replicate
    public List<CollisionGroup> detectGroups = Lists.<CollisionGroup>newArrayList(StandardCollisionGroup.DEFAULT, 
            StandardCollisionGroup.CHARACTER, StandardCollisionGroup.WORLD);
    @Replicate
    public float range = 5.0f;
    @Replicate
    public boolean sensorAttachedToEntity = true;
    @Replicate
    public float distanceFromEntity = 0.0f;
    @Replicate
    public Quat4f directionRot = new Quat4f();

}
