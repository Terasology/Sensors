package org.terasology.sensors.volumeSensing;

import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.NetworkComponent;
import org.terasology.physics.StandardCollisionGroup;
import org.terasology.physics.components.TriggerComponent;
import org.terasology.physics.shapes.BoxShapeComponent;
import org.terasology.registry.In;
import org.terasology.rendering.logic.MeshComponent;
import org.terasology.sensors.ActivateSensorEvent;
import org.terasology.sensors.DeactivateSensorEvent;
import org.terasology.sensors.SensorComponent;
import org.terasology.utilities.Assets;

@RegisterSystem
public class VolumeManagingSystem extends BaseComponentSystem{
    @In
    EntityManager entityManager;
    
    @ReceiveEvent
    public void createSensor(ActivateSensorEvent event, EntityRef entity, VolumeSensorComponent volumeSensor, 
            LocationComponent location){
        if(volumeSensor.sensor != null && volumeSensor.sensor != EntityRef.NULL){
            return;
        }
        
        EntityBuilder builder = entityManager.newBuilder();
        
        NetworkComponent network = new NetworkComponent();
        builder.addComponent(network);
        
        LocationComponent sensorLoc = new LocationComponent();
        Vector3f pos = location.getWorldPosition();
        Vector3f dir = location.getWorldDirection();
        Vector3f sensorDir = volumeSensor.directionRot.rotate(dir);
        sensorDir.scale(volumeSensor.distanceFromEntity);
        pos.add(sensorDir);
        sensorLoc.setWorldPosition(pos);
        sensorLoc.setWorldRotation(location.getWorldRotation());
        sensorLoc.setWorldScale((volumeSensor.range/0.5f));
        builder.addComponent(sensorLoc);
        
        BoxShapeComponent box = new BoxShapeComponent();
        builder.addComponent(box);
        
        SensorComponent sensorC = new SensorComponent();
        sensorC.entity = entity;
        builder.addComponent(sensorC);
        
        TriggerComponent trigger = new TriggerComponent();
        trigger.collisionGroup = StandardCollisionGroup.SENSOR;
        trigger.detectGroups = volumeSensor.detectGroups;
        builder.addComponent(trigger);
        
        EntityRef sensor = builder.build();
        
        if(volumeSensor.sensorAttachedToEntity){
            Location.attachChild(entity, sensor);
        }
        
        volumeSensor.sensor = sensor;
        entity.saveComponent(volumeSensor);
    }
    
    @ReceiveEvent(components = {VolumeSensorComponent.class, LocationComponent.class}, priority = EventPriority.PRIORITY_LOW)
    public void addMeshForClient(ActivateSensorEvent event, EntityRef entity, VolumeSensorComponent volumeSensor){
        EntityRef sensor = volumeSensor.sensor;
        if(!sensor.hasComponent(MeshComponent.class)){
            MeshComponent mesh = new MeshComponent();
            mesh.translucent = true;
            mesh.material = Assets.getMaterial("CombatSystem:forceField").get();
            mesh.mesh = Assets.getMesh("CombatSystem:zesphere").get();
            sensor.addComponent(mesh);
        }
    }
    
//    @ReceiveEvent(components = {VolumeSensorComponent.class})
//    public void changeSensor(OnChangedComponent event, EntityRef entity, VolumeSensorComponent volumeSensor){
//        LocationComponent location = entity.getComponent(LocationComponent.class);
//        if(location == null){
//            return;
//        }
//        
//        EntityRef sensor = volumeSensor.sensor;
//        if(sensor == null || sensor == EntityRef.NULL){
//            return;
//        }
//        
//        LocationComponent sensorLoc = sensor.getComponent(LocationComponent.class);
//        if(sensorLoc == null){
//            return;
//        }
//        Vector3f pos = location.getWorldPosition();
//        Vector3f dir = location.getWorldDirection();
//        Vector3f sensorDir = volumeSensor.directionRot.rotate(dir);
//        sensorDir.scale(volumeSensor.distanceFromEntity);
//        pos.add(sensorDir);
//        sensorLoc.setWorldPosition(pos);
//        sensor.saveComponent(sensorLoc);
//        
//        if(volumeSensor.sensorAttachedToEntity){
//            Location.attachChild(entity, sensor);
//        }
//        else{
//            Location.removeChild(entity, sensor);
//        }
//        
//        SphereShapeComponent sphere = sensor.getComponent(SphereShapeComponent.class);
//        if(sphere == null){
//            return;
//        }
//        sphere.radius = volumeSensor.range;
//        sensor.saveComponent(sphere);
//        
//        TriggerComponent trigger = sensor.getComponent(TriggerComponent.class);
//        if(trigger == null){
//            return;
//        }
//        trigger.collisionGroup = StandardCollisionGroup.SENSOR;
//        trigger.detectGroups = volumeSensor.detectGroups;
//        sensor.saveComponent(trigger);
//    }
    
    @ReceiveEvent(components = {VolumeSensorComponent.class, LocationComponent.class})
    public void removeSensor(DeactivateSensorEvent event, EntityRef entity, VolumeSensorComponent volumeSensor){
        EntityRef sensor = volumeSensor.sensor;
        volumeSensor.sensor = EntityRef.NULL;
        if(sensor != null && sensor != EntityRef.NULL){
            sensor.destroy();
        }
    }

}
