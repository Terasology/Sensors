// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors.volumeSensing;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.physics.components.TriggerComponent;
import org.terasology.engine.physics.events.CollideEvent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.sensors.SensorComponent;

/**
 * A system responsible for detecting entities with a conical radius, defined from a point with a particular aperture.
 * If a ConeSensorComponent is present, then this overrides VolumeSensingSystem initially with a higher priority, in
 * order to add additional detection requirements.
 */
@RegisterSystem
public class ConeSensingSystem extends BaseComponentSystem {

    /**
     * This event handler has a higher priority, so that events are handled here first before {@link
     * VolumeSensingSystem}. This allows entities not detected within a specified forward-facing cone to be ignored. Any
     * entities without a {@link ConeSensorComponent} are also ignored, although these events will still be handled by
     * {@link VolumeSensingSystem}.
     *
     * @param event the issued event
     * @param entity the target entity
     * @param sensor the sensor belonging to the target entity
     * @param trigger the trigger belonging to the target entity
     */
    @Priority(EventPriority.PRIORITY_HIGH)
    @ReceiveEvent
    public void entityDetected(CollideEvent event, EntityRef entity, SensorComponent sensor, TriggerComponent trigger) {
        EntityRef sensorParent = sensor.physicalSensor;
        if (sensorParent == null || sensorParent == EntityRef.NULL) {
            return;
        }

        ConeSensorComponent coneSensor = sensorParent.getComponent(ConeSensorComponent.class);
        if (coneSensor == null) {
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

        Vector3f targetDir = targetPos.sub(sensorPos);
        targetDir.normalize();

        Vector3f sensorDir = loc.getWorldDirection(new Vector3f());

        //get the angle between the 2 directions
        Quaternionf quatAngle = new Quaternionf().rotationTo(sensorDir, targetDir);
        float angle = (float) (Math.toDegrees(quatAngle.angle()));

        if ((angle * 2) > coneSensor.aperture) {
            event.consume();
        }

        //rest gets taken care in VolumeSensingSystem
    }
}
