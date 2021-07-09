// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.sensors.volumeSensing;

import org.terasology.gestalt.entitysystem.component.EmptyComponent;

/**
 * This component is used to indicate that an entity is visible to another entity. If this component is not present,
 * then other entities will be unable to sense the attached entity.
 */
public class TargetVisibleComponent extends EmptyComponent<TargetVisibleComponent> {
}
