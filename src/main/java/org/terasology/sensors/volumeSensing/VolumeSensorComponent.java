package org.terasology.sensors.volumeSensing;

import java.util.List;

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Quat4f;
import org.terasology.network.Replicate;
import org.terasology.physics.CollisionGroup;
import org.terasology.physics.StandardCollisionGroup;
import org.terasology.world.block.ForceBlockActive;

import com.google.common.collect.Lists;

@ForceBlockActive
public class VolumeSensorComponent implements Component{
    @Replicate
    public List<CollisionGroup> detectGroups = Lists.<CollisionGroup>newArrayList(StandardCollisionGroup.DEFAULT, 
            StandardCollisionGroup.CHARACTER);
    @Replicate
    public float range = 5.0f;
    @Replicate
    public boolean sensorAttachedToEntity = true;
    @Replicate
    public float distanceFromEntity = 0.0f;
    @Replicate
    public Quat4f directionRot = new Quat4f(0, 0, 0, 1);
    @Replicate
    public Quat4f sensorRotOffset = new Quat4f(0, 0, 0, 1);

}
