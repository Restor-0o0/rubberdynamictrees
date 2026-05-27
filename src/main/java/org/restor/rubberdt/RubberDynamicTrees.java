package org.restor.rubberdt;

import com.dtteam.dynamictrees.block.leaves.LeavesProperties;
import com.dtteam.dynamictrees.block.pod.Pod;
import com.dtteam.dynamictrees.block.soil.SoilProperties;
import com.dtteam.dynamictrees.data.GatherDataHelper;
import com.dtteam.dynamictrees.registry.NeoForgeRegistryHandler;
import com.dtteam.dynamictrees.tree.family.Family;
import com.dtteam.dynamictrees.tree.species.Species;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(RubberDynamicTrees.MOD_ID)
public class RubberDynamicTrees {
    public static final String MOD_ID = "rubberdt";

    public RubberDynamicTrees(IEventBus eventBus, ModContainer container) {
        eventBus.addListener(this::gatherData);

        NeoForgeRegistryHandler.setup(MOD_ID, eventBus);
    }

    public void gatherData(final GatherDataEvent event) {
        GatherDataHelper.gatherAllData(MOD_ID, event,
                SoilProperties.REGISTRY,
                Family.REGISTRY,
                Species.REGISTRY,
                LeavesProperties.REGISTRY,
                Pod.REGISTRY
        );
    }

    public static ResourceLocation location (String name){
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

}
