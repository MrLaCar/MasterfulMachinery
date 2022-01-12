package com.ticticboooom.mods.mm.block.item;

import com.ticticboooom.mods.mm.data.DataRegistry;
import com.ticticboooom.mods.mm.data.model.ControllerModel;
import com.ticticboooom.mods.mm.setup.MMItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Map;

public class MMItemGroup extends ItemGroup {

    public static final MMItemGroup INSTANCE = new MMItemGroup();

    public MMItemGroup() {
        super("masterfulmachinery");
    }


    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.ACACIA_PLANKS);
    }

    @Override
    public void fill(NonNullList<ItemStack> items) {
        items.addAll(getControllers());
        super.fill(items);
    }

    private NonNullList<ItemStack> getControllers() {
        NonNullList<ItemStack> controllers = NonNullList.create();
        for (Map.Entry<ResourceLocation, ControllerModel> entry : DataRegistry.CONTROLLERS.entrySet()) {
            ItemStack stack = new ItemStack(MMItems.CONTROLLER.get());
            CompoundNBT tag = stack.getOrCreateTag();
            tag.putString("Controller", entry.getValue().id.toString());
            controllers.add(stack);
        }
        return controllers;
    }
}