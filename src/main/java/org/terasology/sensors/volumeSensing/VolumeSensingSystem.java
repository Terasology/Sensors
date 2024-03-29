// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors.volumeSensing;

import com.google.common.collect.Lists;
import org.joml.Vector3f;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.physics.CollisionGroup;
import org.terasology.engine.physics.HitResult;
import org.terasology.engine.physics.Physics;
import org.terasology.engine.physics.StandardCollisionGroup;
import org.terasology.engine.physics.components.TriggerComponent;
import org.terasology.engine.physics.events.CollideEvent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.sensors.EntitySensedEvent;
import org.terasology.sensors.SensorComponent;

import java.util.List;

/**
 * A sensor sub-system responsible for marking an entity within a detection radius as detected, assuming that there are
 * no other entities that lie between the triggering entity to the triggered entity.
 */
@RegisterSystem
public class VolumeSensingSystem extends BaseComponentSystem {

    @In
    private Physics physics;

    @Priority(EventPriority.PRIORITY_CRITICAL)
    @ReceiveEvent
    public void removeCollisionResponse(CollideEvent event, EntityRef entity) {
        EntityRef target = event.getOtherEntity();

        if (!target.exists()) {
            event.consume();
        } else if (target.hasComponent(SensorComponent.class)) {
            event.consume();
        }
    }

    /**
     * This event handler is responsible for managing when an entity has been detected. It sends {@link
     * EntitySensedEvent} events if the detection is successful.
     *
     * @param event the event received
     * @param entity the affected entity
     * @param sensor the affected sensor
     * @param trigger the trigger volume that was entered
     */
    @ReceiveEvent
    public void entityDetected(CollideEvent event, EntityRef entity, SensorComponent sensor, TriggerComponent trigger) {
        EntityRef sensorParent = sensor.physicalSensor;
        if (sensorParent == null || sensorParent == EntityRef.NULL) {
            return;
        }
        VolumeSensorComponent volumeSensor = sensorParent.getComponent(VolumeSensorComponent.class);
        if (volumeSensor == null) {
            return;
        }
        EntityRef target = event.getOtherEntity();

        if (sensorParent.equals(target)) {
            return;
        }

        LocationComponent loc = sensorParent.getComponent(LocationComponent.class);
        LocationComponent targetLoc = target.getComponent(LocationComponent.class);
        if (loc == null || targetLoc == null) {
            return;
        }
        Vector3f sensorPos = loc.getWorldPosition(new Vector3f());
        Vector3f targetPos = targetLoc.getWorldPosition(new Vector3f());
        float distance = sensorPos.distance(targetPos);
        if (distance > volumeSensor.range) {
            return;
        }

        //checks if the sensor should be notified only if the entity is visible
        if (!sensorParent.hasComponent(TargetVisibleComponent.class)) {
            sensorParent.send(new EntitySensedEvent(target));
            return;
        }

        //should sense entity only if target is visible
        Vector3f dir = targetPos.sub(sensorPos);
        dir.normalize();

        List<CollisionGroup> rayGroup = Lists.newArrayList();
        rayGroup.addAll(trigger.detectGroups);
        boolean hasWorld = false;
        for (CollisionGroup group : rayGroup) {
            if (group.getFlag() == StandardCollisionGroup.WORLD.getFlag()) {
                hasWorld = true;
            }
        }

        if (!hasWorld) {
            rayGroup.add(StandardCollisionGroup.WORLD);
        }

        HitResult result = physics.rayTrace(sensorPos, dir, distance + 1.0f,rayGroup.toArray(new CollisionGroup[0]));

        if (result.isHit()) {
            if (target.equals(result.getEntity())) {
                sensorParent.send(new EntitySensedEvent(target));
            }
        }
    }
}
