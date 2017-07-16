package org.terasology.sensors.volumeSensing;

import java.util.Map;

import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.NetworkComponent;
import org.terasology.physics.StandardCollisionGroup;
import org.terasology.physics.components.TriggerComponent;
import org.terasology.physics.shapes.SphereShapeComponent;
import org.terasology.registry.In;
import org.terasology.sensors.EntitySensorBiMap;
import org.terasology.sensors.SensorComponent;

import com.google.api.client.util.Maps;
import com.google.common.collect.HashBiMap;

@RegisterSystem
public class VolumeManagingSystem extends BaseComponentSystem{
    @In
    EntityManager entityManager;
    
    @ReceiveEvent(components = VolumeSensorComponent.class)
    public void createSensor(OnActivatedComponent event, EntityRef entity, LocationComponent location){
        VolumeSensorComponent volumeSensor = entity.getComponent(VolumeSensorComponent.class);
        
        EntityBuilder builder = entityManager.newBuilder();
        
        NetworkComponent network = new NetworkComponent();
        builder.addComponent(network);
        
        SensorComponent sensorC = new SensorComponent();
        builder.addComponent(sensorC);
        
        TriggerComponent trigger = new TriggerComponent();
        trigger.collisionGroup = StandardCollisionGroup.SENSOR;
        trigger.detectGroups = volumeSensor.detectGroups;
        builder.addComponent(trigger);
        
        SphereShapeComponent sphere = new SphereShapeComponent();
        sphere.radius = volumeSensor.range;
        builder.addComponent(sphere);
        
        LocationComponent sensorLoc = new LocationComponent();
        Vector3f pos = location.getWorldPosition();
        Vector3f dir = location.getWorldDirection();
        Vector3f sensorDir = volumeSensor.directionRot.rotate(dir);
        sensorDir.scale(volumeSensor.distanceFromEntity);
        pos.add(sensorDir);
        sensorLoc.setWorldPosition(pos);
        builder.addComponent(sensorLoc);
        
        EntityRef sensor = builder.build();
        
        if(volumeSensor.sensorAttachedToEntity){
            Location.attachChild(entity, sensor);
        }
        
        EntitySensorBiMap.mapSensorAndEntity(entity, sensor);
    }
    
    @ReceiveEvent(components = VolumeSensorComponent.class)
    public void changeSensor(OnChangedComponent event, EntityRef entity, LocationComponent location){
        VolumeSensorComponent volumeSensor = entity.getComponent(VolumeSensorComponent.class);
        EntityRef sensor = EntitySensorBiMap.getSensorForEntity(entity);
        
        TriggerComponent trigger = sensor.getComponent(TriggerComponent.class);
        if(trigger == null){
            return;
        }
        trigger.collisionGroup = StandardCollisionGroup.SENSOR;
        trigger.detectGroups = volumeSensor.detectGroups;
        sensor.saveComponent(trigger);
        
        SphereShapeComponent sphere = sensor.getComponent(SphereShapeComponent.class);
        if(sphere == null){
            return;
        }
        sphere.radius = volumeSensor.range;
        sensor.saveComponent(sphere);
        
        LocationComponent sensorLoc = sensor.getComponent(LocationComponent.class);
        if(sensorLoc == null){
            return;
        }
        Vector3f pos = location.getWorldPosition();
        Vector3f dir = location.getWorldDirection();
        Vector3f sensorDir = volumeSensor.directionRot.rotate(dir);
        sensorDir.scale(volumeSensor.distanceFromEntity);
        pos.add(sensorDir);
        sensorLoc.setWorldPosition(pos);
        sensor.saveComponent(sensorLoc);
        
        if(volumeSensor.sensorAttachedToEntity){
            Location.attachChild(entity, sensor);
        }
        else{
            Location.removeChild(entity, sensor);
        }
    }
    
    @ReceiveEvent(components = {VolumeSensorComponent.class, LocationComponent.class})
    public void removeSensor(BeforeDeactivateComponent event, EntityRef entity){
        EntityRef sensor = EntitySensorBiMap.removeMappingForEntity(entity);
        sensor.destroy();
    }

}
