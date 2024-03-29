// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors.volumeSensing;

import com.google.common.collect.Lists;
import org.joml.Quaternionf;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.physics.CollisionGroup;
import org.terasology.engine.physics.StandardCollisionGroup;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.List;

/**
 * A component defining the properties needed to sense another entity. This component is required for all sensing and
 * sense-able entities.
 */
@ForceBlockActive
public class VolumeSensorComponent implements Component<VolumeSensorComponent> {
    /**
     * The {@link CollisionGroup}s that can be detected by this entity.
     */
    @Replicate
    public List<CollisionGroup> detectGroups = Lists.<CollisionGroup>newArrayList(StandardCollisionGroup.DEFAULT,
        StandardCollisionGroup.CHARACTER);

    /**
     * The range, in all axes, from which other entities can be detected by this entity.
     */
    @Replicate
    public float range = 5.0f;

    /**
     * States if the sensor has been attached directly to a sensing entity.
     */
    @Replicate
    public boolean sensorAttachedToEntity = true;

    /**
     * States the distance, rotated by {@link VolumeSensorComponent#directionRot}, away from the attached entity that
     * the sensor should operate from.
     */
    @Replicate
    public float distanceFromEntity = 0.0f;

    /**
     * States the rotation in world-space of the sensor entity offset.
     */
    @Replicate
    public Quaternionf directionRot = new Quaternionf(0, 0, 0, 1);

    /**
     * States the rotation offset in world-space of the sensor entity, relative to the attached entity's world-space
     * rotation.
     */
    @Replicate
    public Quaternionf sensorRotOffset = new Quaternionf(0, 0, 0, 1);

    @Override
    public void copyFrom(VolumeSensorComponent other) {
        this.detectGroups = Lists.newArrayList(other.detectGroups);
        this.range = other.range;
        this.sensorAttachedToEntity = other.sensorAttachedToEntity;
        this.distanceFromEntity = other.distanceFromEntity;
        this.directionRot.set(other.directionRot);
        this.sensorRotOffset.set(other.sensorRotOffset);
    }
}
