package org.terasology.sensors;

import org.terasology.entitySystem.entity.EntityRef;

import com.google.common.collect.HashBiMap;

public class EntitySensorBiMap {
    private static HashBiMap<EntityRef, EntityRef> entitySensors = HashBiMap.create();
    
    public static void mapSensorAndEntity(EntityRef entity, EntityRef sensor){
        entitySensors.put(entity, sensor);
    }
    
    public static EntityRef getSensorForEntity(EntityRef entity){
        return entitySensors.get(entity);
    }
    
    public static EntityRef getEntityForSensor(EntityRef sensor){
        return entitySensors.inverse().get(sensor);
    }
    
    public static EntityRef removeMappingForEntity(EntityRef entity){
        return entitySensors.remove(entity);
    }
    
    public static EntityRef removeMappingBySensor(EntityRef sensor){
        return entitySensors.inverse().remove(sensor);
    }

}
