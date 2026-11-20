/**
 * Copyright (c) 2024 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package rpggods.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Client-only helper that must only be class-loaded on the physical client.
 */
public final class ClientRegistryAccess {

    private ClientRegistryAccess() {
    }

    @Nullable
    public static RegistryAccess getRegistryAccess() {
        final Level level = Minecraft.getInstance().level;
        return level != null ? level.registryAccess() : null;
    }
}
