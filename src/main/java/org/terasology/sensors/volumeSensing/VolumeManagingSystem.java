package org.terasology.sensors.volumeSensing;

import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.DestroyEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.network.NetworkComponent;
import org.terasology.physics.StandardCollisionGroup;
import org.terasology.physics.components.TriggerComponent;
import org.terasology.physics.components.shapes.BoxShapeComponent;
import org.terasology.registry.In;
import org.terasology.sensors.ActivateSensorEvent;
import org.terasology.sensors.DeactivateSensorEvent;
import org.terasology.sensors.PhysicalSensorComponent;
import org.terasology.sensors.SensorComponent;

/**
 * A system responsible for managing detection volumes. It creates trigger volumes that can be used to detect if an
 * entity enters another's radius. It is then the responsibility of other individual sensing sub-systems, such as
 * {@link VolumeSensingSystem} and {@link ConeSensingSystem} to determine if the triggering entity has been detected.
 */
@RegisterSystem
public class VolumeManagingSystem extends BaseComponentSystem{
    @In
    EntityManager entityManager;

    /**
     * This event handler is responsible for creating trigger volumes used to detect entities. It creates a new sensor
     * entity which contains all of the trigger components needed to sense entities within a specified volume and adds
     * a reference to this sensor in the attached {@link PhysicalSensorComponent}.
     *
     * @param event the event received
     * @param entity the entity affected
     * @param volumeSensor a component containing details of the volume around the affected entity to sense
     * @param location a component containing the entity's location
     * @param physical a component containing information about the entity's sensor
     */
    @ReceiveEvent
    public void createVolumeSensor(ActivateSensorEvent event, EntityRef entity,
            VolumeSensorComponent volumeSensor, LocationComponent location,
            PhysicalSensorComponent physical){
        
        if(physical.activated){
            return;
        }
        if(physical.sensor != null && physical.sensor != EntityRef.NULL){
            return;
        }
        
        EntityBuilder builder = entityManager.newBuilder();
        
        NetworkComponent network = new NetworkComponent();
        builder.addComponent(network);
        
        LocationComponent sensorLoc = new LocationComponent();
        Vector3f pos = location.getWorldPosition();
        Vector3f dir = location.getWorldDirection();
        Quat4f sensorRot = location.getWorldRotation();
        sensorRot.mul(volumeSensor.sensorRotOffset);
        Vector3f sensorDir = volumeSensor.directionRot.rotate(dir);
        sensorDir.scale(volumeSensor.distanceFromEntity);
        pos.add(sensorDir);
        sensorLoc.setWorldPosition(pos);
        sensorLoc.setWorldRotation(sensorRot);
        sensorLoc.setWorldScale((volumeSensor.range/0.5f));
        builder.addComponent(sensorLoc);
        
        BoxShapeComponent box = new BoxShapeComponent();
        builder.addComponent(box);
        
        SensorComponent sensorC = new SensorComponent();
        sensorC.physicalSensor = entity;
        builder.addComponent(sensorC);
        
        TriggerComponent trigger = new TriggerComponent();
        trigger.collisionGroup = StandardCollisionGroup.SENSOR;
        trigger.detectGroups = volumeSensor.detectGroups;
        builder.addComponent(trigger);
        
        EntityRef sensor = builder.build();
        
        if(volumeSensor.sensorAttachedToEntity){
            Location.attachChild(entity, sensor);
        }
        
        physical.sensor = sensor;
        physical.activated = true;
        entity.saveComponent(physical);
    }

    /**
     * When a {@link VolumeSensorComponent} is changed, then this method modifier the existing sensor to utilise the
     * new properties contained within the component.
     *
     * @param event the event received
     * @param entity the affected entity
     * @param volumeSensor a sensor belonging to the affected entity
     * @param physical a component containing a reference to the affected sensor entity
     */
    @ReceiveEvent(components = {VolumeSensorComponent.class})
    public void changeVolumeSensor(OnChangedComponent event, EntityRef entity,
            VolumeSensorComponent volumeSensor, PhysicalSensorComponent physical){
        
        if(!physical.activated){
            return;
        }
        LocationComponent location = entity.getComponent(LocationComponent.class);
        if(location == null){
            return;
        }
        
        EntityRef sensor = physical.sensor;
        if(sensor == null || sensor == EntityRef.NULL){
            return;
        }
        
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
        sensorLoc.setWorldRotation(location.getWorldRotation());
        sensorLoc.setWorldScale((volumeSensor.range/0.5f));
        sensor.saveComponent(sensorLoc);
        
        if(volumeSensor.sensorAttachedToEntity){
            Location.attachChild(entity, sensor);
        }
        else{
            Location.removeChild(entity, sensor);
        }
        
        TriggerComponent trigger = sensor.getComponent(TriggerComponent.class);
        if(trigger == null){
            return;
        }
        trigger.collisionGroup = StandardCollisionGroup.SENSOR;
        trigger.detectGroups = volumeSensor.detectGroups;
        sensor.saveComponent(trigger);
    }

    /**
     * When a sensor is deactivated, this method removes the sensor entity from the world.
     *
     * @param event the event received
     * @param entity the affected entity
     * @param volumeSensor the volume covered by the sensor
     * @param physical a component containing a reference to the sensor entity
     */
    @ReceiveEvent(components = {VolumeSensorComponent.class, LocationComponent.class})
    public void removeVolumeSensor(DeactivateSensorEvent event, EntityRef entity,
            VolumeSensorComponent volumeSensor, PhysicalSensorComponent physical){
        if(!physical.activated){
            return;
        }
        EntityRef sensor = physical.sensor;
        physical.sensor = EntityRef.NULL;
        physical.activated = false;
        entity.saveComponent(physical);
        if(sensor != null && sensor != EntityRef.NULL){
            sensor.send(new DestroyEvent(EntityRef.NULL, EntityRef.NULL,
                    EngineDamageTypes.DIRECT.get()));
        }
    }

}
