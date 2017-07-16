package org.terasology.sensors;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.AbstractConsumableEvent;

public class EntitySensedEvent extends AbstractConsumableEvent{
    EntityRef target = EntityRef.NULL;
    
    public EntitySensedEvent(EntityRef entity){
        target = entity;
    }
    
    public EntityRef getEntity(){
        return target;
    }

}
