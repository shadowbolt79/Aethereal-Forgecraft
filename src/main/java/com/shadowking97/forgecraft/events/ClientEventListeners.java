package com.shadowking97.forgecraft.events;

import com.shadowking97.forgecraft.item.material.ItemMaterial;
import com.shadowking97.forgecraft.item.material.ItemMaterialDefinition;
import com.shadowking97.forgecraft.item.material.MaterialStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Shadow Bolt on 7/17/2017.
 */
public class ClientEventListeners {
    @SubscribeEvent
    public void addTooltipInformation(ItemTooltipEvent tooltipEvent)
    {
        ItemMaterialDefinition itemMaterialDefinition = MaterialStore.INSTANCE.getMaterialDefinitionForItem(tooltipEvent.getItemStack());
        if(itemMaterialDefinition!=null)
        {
            //TODO: Implement better localization
            if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak))
                tooltipEvent.getToolTip().add(
                        "Aetherial Forge ("+itemMaterialDefinition.getMaterial().getName()+"):\n" +
                                "\nStrength: "+itemMaterialDefinition.getMaterial().getStrength()+
                                "\nDurability: "+itemMaterialDefinition.getMaterial().getDurability()+
                                "\nMaterial Amount: "+itemMaterialDefinition.getValue()*tooltipEvent.getItemStack().stackSize);
            else
                tooltipEvent.getToolTip().add("(Press "+Minecraft.getMinecraft().gameSettings.keyBindSneak.getDisplayName()+ " for Aetherial Forge stats)");
        }
    }
}
