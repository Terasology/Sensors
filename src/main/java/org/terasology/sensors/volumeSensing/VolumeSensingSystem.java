package org.terasology.sensors.volumeSensing;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.physics.CollisionGroup;
import org.terasology.physics.HitResult;
import org.terasology.physics.Physics;
import org.terasology.physics.components.TriggerComponent;
import org.terasology.physics.events.CollideEvent;
import org.terasology.registry.In;
import org.terasology.sensors.EntitySensedEvent;
import org.terasology.sensors.SensorComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class VolumeSensingSystem extends BaseComponentSystem{
    @In
    private Physics physics;
    
    @ReceiveEvent
    public void entityDetected(CollideEvent event, EntityRef entity, SensorComponent sensor, TriggerComponent trigger){
        EntityRef sensorParent = sensor.sensorParent;
        if(sensorParent == null || sensorParent == EntityRef.NULL){
            return;
        }
        
        EntityRef target = event.getOtherEntity();
        
        LocationComponent sensorLoc = sensorParent.getComponent(LocationComponent.class);
        LocationComponent targetLoc = target.getComponent(LocationComponent.class);
        if(sensorLoc == null || targetLoc == null){
            return;
        }
        Vector3f sensorPos = sensorLoc.getWorldPosition();
        Vector3f targetPos = targetLoc.getWorldPosition();
        float distance = sensorPos.distance(targetPos);
        Vector3f dir = targetPos.sub(sensorPos);
        dir.normalize();
        
        HitResult result  = physics.rayTrace(sensorPos, dir, distance, 
                trigger.detectGroups.toArray(new CollisionGroup[trigger.detectGroups.size()]));
        
        if(result.isHit()){
            if(target.getId() == result.getEntity().getId()){
                sensorParent.send(new EntitySensedEvent(target));
            }
        }
    }
}
