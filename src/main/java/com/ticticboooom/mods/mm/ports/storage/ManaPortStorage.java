package com.ticticboooom.mods.mm.ports.storage;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ticticboooom.mods.mm.MM;
import com.ticticboooom.mods.mm.block.tile.MachinePortBlockEntity;
import com.ticticboooom.mods.mm.inventory.PortEnergyInventory;
import com.ticticboooom.mods.mm.inventory.botania.PortManaInventory;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.IManaReceiver;

import java.util.ArrayList;
import java.util.List;

public class ManaPortStorage extends PortStorage {
    public static final Codec<ManaPortStorage> CODEC  = RecordCodecBuilder.create(x -> x.group(
            Codec.INT.fieldOf("capacity").forGetter(z -> z.inv.getMaxManaStored())
    ).apply(x, ManaPortStorage::new));
    @Getter
    private final List<IManaReceiver> validPools = new ArrayList<>();

    @Getter
    private final PortManaInventory inv;
    private final LazyOptional<PortManaInventory> invLO;

    public ManaPortStorage(int capacity) {
        this.inv = new PortManaInventory(0, capacity);
        invLO =  LazyOptional.of(() -> this.inv);
    }

    @Override
    public <T> LazyOptional<T> getLO() {
        return invLO.cast();
    }

    @Override
    public <T> boolean validate(Capability<T> cap) {
        return false;
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.putInt("stored", inv.getManaStored());
        return nbt;
    }

    @Override
    public void load(CompoundNBT nbt) {
        if (nbt.contains("stored")) {
            inv.setStored(nbt.getInt("stored"));
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, int left, int top, Screen screen) {
        Minecraft.getInstance().textureManager.bindTexture(new ResourceLocation(MM.ID, "textures/gui/mana_gui.png"));
        screen.blit(stack, left, top, 0, 0,  175, 256);
        int barOffsetX = 175 - 30;
        int barOffsetY = 20;
        screen.blit(stack, left + barOffsetX, top + barOffsetY, 175, 18, 18, 108);
        float amount = 0;
        if (inv.getMaxManaStored() > 0) {
            amount = (float)inv.getManaStored() / inv.getMaxManaStored();
        }
        screen.blit(stack, left + barOffsetX, top + barOffsetY, 193, 18, 18, (int) (108 * amount));
        AbstractGui.drawString(stack, Minecraft.getInstance().fontRenderer, inv.getManaStored() + "Mana", left + 30, top + 60, 0xfefefe);
    }

    @Override
    public void tick(MachinePortBlockEntity tile) {
        if (tile.getWorld().isRemote) {
            return;
        }
        validPools.clear();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos pos = tile.getPos().offset(dir);
            if (tile.getWorld().isBlockLoaded(pos)) {
                TileEntity tileAt = tile.getWorld().getTileEntity(pos);
                if (tileAt instanceof IManaPool && !tileAt.isRemoved()) {
                    IManaReceiver receiver = (IManaReceiver) tileAt;
                    if (!receiver.isFull()) {
                        validPools.add(receiver);
                    }
                }
            }
        }

        System.out.println(this.validPools);
        int tiles = validPools.size();
        if (tiles != 0) {
            int extractableMana = inv.extractMana(Integer.MAX_VALUE, true);
            int extractedMana = 0;
            int filledPools = 0;
            while (extractableMana != 0) {
                for (IManaReceiver pool : validPools) {
                    if (!pool.isFull()) {
                        pool.receiveMana(1);
                        extractableMana--;
                        extractedMana++;
                    }
                    else {
                        filledPools++;
                    }
                }
                if (filledPools == validPools.size()) {
                    break;
                }
            }
            inv.extractMana(extractedMana, false);
        }

        super.tick(tile);
    }
}