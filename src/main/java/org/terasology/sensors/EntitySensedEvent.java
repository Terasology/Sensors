package org.terasology.sensors;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.Event;

public class EntitySensedEvent implements Event{
    EntityRef target = EntityRef.NULL;
    
    public EntitySensedEvent(EntityRef entity){
        target = entity;
    }
    
    public EntityRef getEntity(){
        return target;
    }

}
