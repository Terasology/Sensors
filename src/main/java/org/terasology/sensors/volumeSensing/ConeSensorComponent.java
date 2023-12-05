// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors.volumeSensing;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * When this component is present on an entity, then it becomes capable of detecting other entities within a fixed cone
 * relative to its forward direction. The size of the cone is determined by its aperture.
 */
public class ConeSensorComponent implements Component<ConeSensorComponent> {
    /**
     * The aperture of the cone, defined in degrees.
     */
    public float aperture = 60.0f;

    @Override
    public void copyFrom(ConeSensorComponent other) {
        this.aperture = other.aperture;
    }
}
